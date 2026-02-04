package net.smileycorp.mounts.common;

import com.google.common.collect.Sets;
import net.minecraft.util.SoundEvent;

import java.util.Set;

public class MountsSoundEvents {
    
    public static final Set<SoundEvent> SOUNDS = Sets.newHashSet();

    public static final SoundEvent CAMEL_AMBIENT = register("entity.camel.ambient");
    public static final SoundEvent CAMEL_DASH = register("entity.camel.dash");
    public static final SoundEvent CAMEL_DASH_READY = register("entity.camel.dash_ready");
    public static final SoundEvent CAMEL_DEATH = register("entity.camel.death");
    public static final SoundEvent CAMEL_EAT = register("entity.camel.eat");
    public static final SoundEvent CAMEL_HURT = register("entity.camel.hurt");
    public static final SoundEvent CAMEL_SIT = register("entity.camel.sit");
    public static final SoundEvent CAMEL_STAND = register("entity.camel.stand");
    public static final SoundEvent CAMEL_STEP = register("entity.camel.step");
    public static final SoundEvent CAMEL_STEP_SAND = register("entity.camel.step_sand");

    public static final SoundEvent ITEM_SADDLE_CAMEL_EQUIP = register("item.saddle.camel_equip");
    public static final SoundEvent ITEM_SADDLE_CAMEL_UNEQUIP = register("item.saddle.camel_unequip");
    public static final SoundEvent ITEM_SPEAR_ATTACK = register("item.spear.attack");
    public static final SoundEvent ITEM_SPEAR_HIT = register("item.spear.hit");
    public static final SoundEvent ITEM_SPEAR_USE = register("item.spear.use");

    public static SoundEvent register(String name) {
        SoundEvent sound = new SoundEvent(Constants.loc(name));
        sound.setRegistryName(name);
        SOUNDS.add(sound);
        return sound;
    }

}
