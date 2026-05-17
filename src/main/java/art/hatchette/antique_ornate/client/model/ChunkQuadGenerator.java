package art.hatchette.antique_ornate.client.model;

import net.minecraft.client.renderer.block.model.BakedQuad;
import net.minecraft.client.renderer.texture.TextureAtlasSprite;
import net.minecraft.core.Direction;
import org.joml.Vector3f;

import java.util.List;

/**
 * Utility for generating quads with UV slicing from sprite sheets.
 *
 * 32x64 sheet layout (quarter-chunk variants):
 *   Col 0              Col 1
 *   [0] Corner Outer   [1] Corner Inner
 *   [2] Edge Horiz     [3] Edge Vertical
 *   [4] Background     [5] Fancy Background
 *   [6] Fancy Corner   [7] Standalone
 *
 * 32x32 sheet layout (segment variants):
 *   [0] Standalone     [1] Lower End
 *   [2] Upper End      [3] Middle
 */
public class ChunkQuadGenerator {

    private static final int SHEET_COLUMNS = 2;
    private static final int SPRITE_SIZE = 16;
    private static final int CHUNK_SIZE = 8;

    public enum Quadrant {
        NW(0, 0),
        NE(1, 0),
        SW(0, 1),
        SE(1, 1);

        public final int xOffset;
        public final int yOffset;

        Quadrant(int xOffset, int yOffset) {
            this.xOffset = xOffset;
            this.yOffset = yOffset;
        }
    }

    /**
     * Generates a single 8x8 quad for a chunk quadrant on a given face.
     *
     * For UP/DOWN faces, the quadrant's position on the face is rotated 90° CCW
     * so that the AO gradient aligns correctly with the face orientation.
     * UV sampling is unaffected — it always reads the correct 8x8 region from the sheet.
     */
    public static BakedQuad generateChunkQuad(Direction face, Quadrant quadrant,
                                              ConnectiveModelData.ChunkType chunkType,
                                              TextureAtlasSprite sprite, int sheetRows) {
        int spriteIndex = chunkType.getSpriteIndex();
        int col = spriteIndex % SHEET_COLUMNS;
        int row = spriteIndex / SHEET_COLUMNS;

        int sheetWidth = SHEET_COLUMNS * SPRITE_SIZE;
        int sheetHeight = sheetRows * SPRITE_SIZE;

        // Pixel coords of the 8x8 chunk within the full sheet
        int pixelX = col * SPRITE_SIZE + quadrant.xOffset * CHUNK_SIZE;
        int pixelY = row * SPRITE_SIZE + quadrant.yOffset * CHUNK_SIZE;

        float u0 = getU(sprite, pixelX, sheetWidth);
        float u1 = getU(sprite, pixelX + CHUNK_SIZE, sheetWidth);
        float v0 = getV(sprite, pixelY, sheetHeight);
        float v1 = getV(sprite, pixelY + CHUNK_SIZE, sheetHeight);

        // Quadrant position on face (each covers half the face)

        float minX = quadrant.xOffset * 0.5f;
        float maxX = minX + 0.5f;
        float minY = (1 - quadrant.yOffset) * 0.5f;
        float maxY = minY + 0.5f;

        return buildFaceQuad(face, minX, minY, maxX, maxY, u0, v0, u1, v1, sprite);
    }

    /**
     * Generates a background quad for a single quadrant, for use in the side==null pass.
     * This renders the BACKGROUND chunk type (or FANCY_BACKGROUND if applicable) as an
     * interior quad, behind all sided quads, so that edge/corner overlay transparency
     * composites correctly without depth conflicts.
     */
    public static void generateBackgroundQuad(List<BakedQuad> quads, Direction face,
                                              Quadrant quadrant,
                                              ConnectiveModelData.ChunkType chunkType,
                                              TextureAtlasSprite sprite, int sheetRows) {
        // Resolve which background variant to use
        ConnectiveModelData.ChunkType bgType = (chunkType == ConnectiveModelData.ChunkType.FANCY_BACKGROUND)
                ? ConnectiveModelData.ChunkType.FANCY_BACKGROUND
                : ConnectiveModelData.ChunkType.BACKGROUND;

        quads.add(generateChunkQuad(face, quadrant, bgType, sprite, sheetRows));
    }

