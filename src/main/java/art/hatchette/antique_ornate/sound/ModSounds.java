package art.hatchette.antique_ornate.sound;

import art.hatchette.antique_ornate.AntiqueOrnate;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.sounds.SoundEvent;
import net.neoforged.bus.api.IEventBus;
import net.neoforged.neoforge.registries.DeferredHolder;
import net.neoforged.neoforge.registries.DeferredRegister;

public class ModSounds {
    public static final DeferredRegister<SoundEvent> SOUND_EVENTS =
            DeferredRegister.create(BuiltInRegistries.SOUND_EVENT, AntiqueOrnate.MOD_ID);

    public static final DeferredHolder<SoundEvent, SoundEvent> HAUNTED_WOOD_BREAK = registerSoundEvent("haunted_wood_break");
    public static final DeferredHolder<SoundEvent, SoundEvent> HAUNTED_WOOD_STEP = registerSoundEvent("haunted_wood_step");
    public static final DeferredHolder<SoundEvent, SoundEvent> HAUNTED_WOOD_PLACE = registerSoundEvent("haunted_wood_place");
    public static final DeferredHolder<SoundEvent, SoundEvent> HAUNTED_WOOD_HIT = registerSoundEvent("haunted_wood_hit");
    public static final DeferredHolder<SoundEvent, SoundEvent> HAUNTED_WOOD_FALL = registerSoundEvent("haunted_wood_fall");

    private static DeferredHolder<SoundEvent, SoundEvent> registerSoundEvent(String name) {
        return SOUND_EVENTS.register(name, () -> SoundEvent.createVariableRangeEvent(ResourceLocation.fromNamespaceAndPath(AntiqueOrnate.MOD_ID, name)));
    }

    public static void register(IEventBus eventBus) {
        SOUND_EVENTS.register(eventBus);
    }
}
