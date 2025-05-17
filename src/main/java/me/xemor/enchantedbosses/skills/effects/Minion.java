package me.xemor.enchantedbosses.skills.effects;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.enchantedbosses.EnchantedBosses;
import me.xemor.enchantedbosses.SkillEntity;
import me.xemor.skillslibrary2.effects.Effect;
import me.xemor.skillslibrary2.effects.EntityEffect;
import me.xemor.skillslibrary2.effects.TargetEffect;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.jetbrains.annotations.Nullable;

public class Minion extends Effect implements EntityEffect, TargetEffect {

    @JsonProperty
    private String spawn;
    @JsonPropertyWithDefault
    private int amount = 1;
    @JsonPropertyWithDefault
    private boolean spawnOnTarget = true;

    public void spawnMinions(Location location, @Nullable LivingEntity target) {
        SkillEntity boss = EnchantedBosses.getInstance().getBossHandler().getBoss(spawn);
        if (boss == null) {
            Bukkit.getLogger().warning(spawn + " is an invalid boss! Your minion skill needs correcting!");
            return;
        }
        for (int i = 0; i < amount; i++) {
            LivingEntity entity = EnchantedBosses.getInstance().getBossHandler().spawn(boss, location);
            EnchantedBosses.getInstance().getBossHandler().setAsMinion(entity);
            entity.setPersistent(false);
            entity.setRemoveWhenFarAway(true);
            if (entity instanceof Creature creature && target != null) {
                creature.setTarget(target);
            }
        }
    }

    @Override
    public void useEffect(Execution execution, Entity entity) {
        spawnMinions(entity.getLocation(), null);
    }

    @Override
    public void useEffect(Execution execution, Entity entity, Entity target) {
        Location location;
        if (spawnOnTarget) location = target.getLocation();
        else location = entity.getLocation();

        if (target instanceof LivingEntity) spawnMinions(location, (LivingEntity) target);
        else spawnMinions(location, null);
    }
}
