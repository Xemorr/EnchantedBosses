package me.xemor.enchantedbosses.skills.effects;

import me.xemor.enchantedbosses.BossHandler;
import me.xemor.enchantedbosses.EnchantedBosses;
import me.xemor.enchantedbosses.SkillEntity;
import me.xemor.skillslibrary2.effects.Effect;
import me.xemor.skillslibrary2.effects.EntityEffect;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;

public class Rider extends Effect implements EntityEffect {

    private final String skillEntityToSpawn;

    public Rider(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        skillEntityToSpawn = configurationSection.getString("rider");
    }

    @Override
    public boolean useEffect(Entity jockey) {
        BossHandler bossHandler = EnchantedBosses.getInstance().getBossHandler();
        SkillEntity boss = bossHandler.getBoss(skillEntityToSpawn);
        LivingEntity rider = bossHandler.spawn(boss, jockey.getLocation());
        EnchantedBosses.getInstance().getBossHandler().setAsMinion(rider);
        rider.setPersistent(jockey.isPersistent());
        rider.setRemoveWhenFarAway(!jockey.isPersistent());
        jockey.addPassenger(rider);
        return false;
    }

}
