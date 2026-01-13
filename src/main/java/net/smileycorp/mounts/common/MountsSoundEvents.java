package net.smileycorp.mounts.common;

import com.google.common.collect.Sets;
import net.minecraft.util.SoundEvent;

import java.util.Set;

public class MountsSoundEvents {
    
    public static final Set<SoundEvent> SOUNDS = Sets.newHashSet();

    public static SoundEvent register(String name) {
        SoundEvent sound = new SoundEvent(Constants.loc(name));
        sound.setRegistryName(name);
        SOUNDS.add(sound);
        return sound;
    }

}
