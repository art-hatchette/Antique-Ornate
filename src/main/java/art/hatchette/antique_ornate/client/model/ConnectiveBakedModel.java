package art.hatchette.antique_ornate.client.model;

import art.hatchette.antique_ornate.block.custom.ConnectionVariant;
import art.hatchette.antique_ornate.block.custom.SimpleConnectiveBlock;
import net.minecraft.client.renderer.RenderType;
import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.block.model.ItemOverrides;
import net.minecraft.client.renderer.block.model.ItemTransforms;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.client.resources.model.ModelState;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.util.RandomSource;
import net.minecraft.world.level.BlockAndTintGetter;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.client.ChunkRenderTypeSet;
import net.neoforged.neoforge.client.model.IDynamicBakedModel;
import net.neoforged.neoforge.client.model.data.ModelData;
import net.neoforged.neoforge.client.model.data.ModelProperty;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;

/**
 * The baked model for the connective texture system.
 * Implements IDynamicBakedModel to generate quads dynamically based on neighbor connectivity.
 *
 * Render type strategy:
 *   RenderType.solid()        → background quads only (always opaque, rendered first)
 *   RenderType.cutoutMipped() → overlay quads only (transparent pixels discarded)
 *   null / other              → both passes together (items, destroy overlay, etc.)
 *
 * Segment quads emit on any render type so destroy overlays work correctly.
 */
public class ConnectiveBakedModel implements IDynamicBakedModel {

    public static final ModelProperty<Map<Direction, ConnectiveModelData.FaceConnectionData>> CONNECTION_PROPERTY =
            new ModelProperty<>();

    private static final ChunkRenderTypeSet RENDER_TYPES =
            ChunkRenderTypeSet.of(RenderType.solid(), RenderType.cutoutMipped());

    private final ConnectionVariant variant;
    private final TextureAtlasSprite connectiveSprite;
    private final TextureAtlasSprite simpleSprite;
    private final TextureAtlasSprite capSprite;
    private final TextureAtlasSprite particleSprite;
    private final ModelState modelState;
    private final ItemTransforms transforms;

    public ConnectiveBakedModel(ConnectionVariant variant,
                                TextureAtlasSprite connectiveSprite,
                                TextureAtlasSprite simpleSprite,
                                TextureAtlasSprite capSprite,
                                TextureAtlasSprite particleSprite,
                                ModelState modelState,
                                ItemTransforms transforms) {
        this.variant = variant;
        this.connectiveSprite = connectiveSprite;
        this.simpleSprite = simpleSprite;
        this.capSprite = capSprite;
        this.particleSprite = particleSprite;
        this.modelState = modelState;
        this.transforms = transforms;
    }

    @Override
    public @NotNull ChunkRenderTypeSet getRenderTypes(@NotNull BlockState state,
                                                      @NotNull RandomSource rand,
                                                      @NotNull ModelData data) {
        return RENDER_TYPES;
    }

    @Override
    public @NotNull ItemTransforms getTransforms() {
        return transforms;
    }

    @Override
    public @NotNull ModelData getModelData(@NotNull BlockAndTintGetter level, @NotNull BlockPos pos,
                                           @NotNull BlockState state, @NotNull ModelData modelData) {
        if (state.getBlock() instanceof SimpleConnectiveBlock connectiveBlock) {
            Map<Direction, ConnectiveModelData.FaceConnectionData> connectionData =
                    connectiveBlock.computeConnectionData(level, pos);
            return ModelData.builder()
                    .with(CONNECTION_PROPERTY, connectionData)
                    .build();
        }
        return modelData;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side,
                                             @NotNull RandomSource rand, @NotNull ModelData modelData,
                                             @Nullable RenderType renderType) {
        if (side == null) return Collections.emptyList();

        Map<Direction, ConnectiveModelData.FaceConnectionData> connectionMap =
                modelData.get(CONNECTION_PROPERTY);

        if (connectionMap == null) {
            return generateStandaloneQuads(side, renderType);
        }

        ConnectiveModelData.FaceConnectionData faceData = connectionMap.get(side);
        if (faceData == null) {
            return generateStandaloneQuads(side, renderType);
        }

        if (variant.usesQuarterChunks() && shouldUseCTForFace(side)) {
            // Quarter-chunk faces: strict solid/cutout split only
            if (renderType != null
                    && renderType != RenderType.solid()
                    && renderType != RenderType.cutoutMipped()) {
                return Collections.emptyList();
            }
            return generateQuarterChunkQuads(side, faceData, renderType);
        } else if (variant.usesSegments() && shouldUseCTForFace(side)) {
            // Segments: emit on any render type so destroy overlays work
            return generateSegmentQuads(side, faceData);
        } else {
            // Simple/cap faces: emit on solid or any non-cutout pass
            if (renderType == RenderType.cutoutMipped()) return Collections.emptyList();
            return generateSimpleFaceQuads(side);
        }
    }

