package me.xemor.enchantedbosses.damagemodifiers;

import me.xemor.enchantedbosses.BossHandler;
import me.xemor.enchantedbosses.EnchantedBosses;
import me.xemor.enchantedbosses.SkillEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;

public class DamageChanger implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        BossHandler bossHandler = EnchantedBosses.getInstance().getBossHandler();
        SkillEntity skillEntity = bossHandler.getBoss(e.getEntity());

        if (skillEntity == null) return;

        long numberOfPlayers = e.getEntity().getNearbyEntities(16, 16, 16).stream().filter((entity -> entity instanceof Player)).count();
        e.setDamage(skillEntity.getDamageModifier().modify(e.getDamage(), numberOfPlayers));
    }

}
