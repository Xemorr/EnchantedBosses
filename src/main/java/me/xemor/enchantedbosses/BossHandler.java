package me.xemor.enchantedbosses;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import com.google.common.collect.Multimaps;
import me.xemor.configurationdata.entity.EntityData;
import me.xemor.enchantedbosses.bossbar.BossBarData;
import me.xemor.enchantedbosses.configs.ConfigHandler;
import me.xemor.enchantedbosses.events.SkillEntitySpawnEvent;
import me.xemor.skillslibrary2.SkillsLibrary;
import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.NamespacedKey;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.persistence.PersistentDataType;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class BossHandler {

    private Map<String, SkillEntity> nameToBosses = new ConcurrentHashMap<>();
    private final Map<UUID, BossBar> entityToBossBar = new ConcurrentHashMap<>();
    private final Multimap<UUID, UUID> entityToBossBarPlayers = Multimaps.synchronizedMultimap(HashMultimap.create());
    private final ConfigHandler configHandler;
    private final EnchantedBosses enchantedBosses;
    private final NamespacedKey entityType;
    private final NamespacedKey minionKey;


    public BossHandler(EnchantedBosses enchantedBosses, ConfigHandler configHandler) {
        this.configHandler = configHandler;
        this.enchantedBosses = enchantedBosses;
        entityType = new NamespacedKey(enchantedBosses, "entityType");
        minionKey = new NamespacedKey(EnchantedBosses.getInstance(), "minion");
    }

    public void registerBosses(Map<String, SkillEntity> bossMap) {
        nameToBosses = bossMap;
    }

    @Nullable
    public SkillEntity getBoss(String bossName) {
        return nameToBosses.get(bossName);
    }

    @Nullable
    public SkillEntity getBoss(@NotNull Entity entity) {
        return getBoss(entity.getPersistentDataContainer().get(entityType, PersistentDataType.STRING));
    }

    public LivingEntity spawn(SkillEntity skillEntity, Location location) {
        EntityData entityData = skillEntity.getEntityData();
        Entity entity = entityData.spawnEntity(location);
        if (!(entity instanceof LivingEntity livingEntity)) {
            EnchantedBosses.getInstance().getLogger().severe("Entity must be an instance of a LivingEntity to be spawned!");
            return null;
        }
        livingEntity.setPersistent(true);
        livingEntity.setRemoveWhenFarAway(false);
        setBoss(skillEntity, livingEntity);
        createBossBar(livingEntity.getUniqueId(), skillEntity);
        SkillEntitySpawnEvent skillEntitySpawnEvent = new SkillEntitySpawnEvent(livingEntity, skillEntity);
        Bukkit.getServer().getPluginManager().callEvent(skillEntitySpawnEvent);
        SkillsLibrary.getSkillsManager().addLoopEntity(livingEntity.getUniqueId());
        return livingEntity;
    }

    public void setBoss(SkillEntity skillEntity, Entity entity) { //Iterates over all passengers, and passengers of passengers etc to set them as a certain boss.
        entity.getPersistentDataContainer().set(entityType, PersistentDataType.STRING, skillEntity.getName());
        entity.getPassengers().forEach((passenger) -> setBoss(skillEntity, passenger));
    }

    public void createBossBar(UUID uuid, SkillEntity skillEntity) {
        BossBarData bossBarData = skillEntity.getBossBar();
        if (bossBarData != null && bossBarData.isEnabled()) {
           BossBar bossBar = BossBar.bossBar(skillEntity.getColouredName(), 1.0f, bossBarData.getColor(), bossBarData.getStyle(), bossBarData.getFlags());
           entityToBossBar.put(uuid, bossBar);
        }
    }

    public BossBar getBossBar(UUID uuid) {
        return entityToBossBar.get(uuid);
    }

    public void removeBossBar(UUID uuid) {
        entityToBossBar.remove(uuid);
    }

    public Collection<UUID> getBossBarPlayers(UUID uuid) {
        return entityToBossBarPlayers.get(uuid);
    }

    public boolean bossBarPlayersContains(UUID entityUUID, UUID playerUUID) {
        return entityToBossBarPlayers.containsEntry(entityUUID, playerUUID);
    }

    public void removeBossBarPlayer(UUID entityUUID, UUID playerUUID) {
        entityToBossBarPlayers.remove(entityUUID, playerUUID);
    }

    public void removeBossBarPlayers(UUID uuid) {
        entityToBossBarPlayers.removeAll(uuid);
    }

    public void addBossBarPlayer(UUID entityUUID, UUID playerUUID) {
        entityToBossBarPlayers.put(entityUUID, playerUUID);
    }

    public Collection<SkillEntity> getBosses() {
        return nameToBosses.values();
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public EnchantedBosses getEnchantedBosses() {
        return enchantedBosses;
    }

    public boolean isMinion(Entity entity) {
        return entity.getPersistentDataContainer().has(minionKey, PersistentDataType.INTEGER);
    }

    public void setAsMinion(Entity entity) {
        entity.getPersistentDataContainer().set(minionKey, PersistentDataType.INTEGER, 1);
    }
}
