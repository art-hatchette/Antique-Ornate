package art.hatchette.antique_ornate.block.custom;

import art.hatchette.antique_ornate.client.model.ConnectiveModelData;
import art.hatchette.antique_ornate.client.model.ConnectiveModelData.ChunkType;
import art.hatchette.antique_ornate.client.model.ConnectiveModelData.FaceConnectionData;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.BlockGetter;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.BlockState;
import net.neoforged.neoforge.common.extensions.IBlockExtension;

import java.util.EnumMap;
import java.util.Map;

/**
 * A block that uses the Hatchette connective texture system.
 * Each face is divided into 4 quadrants (NW, NE, SW, SE) that independently
 * resolve their texture based on surrounding neighbors.
 */
public class SimpleConnectiveBlock extends Block {

    private final ConnectionVariant variant;

    public SimpleConnectiveBlock(ConnectionVariant variant, Properties properties) {
        super(properties);
        this.variant = variant;
    }

    public ConnectionVariant getVariant() {
        return variant;
    }

    /**
     * Determines if this block connects to the neighbor at the given position.
     * Override this to customize connection logic (e.g., connect to a group of blocks).
     */
    public boolean connectsTo(BlockGetter level, BlockPos thisPos, BlockPos neighborPos) {
        BlockState neighbor = level.getBlockState(neighborPos);
        return neighbor.getBlock() == this;
    }


    /**
     * Computes the full connection data for this block position.
     * Called by the baked model to determine quad generation.
     */
    public Map<Direction, FaceConnectionData> computeConnectionData(BlockGetter level, BlockPos pos) {
        Map<Direction, FaceConnectionData> faceData = new EnumMap<>(Direction.class);

        for (Direction face : Direction.values()) {
            if (shouldUseCTForFace(face)) {
                FaceConnectionData data = computeFaceData(level, pos, face);
                faceData.put(face, data.withFancyUpgrades());
            } else {
                faceData.put(face, new FaceConnectionData(
                        ChunkType.STANDALONE, ChunkType.STANDALONE,
                        ChunkType.STANDALONE, ChunkType.STANDALONE));
            }
        }

        return faceData;
    }

    /**
     * Whether a given face should use connective texture logic based on the variant.
     */
    public boolean shouldUseCTForFace(Direction face) {
        return switch (variant) {
            case ALL -> true;
            case HORIZONTAL -> face == Direction.UP || face == Direction.DOWN;
            case VERTICAL -> face.getAxis() != Direction.Axis.Y;
            case PILLAR -> face.getAxis() != Direction.Axis.Y;
            case BEAM -> true;
        };
    }

    /**
     * Computes the connection data for a single face using the quarter-chunk system.
     */
    private FaceConnectionData computeFaceData(BlockGetter level, BlockPos pos, Direction face) {
        Direction localUp = getLocalUp(face);
        Direction localRight = getLocalRight(face);
        Direction localDown = localUp.getOpposite();
        Direction localLeft = localRight.getOpposite();

        // Cardinal neighbors
        boolean hasUp = connectsTo(level, pos, pos.relative(localUp));
        boolean hasDown = connectsTo(level, pos, pos.relative(localDown));
        boolean hasLeft = connectsTo(level, pos, pos.relative(localLeft));
        boolean hasRight = connectsTo(level, pos, pos.relative(localRight));

        // Diagonal neighbors
        boolean hasUpLeft = connectsTo(level, pos, pos.relative(localUp).relative(localLeft));
        boolean hasUpRight = connectsTo(level, pos, pos.relative(localUp).relative(localRight));
        boolean hasDownLeft = connectsTo(level, pos, pos.relative(localDown).relative(localLeft));
        boolean hasDownRight = connectsTo(level, pos, pos.relative(localDown).relative(localRight));

        // Resolve each chunk independently
        ChunkType nw = resolveChunk(hasUp, hasLeft, hasUpLeft);
        ChunkType ne = resolveChunk(hasUp, hasRight, hasUpRight);
        ChunkType sw = resolveChunk(hasDown, hasLeft, hasDownLeft);
        ChunkType se = resolveChunk(hasDown, hasRight, hasDownRight);

        return new FaceConnectionData(nw, ne, sw, se);
    }

    /**
     * The core chunk resolution logic.
     *
     * (!cardA && !cardB)              → CORNER_OUTER
     * (cardA && !cardB)               → EDGE_VERTICAL
     * (!cardA && cardB)               → EDGE_HORIZONTAL
     * (cardA && cardB && !diagonal)   → CORNER_INNER
     * (cardA && cardB && diagonal)    → BACKGROUND
     */
    private ChunkType resolveChunk(boolean cardinalA, boolean cardinalB, boolean diagonal) {
        if (!cardinalA && !cardinalB) {
            return ChunkType.CORNER_OUTER;
        } else if (cardinalA && !cardinalB) {
            return ChunkType.EDGE_VERTICAL;
        } else if (!cardinalA) {
            return ChunkType.EDGE_HORIZONTAL;
        } else if (!diagonal) {
            return ChunkType.CORNER_INNER;
        } else {
            return ChunkType.BACKGROUND;
        }
    }

    /**
     * Returns the "local up" direction for a given face.
     */
    public static Direction getLocalUp(Direction face) {
        return switch (face) {
            case UP -> Direction.NORTH;
            case DOWN -> Direction.SOUTH;
            case NORTH -> Direction.UP;
            case SOUTH -> Direction.UP;
            case WEST -> Direction.UP;
            case EAST -> Direction.UP;
        };
    }

    /**
     * Returns the "local right" direction for a given face.
     */
    public static Direction getLocalRight(Direction face) {
        return switch (face) {
            case UP -> Direction.EAST;
            case DOWN -> Direction.EAST;
            case NORTH -> Direction.WEST;
            case SOUTH -> Direction.EAST;
            case WEST -> Direction.SOUTH;
            case EAST -> Direction.NORTH;
        };
    }
}
