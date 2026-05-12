package art.hatchette.antique_ornate.item;

import art.hatchette.antique_ornate.AntiqueOrnateMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.Items;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AntiqueOrnateMod.MOD_ID);

    public static final Supplier<CreativeModeTab> ANTIQUE_ORNATE_TAB =
            CREATIVE_MODE_TABS.register("antique_ornate_tab", () -> CreativeModeTab.builder()
                    .title(net.minecraft.network.chat.Component.empty()) //set to empty to override the text in the background texture
                    .icon(() -> new ItemStack(Items.AIR))
                    .displayItems((itemDisplayParameters, pOutput) -> {
                        pOutput.accept(ModItems.SOUL.get());
                    }) //section below is for total customization, those above are just items



                    .withSearchBar() //allows the searchbar use inside the inventory
                    .backgroundTexture(ResourceLocation.fromNamespaceAndPath(AntiqueOrnateMod.MOD_ID, "textures/gui/container/creative_inventory/tab_antique_ornate.png")) //overrides the default container texture
                    .withTabsImage(ResourceLocation.fromNamespaceAndPath(AntiqueOrnateMod.MOD_ID, "container/creative_inventory/tab")) //overrides the default tab texture
                    .withScrollBarSpriteLocation(ResourceLocation.fromNamespaceAndPath(AntiqueOrnateMod.MOD_ID, "container/creative_inventory/scroller")) //overrides the default scroller texture
                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
