package art.hatchette.antique_ornate.item;

import art.hatchette.antique_ornate.AntiqueOrnateMod;
import net.minecraft.core.registries.Registries;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredRegister;

import java.util.function.Supplier;

public class ModCreativeModeTabs {
    public static final DeferredRegister<CreativeModeTab> CREATIVE_MODE_TABS =
            DeferredRegister.create(Registries.CREATIVE_MODE_TAB, AntiqueOrnateMod.MOD_ID);

    public static final Supplier<CreativeModeTab> ANTIQUE_ORNATE_TAB =
            CREATIVE_MODE_TABS.register("antique_ornate_tab", () -> CreativeModeTab.builder()
                    .title(Component.translatable("creativetab.antique_ornate"))
                    .icon(() -> new ItemStack(ModItems.SOUL.get()))
                    .displayItems((itemDisplayParameters, pOutput) -> {
                        pOutput.accept(ModItems.SOUL.get());
                    })




                    .build());


    public static void register(IEventBus eventBus) {
        CREATIVE_MODE_TABS.register(eventBus);
    }
}
