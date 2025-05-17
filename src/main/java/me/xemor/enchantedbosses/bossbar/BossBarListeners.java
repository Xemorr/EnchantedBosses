package me.xemor.enchantedbosses.bossbar;

import me.xemor.enchantedbosses.BossHandler;
import me.xemor.enchantedbosses.EnchantedBosses;
import me.xemor.enchantedbosses.SkillEntity;
import me.xemor.enchantedbosses.events.SkillEntityDeathEvent;
import me.xemor.enchantedbosses.events.SkillEntitySpawnEvent;
import me.xemor.skillslibrary2.Skill;
import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.skillslibrary2.triggers.Trigger;
import net.kyori.adventure.bossbar.BossBar;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.Bukkit;
import org.bukkit.attribute.Attribute;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.world.EntitiesLoadEvent;
import org.bukkit.event.world.EntitiesUnloadEvent;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class BossBarListeners implements Listener {

    @EventHandler
    public void onDamage(EntityDamageEvent e) {
        Entity entity = e.getEntity();
        if (entity instanceof LivingEntity lEntity) {
            BossHandler bossHandler = EnchantedBosses.getInstance().getBossHandler();
            SkillEntity skillEntity = bossHandler.getBoss(entity);
            if (skillEntity == null) {
                return;
            }
            if (!SkillsLibrary.getSkillsManager().getLoopEntities().contains(entity.getUniqueId())) {
                SkillsLibrary.getSkillsManager().addLoopEntity(entity.getUniqueId());
            }
            UUID uuid = lEntity.getUniqueId();
            BossBar bossBar = bossHandler.getBossBar(uuid);
            if (bossBar == null) {
                return;
            }
            bossBar.progress((float) (lEntity.getHealth() / lEntity.getAttribute(Attribute.MAX_HEALTH).getValue()));
        }
    }

    @EventHandler
    public void onDeath(SkillEntityDeathEvent e) {
        UUID uuid = e.getEntity().getUniqueId();
        BossHandler bossHandler = EnchantedBosses.getInstance().getBossHandler();
        Collection<UUID> playerUUIDs = bossHandler.getBossBarPlayers(uuid);
        BossBar bossBar = bossHandler.getBossBar(uuid);
        for (UUID playerUUID : playerUUIDs) {
            Player player = Bukkit.getPlayer(playerUUID);
            if (bossBar != null && player != null) {
                EnchantedBosses.getBukkitAudiences().player(player).hideBossBar(bossBar);
            }
        }
        bossHandler.removeBossBarPlayers(uuid);
        bossHandler.removeBossBar(uuid);
        Entity killer = e.getEntity().getKiller();
        String killerName =  killer == null ? "something" : killer.getName();
        if (!EnchantedBosses.getInstance().getBossHandler().isMinion(e.getEntity())) {
            EnchantedBosses.getBukkitAudiences().all().sendMessage(
                    MiniMessage.miniMessage().deserialize(EnchantedBosses.getInstance().getConfigHandler().getLanguageConfig().getBossDeathBroadcast(),
                            Placeholder.unparsed("boss", e.getSkillEntity().getName()),
                            Placeholder.unparsed("player", killerName),
                            Placeholder.unparsed("boss_name", e.getSkillEntity().getName()),
                            Placeholder.component("boss_coloured_name", e.getSkillEntity().getColouredName()))
            );
        }
    }

    @EventHandler
    public void onSpawn(SkillEntitySpawnEvent e) {
        final Entity entity = e.getEntity();
        Collection<Skill> skills = SkillsLibrary.getSkillsManager().getSkills(Trigger.getTrigger("SPAWN"));
        for (Skill skill : skills) {
            skill.handleEffects(entity);
        }
        BossBar bossBar = EnchantedBosses.getInstance().getBossHandler().getBossBar(entity.getUniqueId());
        if (bossBar == null) {
            return;
        }
        startRunnable(entity, bossBar);
    }

    public void startRunnable(Entity entity, BossBar bossBar) {
        EnchantedBosses.getFoliaHacks().getScheduling().entitySpecificScheduler(entity).runAtFixedRate(
                (task) -> {
                    if (!entity.isValid()) {
                        task.cancel();
                        return;
                    }
                    List<Entity> nearbyEntities = entity.getNearbyEntities(16, 16, 16);
                    UUID bossUUID = entity.getUniqueId();
                    BossHandler bossHandler = EnchantedBosses.getInstance().getBossHandler();
                    for (Entity nearbyEntity : nearbyEntities) {
                        if (nearbyEntity instanceof Player player) {
                            if (bossHandler.bossBarPlayersContains(bossUUID, player.getUniqueId())) continue;
                            bossHandler.addBossBarPlayer(bossUUID, player.getUniqueId());
                            EnchantedBosses.getBukkitAudiences().player(player).showBossBar(bossBar);
                        }
                    }
                    Collection<UUID> bossBarPlayers = bossHandler.getBossBarPlayers(entity.getUniqueId());
                    List<UUID> bossBarPlayersToRemove = new ArrayList<>();
                    for (UUID uuid : bossBarPlayers) {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null) {
                            if (!bossHandler.bossBarPlayersContains(entity.getUniqueId(), uuid)) {
                                continue;
                            }
                            if (!player.getWorld().equals(entity.getWorld()) || player.getLocation().distanceSquared(entity.getLocation()) > 10000) {
                                bossBarPlayersToRemove.add(uuid);
                                EnchantedBosses.getBukkitAudiences().player(player).hideBossBar(bossBar);
                            }
                        }
                    }
                    for (UUID uuid : bossBarPlayersToRemove) {
                        EnchantedBosses.getInstance().getBossHandler().removeBossBarPlayer(bossUUID, uuid);
                    }
                },
                () -> {},
                1L,
                20L
        );
    }

    @EventHandler
    public void onUnload(EntitiesUnloadEvent e) {
        BossHandler bossHandler = EnchantedBosses.getInstance().getBossHandler();
        List<Entity> entities = e.getEntities();
        for (Entity entity : entities) {
            SkillEntity boss = bossHandler.getBoss(entity);
            if (boss != null) {
                SkillsLibrary.getSkillsManager().removeLoopEntity(entity.getUniqueId());
                BossBar bossBar = bossHandler.getBossBar(entity.getUniqueId());
                if (bossBar != null) {
                    Collection<UUID> bossBarPlayers = bossHandler.getBossBarPlayers(entity.getUniqueId());
                    for (UUID uuid : bossBarPlayers) {
                        Player player = Bukkit.getPlayer(uuid);
                        if (player != null) EnchantedBosses.getBukkitAudiences().player(player).hideBossBar(bossBar);
                    }
                    bossHandler.removeBossBar(entity.getUniqueId());
                    bossHandler.removeBossBarPlayers(entity.getUniqueId());
                }
            }
        }
    }

    @EventHandler
    public void onLoad(EntitiesLoadEvent e) {
        BossHandler bossHandler = EnchantedBosses.getInstance().getBossHandler();
        List<Entity> entities = e.getEntities();
        for (Entity entity : entities) {
            SkillEntity boss = bossHandler.getBoss(entity);
            if (boss != null && boss.getBossBar().isEnabled()) {
                SkillsLibrary.getSkillsManager().addLoopEntity(entity.getUniqueId());
                bossHandler.createBossBar(entity.getUniqueId(), boss);
                BossBar bossBar = bossHandler.getBossBar(entity.getUniqueId());
                startRunnable(entity, bossBar);
            }
        }
    }
}
