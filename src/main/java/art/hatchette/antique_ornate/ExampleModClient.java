package art.hatchette.antique_ornate;

import art.hatchette.antique_ornate.block.OrnateBlocks;
import art.hatchette.antique_ornate.client.model.ConnectiveBakedModel;
import net.minecraft.client.Minecraft;
import net.minecraft.client.resources.model.BakedModel;
import net.minecraft.client.resources.model.ModelResourceLocation;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.ModContainer;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.fml.common.Mod;
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent;
import net.neoforged.neoforge.client.event.ModelEvent;
import net.neoforged.neoforge.client.gui.ConfigurationScreen;
import net.neoforged.neoforge.client.gui.IConfigScreenFactory;

import java.util.ArrayList;
import java.util.Map;

// This class will not load on dedicated servers. Accessing client side code from here is safe.
@Mod(value = AntiqueOrnate.MOD_ID, dist = Dist.CLIENT)
// You can use EventBusSubscriber to automatically register all static methods in the class annotated with @SubscribeEvent
@EventBusSubscriber(modid = AntiqueOrnate.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
@SuppressWarnings("removal")
public class ExampleModClient {
    public ExampleModClient(ModContainer container) {
        // Allows NeoForge to create a config screen for this mod's configs.
        // The config screen is accessed by going to the Mods screen > clicking on your mod > clicking on config.
        // Do not forget to add translations for your config options to the en_us.json file.
        container.registerExtensionPoint(IConfigScreenFactory.class, ConfigurationScreen::new);
    }

    @SubscribeEvent
    static void onClientSetup(FMLClientSetupEvent event) {
        // Some client setup code
        AntiqueOrnate.LOGGER.info("HELLO FROM CLIENT SETUP");
        AntiqueOrnate.LOGGER.info("MINECRAFT NAME >> {}", Minecraft.getInstance().getUser().getName());
    }
}