    /**
     * solid pass  → background quad for every quadrant
     * cutout pass → overlay quad for non-background quadrants only
     * null        → both (item rendering)
     */
    private List<BakedQuad> generateQuarterChunkQuads(Direction face,
                                                      ConnectiveModelData.FaceConnectionData faceData,
                                                      @Nullable RenderType renderType) {
        if (connectiveSprite == null) return Collections.emptyList();

        List<BakedQuad> quads = new ArrayList<>(8);
        int sheetRows = 4;

        for (ChunkQuadGenerator.Quadrant quadrant : ChunkQuadGenerator.Quadrant.values()) {
            ConnectiveModelData.ChunkType chunkType = getChunkType(faceData, quadrant);

            boolean isBackground = chunkType == ConnectiveModelData.ChunkType.BACKGROUND
                    || chunkType == ConnectiveModelData.ChunkType.FANCY_BACKGROUND;

            // Solid pass: always emit the background quad
            if (isSolidPass(renderType) || renderType == null) {
                ConnectiveModelData.ChunkType bgType = isBackground
                        ? chunkType
                        : ConnectiveModelData.ChunkType.BACKGROUND;
                quads.add(ChunkQuadGenerator.generateChunkQuad(
                        face, quadrant, bgType, connectiveSprite, sheetRows));
            }

            // Cutout pass: emit the overlay quad for non-background quadrants
            if (!isBackground && (isCutoutPass(renderType) || renderType == null)) {
                quads.add(ChunkQuadGenerator.generateChunkQuad(
                        face, quadrant, chunkType, connectiveSprite, sheetRows));
            }
        }

        return quads;
    }

    private ConnectiveModelData.ChunkType getChunkType(ConnectiveModelData.FaceConnectionData faceData,
                                                       ChunkQuadGenerator.Quadrant quadrant) {
        return switch (quadrant) {
            case NW -> faceData.nw();
            case NE -> faceData.ne();
            case SW -> faceData.sw();
            case SE -> faceData.se();
        };
    }

    private List<BakedQuad> generateSegmentQuads(Direction face,
                                                 ConnectiveModelData.FaceConnectionData faceData) {
        if (connectiveSprite == null) return Collections.emptyList();

        ConnectiveModelData.SegmentType segmentType = computeSegmentType(faceData);
        return List.of(ChunkQuadGenerator.generateSegmentQuad(face, segmentType, connectiveSprite));
    }

    private ConnectiveModelData.SegmentType computeSegmentType(
            ConnectiveModelData.FaceConnectionData faceData) {
        boolean connectedAbove = isConnectedVertically(faceData.nw()) || isConnectedVertically(faceData.ne());
        boolean connectedBelow = isConnectedVertically(faceData.sw()) || isConnectedVertically(faceData.se());

        if (connectedAbove && connectedBelow) return ConnectiveModelData.SegmentType.MIDDLE;
        if (connectedAbove) return ConnectiveModelData.SegmentType.UPPER_END;
        if (connectedBelow) return ConnectiveModelData.SegmentType.LOWER_END;
        return ConnectiveModelData.SegmentType.STANDALONE;
    }