    /**
     * Generates a full-face quad for segment-based variants (PILLAR/BEAM).
     */
    public static BakedQuad generateSegmentQuad(Direction face,
                                                ConnectiveModelData.SegmentType segmentType,
                                                TextureAtlasSprite sprite) {
        int spriteIndex = segmentType.getSpriteIndex();
        int col = spriteIndex % SHEET_COLUMNS;
        int row = spriteIndex / SHEET_COLUMNS;

        float u0 = getU(sprite, col * SPRITE_SIZE, SHEET_COLUMNS * SPRITE_SIZE);
        float u1 = getU(sprite, (col + 1) * SPRITE_SIZE, SHEET_COLUMNS * SPRITE_SIZE);
        float v0 = getV(sprite, row * SPRITE_SIZE, 2 * SPRITE_SIZE);
        float v1 = getV(sprite, (row + 1) * SPRITE_SIZE, 2 * SPRITE_SIZE);

        return buildFaceQuad(face, 0f, 0f, 1f, 1f, u0, v0, u1, v1, sprite);
    }

    /**
     * Generates a simple full-face quad using an entire sprite.
     */
    public static BakedQuad generateSimpleQuad(Direction face, TextureAtlasSprite sprite) {
        return buildFaceQuad(face, 0f, 0f, 1f, 1f,
                sprite.getU0(), sprite.getV0(), sprite.getU1(), sprite.getV1(), sprite);
    }

    private static float getU(TextureAtlasSprite sprite, int pixelX, int totalWidth) {
        float fraction = (float) pixelX / totalWidth;
        return sprite.getU0() + fraction * (sprite.getU1() - sprite.getU0());
    }

    private static float getV(TextureAtlasSprite sprite, int pixelY, int totalHeight) {
        float fraction = (float) pixelY / totalHeight;
        return sprite.getV0() + fraction * (sprite.getV1() - sprite.getV0());
    }

    /**
     * Builds a quad on a specific face with given position bounds and UV coordinates.
     * Routes to the correct UV assignment based on whether the face is horizontal or vertical.
     */
    private static BakedQuad buildFaceQuad(Direction face, float minX, float minY,
                                           float maxX, float maxY,
                                           float u0, float v0, float u1, float v1,
                                           TextureAtlasSprite sprite) {
        Vector3f[] vertices = getFaceVertices(face, minX, minY, maxX, maxY);
        int[] vertexData = new int[32];

        if (face == Direction.UP || face == Direction.DOWN) {
            // Top/bottom faces: vertex order is bottom-left → bottom-right → top-right → top-left
            // UV maps directly: u=left→right, v=bottom→top
            putVertex(vertexData, 0, vertices[0], u0, v0, face);
            putVertex(vertexData, 1, vertices[1], u0, v1, face);
            putVertex(vertexData, 2, vertices[2], u1, v1, face);
            putVertex(vertexData, 3, vertices[3], u1, v0, face);
        } else {
            // Side faces: vertex order is top-left → bottom-left → bottom-right → top-right
            // UV must be rotated to match: top-left=(u0,v0), bottom-left=(u0,v1), etc.
            putVertex(vertexData, 0, vertices[0], u0, v0, face); // top-left
            putVertex(vertexData, 1, vertices[1], u0, v1, face); // bottom-left
            putVertex(vertexData, 2, vertices[2], u1, v1, face); // bottom-right
            putVertex(vertexData, 3, vertices[3], u1, v0, face); // top-right
        }

        return new BakedQuad(vertexData, -1, face, sprite, true);
    }

