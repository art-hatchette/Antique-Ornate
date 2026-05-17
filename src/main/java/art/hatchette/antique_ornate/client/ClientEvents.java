package art.hatchette.antique_ornate.client;

import art.hatchette.antique_ornate.AntiqueOrnate;
import art.hatchette.antique_ornate.client.model.ConnectiveModelLoader;
import net.minecraft.resources.ResourceLocation;
import net.neoforged.api.distmarker.Dist;
import net.neoforged.bus.api.SubscribeEvent;
import net.neoforged.fml.common.EventBusSubscriber;
import net.neoforged.neoforge.client.event.ModelEvent;

@EventBusSubscriber(modid = AntiqueOrnate.MOD_ID, value = Dist.CLIENT, bus = EventBusSubscriber.Bus.MOD)
public class ClientEvents {

    @SubscribeEvent
    public static void registerGeometryLoaders(ModelEvent.RegisterGeometryLoaders event) {
        event.register(ResourceLocation.fromNamespaceAndPath(AntiqueOrnate.MOD_ID, "connective"),
                new ConnectiveModelLoader());
    }
}
