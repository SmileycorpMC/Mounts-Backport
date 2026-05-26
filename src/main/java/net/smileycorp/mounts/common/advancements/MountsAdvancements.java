package net.smileycorp.mounts.common.advancements;

import net.minecraft.advancements.CriteriaTriggers;

public class MountsAdvancements {

    public static final ChargePierceEntitiesCriterionTrigger CHARGE_PIERCE_ENTITIES = new ChargePierceEntitiesCriterionTrigger();

    public static void registerCriterionTriggers() {
        CriteriaTriggers.register(CHARGE_PIERCE_ENTITIES);
    }

}