    /**
     * Returns 4 vertices for a quad on the given face.
     *
     * UP/DOWN use vertex order: bottom-left → bottom-right → top-right → top-left
     * Side faces use vertex order: top-left → bottom-left → bottom-right → top-right
     * (required for correct AO shading gradient direction on side faces)
     */
    private static Vector3f[] getFaceVertices(Direction face, float minX, float minY,
                                              float maxX, float maxY) {
        return switch (face) {
            // UP face: local-up=North(-Z), local-right=East(+X)
            // Viewed from above: CCW = SW→SE→NE→NW
            // face-local bottom(minY) = South(+Z=1), face-local top(maxY) = North(-Z=0)
            case UP -> new Vector3f[]{
                    new Vector3f(minX, 1, 1 - maxY),   // top-left:     west, north
                    new Vector3f(minX, 1, 1 - minY),   // bottom-left:  west, south
                    new Vector3f(maxX, 1, 1 - minY),   // bottom-right: east, south
                    new Vector3f(maxX, 1, 1 - maxY)    // top-right:    east, north
            };
            case DOWN -> new Vector3f[]{
                    new Vector3f(minX, 0, maxY),        // bottom-left:  west, south
                    new Vector3f(minX, 0, minY),        // top-left:     west, north
                    new Vector3f(maxX, 0, minY),        // top-right:    east, north
                    new Vector3f(maxX, 0, maxY)         // bottom-right: east, south
            };
            // NORTH face (Z=0): local-up=Up(+Y), local-right=West(-X... wait)
            // Viewed from north (looking south, from -Z): CCW
            // local-right = West means: face-local right = world West = -X
            // So face-local left(minX) = East(+X), face-local right(maxX) = West(-X... inverted)
            // Actually: getLocalRight(NORTH) = WEST, so "right on the face" = West direction
            // When you look at the north face from outside (from -Z looking toward +Z):
            //   you see East on your left, West on your right
            //   that matches local-right = West
            case NORTH -> new Vector3f[]{
                    new Vector3f(1 - minX, maxY, 0),    // top-left: east side, top
                    new Vector3f(1 - minX, minY, 0),    // bottom-left: east side, bottom
                    new Vector3f(1 - maxX, minY, 0),    // bottom-right: west side, bottom
                    new Vector3f(1 - maxX, maxY, 0)     // top-right: west side, top
            };
            // SOUTH face (Z=1): local-up=Up(+Y), local-right=East(+X)
            // Viewed from south (from +Z looking toward -Z): CCW
            case SOUTH -> new Vector3f[]{
                    new Vector3f(minX, maxY, 1),        // top-left
                    new Vector3f(minX, minY, 1),        // bottom-left
                    new Vector3f(maxX, minY, 1),        // bottom-right
                    new Vector3f(maxX, maxY, 1)         // top-right
            };
            // WEST face (X=0): local-up=Up(+Y), local-right=South(+Z)
            // Viewed from west (from -X looking toward +X): CCW
            case WEST -> new Vector3f[]{
                    new Vector3f(0, maxY, minX),        // top-left
                    new Vector3f(0, minY, minX),        // bottom-left
                    new Vector3f(0, minY, maxX),        // bottom-right
                    new Vector3f(0, maxY, maxX)         // top-right
            };
            // EAST face (X=1): local-up=Up(+Y), local-right=North(-Z)
            // Viewed from east (from +X looking toward -X): CCW
            case EAST -> new Vector3f[]{
                    new Vector3f(1, maxY, 1 - minX),    // top-left
                    new Vector3f(1, minY, 1 - minX),    // bottom-left
                    new Vector3f(1, minY, 1 - maxX),    // bottom-right
                    new Vector3f(1, maxY, 1 - maxX)     // top-right
            };
        };
    }

    /**
     * Packs a vertex: pos(3f), color(1i), uv(2f), light(1i), normal(1i) = 8 ints.
     */
    private static void putVertex(int[] vertexData, int vertexIndex, Vector3f pos,
                                  float u, float v, Direction face) {
        int i = vertexIndex * 8;

        vertexData[i]     = Float.floatToRawIntBits(pos.x());
        vertexData[i + 1] = Float.floatToRawIntBits(pos.y());
        vertexData[i + 2] = Float.floatToRawIntBits(pos.z());
        vertexData[i + 3] = 0xFFFFFFFF; // white, full alpha (ABGR)
        vertexData[i + 4] = Float.floatToRawIntBits(u);
        vertexData[i + 5] = Float.floatToRawIntBits(v);
        vertexData[i + 6] = 0; // lightmap (engine fills)

        int nx = ((byte) (face.getStepX() * 127)) & 0xFF;
        int ny = ((byte) (face.getStepY() * 127)) & 0xFF;
        int nz = ((byte) (face.getStepZ() * 127)) & 0xFF;
        vertexData[i + 7] = nx | (ny << 8) | (nz << 16);
    }
}

