package art.hatchette.antique_ornate.item;

import art.hatchette.antique_ornate.AntiqueOrnate;
import art.hatchette.antique_ornate.block.OrnateBlocks;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceKey;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    
    public static final ResourceKey<CreativeModeTab> TAB_KEY = ResourceKey.create(Registries.CREATIVE_MODE_TAB, 
            ResourceLocation.fromNamespaceAndPath(AntiqueOrnate.MOD_ID, "antique_ornate_tab"));

    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AntiqueOrnate.MOD_ID);

    public static final Supplier<CreativeModeTab> ANTIQUE_ORNATE_TAB =
            CREATIVE_MODE_TABS.register("antique_ornate_tab", () -> CreativeModeTab.builder()
                    .title(net.minecraft.network.chat.Component.empty()) //set to empty to override the text in the background texture
                    .icon(() -> new ItemStack(ItemStack.EMPTY.getItem())) // using Empty.getItem for an icon because tabs have the icon built in
                    .displayItems((itemDisplayParameters, pOutput) -> {
                        //poutputs found in CreativeModeTabMixin
                    })
                    //section below is for total customization, those above are just items



                    .withSearchBar() //allows the searchbar use inside the inventory
                    .backgroundTexture(ResourceLocation.fromNamespaceAndPath(AntiqueOrnate.MOD_ID, "textures/gui/container/creative_inventory/tab_antique_ornate.png")) //overrides the default container texture
                    .withTabsImage(ResourceLocation.fromNamespaceAndPath(AntiqueOrnate.MOD_ID, "container/creative_inventory/tab")) //overrides the default tab texture
                    .withScrollBarSpriteLocation(ResourceLocation.fromNamespaceAndPath(AntiqueOrnate.MOD_ID, "container/creative_inventory/scroller")) //overrides the default scroller texture
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
