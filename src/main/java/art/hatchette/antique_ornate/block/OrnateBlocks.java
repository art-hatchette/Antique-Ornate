package art.hatchette.antique_ornate.block;

import art.hatchette.antique_ornate.AntiqueOrnate;
import art.hatchette.antique_ornate.block.custom.ConnectionVariant;
import art.hatchette.antique_ornate.block.custom.SimpleConnectiveBlock;
import art.hatchette.antique_ornate.item.ModItems;
import art.hatchette.antique_ornate.sound.ModSoundTypes;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.SoundType;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredBlock;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class OrnateBlocks {
    public static final DeferredRegister.Blocks BLOCKS =
            DeferredRegister.createBlocks(AntiqueOrnate.MOD_ID);

    public static final DeferredBlock<Block> WRAITHWOOD_PLANKS = registerBlock("wraithwood_planks",
                    () -> new Block(BlockBehaviour.Properties.of()
                    .strength(2F, 3F)
                    .sound(ModSoundTypes.HAUNTED_WOOD)
                    .ignitedByLava()));

    public static final DeferredBlock<Block> DIVINTINE = registerBlock("divintine",
                    () -> new Block(BlockBehaviour.Properties.of()
                    .strength(1.5F, 8F)
                    .sound(SoundType.DEEPSLATE)));

    public static final DeferredBlock<Block> ORNATE_GLASS = registerBlock("ornate_glass",
            () -> new SimpleConnectiveBlock(ConnectionVariant.ALL, BlockBehaviour.Properties.of()
                    .strength(0.3F)
                    .sound(SoundType.GLASS)
                    .noOcclusion()));

    public static final DeferredBlock<Block> FRAMED_BLACK_LACQUER = registerBlock("framed_black_lacquer",
            () -> new SimpleConnectiveBlock(ConnectionVariant.ALL, BlockBehaviour.Properties.of()
                    .strength(0.3F)
                    .sound(SoundType.NETHERITE_BLOCK)));


    public static final DeferredBlock<Block> DIVINTINE_BRICKS = registerBlock("divintine_bricks",
            () -> new SimpleConnectiveBlock(ConnectionVariant.HORIZONTAL, BlockBehaviour.Properties.of()
                    .strength(1.5F, 8F)
                    .sound(SoundType.DEEPSLATE)));

    public static final DeferredBlock<Block> WRAITHWOOD_VERSAILLES_TRIMPARQUET = registerBlock("wraithwood_versailles_trimparquet",
            () -> new SimpleConnectiveBlock(ConnectionVariant.PILLAR, BlockBehaviour.Properties.of()
                    .strength(1.5F, 6F)
                    .sound(ModSoundTypes.HAUNTED_WOOD)));



/* DEBUG STUFFS!!!!!!!
    public static final DeferredBlock<Block> DEBUG_PILLAR = registerBlock("debug_pillar",
            () -> new SimpleConnectiveBlock(ConnectionVariant.PILLAR, BlockBehaviour.Properties.of()
                    .strength(1.5F, 6F)
                    .sound(SoundType.AMETHYST)));

    public static final DeferredBlock<Block> FRAMED_DEBUG = registerBlock("framed_debug",
            () -> new SimpleConnectiveBlock(ConnectionVariant.ALL, BlockBehaviour.Properties.of()
                    .strength(1.5F, 6F)
                    .sound(SoundType.AMETHYST)));
*/

    private static <T extends Block> DeferredBlock<T> registerBlock(String name, Supplier<T> block) {
        DeferredBlock<T> toReturn = BLOCKS.register(name, block);
        registerBlockItem(name, toReturn);
        return toReturn;
    }

    private static <T extends Block> void registerBlockItem(String name, DeferredBlock<T> block) {
        ModItems.ITEMS.register(name, () -> new BlockItem(block.get(), new Item.Properties()));
    }

    public static void register(IEventBus eventBus) {
        BLOCKS.register(eventBus);
    }
}
