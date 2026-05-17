package art.hatchette.antique_ornate.sound;

import net.minecraft.world.level.block.SoundType;
import net.neoforged.neoforge.common.util.DeferredSoundType;

public class ModSoundTypes {
    public static final SoundType HAUNTED_WOOD = new DeferredSoundType(1.0f, 1.0f,
            ModSounds.HAUNTED_WOOD_BREAK,
            ModSounds.HAUNTED_WOOD_STEP,
            ModSounds.HAUNTED_WOOD_PLACE,
            ModSounds.HAUNTED_WOOD_HIT,
            ModSounds.HAUNTED_WOOD_FALL);
}
