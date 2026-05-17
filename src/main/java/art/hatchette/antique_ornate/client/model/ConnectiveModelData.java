package art.hatchette.antique_ornate.client.model;

import net.minecraft.core.Direction;

import java.util.EnumMap;
import java.util.Map;

/**
 * Data classes for the connective texture system.
 */
public class ConnectiveModelData {

    /**
     * Represents the resolved chunk types for a single face's 4 quadrants.
     */
    public record FaceConnectionData(ChunkType nw, ChunkType ne, ChunkType sw, ChunkType se) {

        /**
         * Apply fancy upgrade rules:
         * - All 4 chunks are BACKGROUND → upgrade all to FANCY_BACKGROUND
         * - Exactly 1 chunk is CORNER_OUTER → upgrade that one to FANCY_CORNER
         */
        public FaceConnectionData withFancyUpgrades() {
            if (nw == ChunkType.BACKGROUND && ne == ChunkType.BACKGROUND &&
                    sw == ChunkType.BACKGROUND && se == ChunkType.BACKGROUND) {
                return new FaceConnectionData(
                        ChunkType.FANCY_BACKGROUND, ChunkType.FANCY_BACKGROUND,
                        ChunkType.FANCY_BACKGROUND, ChunkType.FANCY_BACKGROUND);
            }

            int outerCount = 0;
            if (nw == ChunkType.CORNER_OUTER) outerCount++;
            if (ne == ChunkType.CORNER_OUTER) outerCount++;
            if (sw == ChunkType.CORNER_OUTER) outerCount++;
            if (se == ChunkType.CORNER_OUTER) outerCount++;

            if (outerCount == 1) {
                return new FaceConnectionData(
                        nw == ChunkType.CORNER_OUTER ? ChunkType.FANCY_CORNER : nw,
                        ne == ChunkType.CORNER_OUTER ? ChunkType.FANCY_CORNER : ne,
                        sw == ChunkType.CORNER_OUTER ? ChunkType.FANCY_CORNER : sw,
                        se == ChunkType.CORNER_OUTER ? ChunkType.FANCY_CORNER : se);
            }

            return this;
        }
    }

    /**
     * The resolved type for a single 8x8 chunk quadrant.
     * Sprite sheet layout (32x64, 2 columns x 4 rows):
     *   [0] Corner Outer    [1] Corner Inner
     *   [2] Edge Horizontal [3] Edge Vertical
     *   [4] Background Def  [5] Background Fancy
     *   [6] Corner Fancy    [7] Standalone
     */
    public enum ChunkType {
        CORNER_OUTER,
        CORNER_INNER,
        EDGE_HORIZONTAL,
        EDGE_VERTICAL,
        BACKGROUND,
        FANCY_BACKGROUND,
        FANCY_CORNER,
        STANDALONE;

        public int getSpriteIndex() {
            return switch (this) {
                case CORNER_OUTER -> 0;
                case CORNER_INNER -> 1;
                case EDGE_HORIZONTAL -> 2;
                case EDGE_VERTICAL -> 3;
                case BACKGROUND -> 4;
                case FANCY_BACKGROUND -> 5;
                case FANCY_CORNER -> 6;
                case STANDALONE -> 7;
            };
        }
    }

    /**
     * Segment types for PILLAR/BEAM variants (full-face, no quarter-chunks).
     *
     * Sprite sheet layout (32x32, 2 columns x 2 rows):
     *   [0] Standalone  [1] Lower End
     *   [2] Upper End   [3] Middle
     */
    public enum SegmentType {
        STANDALONE,
        LOWER_END,
        UPPER_END,
        MIDDLE;

        public int getSpriteIndex() {
            return switch (this) {
                case STANDALONE -> 0;
                case LOWER_END -> 1;
                case UPPER_END -> 2;
                case MIDDLE -> 3;
            };
        }
    }

    /**
     * Creates an all-standalone connection data map.
     */
    public static Map<Direction, FaceConnectionData> createStandalone() {
        Map<Direction, FaceConnectionData> map = new EnumMap<>(Direction.class);
        FaceConnectionData standalone = new FaceConnectionData(
                ChunkType.STANDALONE, ChunkType.STANDALONE,
                ChunkType.STANDALONE, ChunkType.STANDALONE);
        for (Direction dir : Direction.values()) {
            map.put(dir, standalone);
        }
        return map;
    }
}