    private boolean isConnectedVertically(ConnectiveModelData.ChunkType type) {
        return type == ConnectiveModelData.ChunkType.EDGE_VERTICAL
                || type == ConnectiveModelData.ChunkType.CORNER_INNER
                || type == ConnectiveModelData.ChunkType.BACKGROUND
                || type == ConnectiveModelData.ChunkType.FANCY_BACKGROUND;
    }

    private List<BakedQuad> generateSimpleFaceQuads(Direction face) {
        TextureAtlasSprite sprite = simpleSprite != null ? simpleSprite : connectiveSprite;
        if (sprite == null) return Collections.emptyList();

        if ((variant == ConnectionVariant.PILLAR || variant == ConnectionVariant.BEAM)
                && face.getAxis() == Direction.Axis.Y && capSprite != null) {
            return List.of(ChunkQuadGenerator.generateSimpleQuad(face, capSprite));
        }

        return List.of(ChunkQuadGenerator.generateSimpleQuad(face, sprite));
    }

    /**
     * Standalone quads used for item rendering and fallback.
     * Segments emit their STANDALONE segment type for CT faces, simple quads for cap/non-CT faces.
     */
    private List<BakedQuad> generateStandaloneQuads(Direction face, @Nullable RenderType renderType) {
        if (connectiveSprite == null) return Collections.emptyList();

        if (variant.usesQuarterChunks() && shouldUseCTForFace(face)) {
            if (renderType != null
                    && renderType != RenderType.solid()
                    && renderType != RenderType.cutoutMipped()) {
                return Collections.emptyList();
            }
            ConnectiveModelData.FaceConnectionData standalone = new ConnectiveModelData.FaceConnectionData(
                    ConnectiveModelData.ChunkType.STANDALONE,
                    ConnectiveModelData.ChunkType.STANDALONE,
                    ConnectiveModelData.ChunkType.STANDALONE,
                    ConnectiveModelData.ChunkType.STANDALONE);
            return generateQuarterChunkQuads(face, standalone, renderType);
        } else if (variant.usesSegments() && shouldUseCTForFace(face)) {
            // Emit STANDALONE segment for item/fallback rendering
            return List.of(ChunkQuadGenerator.generateSegmentQuad(
                    face, ConnectiveModelData.SegmentType.STANDALONE, connectiveSprite));
        } else {
            return generateSimpleFaceQuads(face);
        }
    }

    private boolean shouldUseCTForFace(Direction face) {
        return switch (variant) {
            case ALL -> true;
            case HORIZONTAL -> face == Direction.UP || face == Direction.DOWN;
            case VERTICAL -> face.getAxis() != Direction.Axis.Y;
            case PILLAR -> face.getAxis() != Direction.Axis.Y;
            case BEAM -> true;
        };
    }

    private boolean isSolidPass(@Nullable RenderType renderType) {
        return renderType == RenderType.solid();
    }

    private boolean isCutoutPass(@Nullable RenderType renderType) {
        return renderType == RenderType.cutoutMipped();
    }

    @Override public boolean useAmbientOcclusion() { return true; }
    @Override public boolean isGui3d() { return true; }
    @Override public boolean usesBlockLight() { return true; }
    @Override public boolean isCustomRenderer() { return false; }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon() {
        if (particleSprite != null) return particleSprite;
        if (connectiveSprite != null) return connectiveSprite;
        throw new IllegalStateException("ConnectiveBakedModel has no particle sprite!");
    }

    @Override
    public @NotNull TextureAtlasSprite getParticleIcon(@NotNull ModelData data) {
        return getParticleIcon();
    }

    @Override
    public @NotNull ItemOverrides getOverrides() {
        return ItemOverrides.EMPTY;
    }

    @Override
    public @NotNull List<BakedQuad> getQuads(@Nullable BlockState state, @Nullable Direction side,
                                             @NotNull RandomSource rand) {
        if (side == null) return Collections.emptyList();
        return generateStandaloneQuads(side, null);
    }
}