package art.hatchette.antique_ornate.block.custom;

/**
 * Defines how a connective block applies connected textures to its faces.
 *
 * ALL        - All 6 faces use quarter-chunk CT logic (32x64 sprite sheet)
 * HORIZONTAL - Top and Bottom use CT, sides use a simple texture
 * VERTICAL   - Side faces use CT, top and bottom use a simple texture
 * PILLAR     - Sides use full-face vertical CT (32x32), top/bottom use a cap texture
 * BEAM       - Same as PILLAR but axis-rotatable (like logs), caps on perpendicular faces
 *
 * Quarter-Chunk Sheet (32×64) — for ALL, HORIZONTAL, VERTICAL
 * ┌────────────────┬────────────────┐
 * │                │                │
 * │  Corner Outer  │  Corner Inner  │
 * │     [0]        │     [1]        │
 * │                │                │
 * ├────────────────┼────────────────┤
 * │                │                │
 * │  Edge Horiz    │  Edge Vertical │
 * │     [2]        │     [3]        │
 * │                │                │
 * ├────────────────┼────────────────┤
 * │                │                │
 * │  Background    │  Fancy BG      │
 * │     [4]        │     [5]        │
 * │                │                │
 * ├────────────────┼────────────────┤
 * │                │                │
 * │  Fancy Corner  │  Standalone    │
 * │     [6]        │     [7]        │
 * │                │                │
 * └────────────────┴────────────────┘
 *         32px wide, 64px tall
 *      Each cell is 16x16 px
 * How sampling works: For each chunk quadrant (NW/NE/SW/SE),
 * the system grabs the corresponding 8×8 corner from the resolved 16×16 sprite:
 *
 * ┌────┬────┐
 * │ NW │ NE │  ← 8x8 each
 * ├────┼────┤
 * │ SW │ SE │
 * └────┴────┘
 *    16x16
 *
 * Segment Sheet (32×32) — for PILLAR, BEAM
 * ┌────────────────┬────────────────┐
 * │                │                │
 * │  Standalone    │  Upper End     │
 * │     [0]        │     [1]        │
 * │                │                │
 * ├────────────────┼────────────────┤
 * │                │                │
 * │  Lower End     │  Middle        │
 * │     [2]        │     [3]        │
 * │                │                │
 * └────────────────┴────────────────┘
 *         32px wide, 32px tall
 *      Each cell is 16x16 pixels
 */
public enum ConnectionVariant {
    ALL,
    HORIZONTAL,
    VERTICAL,
    PILLAR,
    BEAM;

    /**
     * Whether this variant uses the quarter-chunk (8x8) CT system.
     */
    public boolean usesQuarterChunks() {
        return this == ALL || this == HORIZONTAL || this == VERTICAL;
    }

    /**
     * Whether this variant uses full-face segment logic (Standalone/Lower/Upper/Middle).
     */
    public boolean usesSegments() {
        return this == PILLAR || this == BEAM;
    }
}
