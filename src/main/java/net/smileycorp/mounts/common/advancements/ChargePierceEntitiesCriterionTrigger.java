package net.smileycorp.mounts.common.advancements;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonObject;
import net.minecraft.advancements.ICriterionTrigger;
import net.minecraft.advancements.PlayerAdvancements;
import net.minecraft.advancements.critereon.TickTrigger;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.util.ResourceLocation;
import net.smileycorp.mounts.common.Constants;

import java.util.Map;
import java.util.Set;

public class ChargePierceEntitiesCriterionTrigger implements ICriterionTrigger<ChargePierceEntitiesCriterionTrigger.Instance> {

    private static final ResourceLocation ID = Constants.loc("charge_pierce_entities");
    private final Map<PlayerAdvancements, Set<Listener<ChargePierceEntitiesCriterionTrigger.Instance>>> listeners = Maps.newHashMap();

    @Override
    public ResourceLocation getId() {
        return ID;
    }

    @Override
    public void addListener(PlayerAdvancements advancements, Listener<ChargePierceEntitiesCriterionTrigger.Instance> listener) {
        Set<Listener<ChargePierceEntitiesCriterionTrigger.Instance>> set = listeners.computeIfAbsent(advancements, k -> Sets.newHashSet());
        set.add(listener);
    }

    @Override
    public void removeListener(PlayerAdvancements advancements, Listener<ChargePierceEntitiesCriterionTrigger.Instance> listener) {
        Set<Listener<Instance>> set = listeners.get(advancements);
        if (set != null) set.remove(listener);
    }

    @Override
    public void removeAllListeners(PlayerAdvancements advancements) {
        Set<Listener<Instance>> set = listeners.get(advancements);
        if (set != null) set.clear();
    }

    public void trigger(EntityPlayerMP player, int count) {
        if (player == null) return;
        PlayerAdvancements advancements = player.getAdvancements();
        Set<Listener<Instance>> set = listeners.get(advancements);
        if (set == null) return;
        Set<Listener<Instance>> passed = Sets.newHashSet();
        set.stream().filter(listener -> listener.getCriterionInstance().count <= count)
                .forEach(passed::add);
        passed.forEach(listener -> listener.grantCriterion(advancements));
    }

    @Override
    public Instance deserializeInstance(JsonObject json, JsonDeserializationContext context) {
        return new ChargePierceEntitiesCriterionTrigger.Instance(json.get("count").getAsInt());
    }

    public static class Instance extends TickTrigger.Instance {

        private final int count;

        public Instance(int count) {
            this.count = count;
        }

        @Override
        public ResourceLocation getId() {
            return ID;
        }

    }

}
