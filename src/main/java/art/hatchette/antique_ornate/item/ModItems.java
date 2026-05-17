package art.hatchette.antique_ornate.item;

import art.hatchette.antique_ornate.AntiqueOrnate;
import net.minecraft.world.item.Item;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredItem;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModItems {
    public static final DeferredRegister.Items ITEMS = DeferredRegister.createItems(AntiqueOrnate.MOD_ID);

    public static final DeferredItem<Item> SOUL = ITEMS.registerSimpleItem("soul");
    //starry rok

    public static void register(IEventBus eventBus) {
        ITEMS.register(eventBus);
    }
}
