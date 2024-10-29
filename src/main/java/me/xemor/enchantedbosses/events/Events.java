package me.xemor.enchantedbosses.events;

import me.xemor.enchantedbosses.EnchantedBosses;
import me.xemor.enchantedbosses.SkillEntity;
import me.xemor.enchantedbosses.reward.Reward;
import me.xemor.skillslibrary2.SkillsLibrary;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import java.util.Comparator;

public class Events implements Listener {

    @EventHandler
    public void onDeath(EntityDeathEvent e) {
        LivingEntity entity = e.getEntity();
        SkillEntity boss = EnchantedBosses.getInstance().getBossHandler().getBoss(entity);
        if (boss != null) {
            Player killer = entity.getKiller();
            if (killer == null) {
                killer = (Player) entity.getNearbyEntities(30, 30, 30).stream().filter((entity1) -> entity1 instanceof Player)
                        .min(Comparator.comparingDouble(entity1 -> entity1.getLocation().distanceSquared(entity.getLocation())))
                        .orElse(null);
            }
            SkillEntityDeathEvent skillEntityDeathEvent = new SkillEntityDeathEvent(entity, boss, killer);
            SkillsLibrary.getSkillsManager().removeLoopEntity(entity.getUniqueId());
            Bukkit.getServer().getPluginManager().callEvent(skillEntityDeathEvent);
            e.getDrops().clear();
            Reward reward = boss.getReward();
            if (reward != null) {
                reward.giveRewards(entity, killer);
            }
        }
    }

}
