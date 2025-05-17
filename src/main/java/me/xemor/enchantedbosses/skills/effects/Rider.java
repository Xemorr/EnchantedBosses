package me.xemor.enchantedbosses.skills.effects;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.xemor.enchantedbosses.BossHandler;
import me.xemor.enchantedbosses.EnchantedBosses;
import me.xemor.enchantedbosses.SkillEntity;
import me.xemor.skillslibrary2.effects.Effect;
import me.xemor.skillslibrary2.effects.EntityEffect;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;

public class Rider extends Effect implements EntityEffect {

    @JsonProperty
    private String rider;

    public Rider(int effect, ConfigurationSection configurationSection) {
        super(effect, configurationSection);
        rider = configurationSection.getString("rider");
    }

    @Override
    public void useEffect(Execution execution, Entity jockey) {
        BossHandler bossHandler = EnchantedBosses.getInstance().getBossHandler();
        SkillEntity boss = bossHandler.getBoss(rider);
        LivingEntity rider = bossHandler.spawn(boss, jockey.getLocation());
        EnchantedBosses.getInstance().getBossHandler().setAsMinion(rider);
        rider.setPersistent(jockey.isPersistent());
        rider.setRemoveWhenFarAway(!jockey.isPersistent());
        jockey.addPassenger(rider);
    }
}
