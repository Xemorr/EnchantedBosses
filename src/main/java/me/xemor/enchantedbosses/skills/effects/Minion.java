package me.xemor.enchantedbosses.skills.effects;

import me.xemor.enchantedbosses.EnchantedBosses;
import me.xemor.enchantedbosses.SkillEntity;
import me.xemor.skillslibrary2.effects.Effect;
import me.xemor.skillslibrary2.effects.EntityEffect;
import me.xemor.skillslibrary2.effects.TargetEffect;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.*;
import org.jetbrains.annotations.Nullable;

public class Minion extends Effect implements EntityEffect, TargetEffect {

    private final String skillEntityToSpawn;
    private final int amount;
    private final boolean spawnOnTarget;
    private String location;

    public Minion(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        location = configurationSection.getCurrentPath();
        amount = configurationSection.getInt("amount", 1);
        skillEntityToSpawn = configurationSection.getString("spawn");
        spawnOnTarget = configurationSection.getBoolean("spawnOnTarget", true);
    }

    public void spawnMinions(Location location, @Nullable LivingEntity target) {
        SkillEntity boss = EnchantedBosses.getInstance().getBossHandler().getBoss(skillEntityToSpawn);
        if (boss == null) {
            Bukkit.getLogger().warning(skillEntityToSpawn + " is an invalid boss! Your minion skill needs correcting! " + location);
            return;
        }
        for (int i = 0; i < amount; i++) {
            LivingEntity entity = EnchantedBosses.getInstance().getBossHandler().spawn(boss, location);
            EnchantedBosses.getInstance().getBossHandler().setAsMinion(entity);
            entity.setPersistent(false);
            entity.setRemoveWhenFarAway(true);
            if (entity instanceof Creature && target != null) {
                ((Creature) entity).setTarget(target);
            }
        }
    }

    @Override
    public boolean useEffect(Entity entity) {
        spawnMinions(entity.getLocation(), null);
        return false;
    }

    @Override
    public boolean useEffect(Entity boss, Entity target) {
        Location location;
        if (spawnOnTarget) location = target.getLocation();
        else location = boss.getLocation();
        if (target instanceof LivingEntity) {
            spawnMinions(location, (LivingEntity) target);
        }
        else spawnMinions(location, null);
        return false;
    }
}
