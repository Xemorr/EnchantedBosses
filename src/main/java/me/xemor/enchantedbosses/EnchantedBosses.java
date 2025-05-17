package me.xemor.enchantedbosses;

import me.xemor.configurationdata.ConfigurationData;
import me.xemor.enchantedbosses.bossbar.BossBarListeners;
import me.xemor.enchantedbosses.commands.BossCommand;
import me.xemor.enchantedbosses.configs.ConfigHandler;
import me.xemor.enchantedbosses.damagemodifiers.DamageChanger;
import me.xemor.enchantedbosses.events.Events;
import me.xemor.enchantedbosses.skills.conditions.BossCondition;
import me.xemor.enchantedbosses.skills.effects.Minion;
import me.xemor.enchantedbosses.skills.effects.Rider;
import me.xemor.enchantedbosses.spawning.SpawnHandler;
import me.xemor.foliahacks.FoliaHacks;
import me.xemor.skillslibrary2.conditions.Conditions;
import me.xemor.skillslibrary2.effects.Effects;
import net.kyori.adventure.platform.bukkit.BukkitAudiences;
import org.bstats.bukkit.Metrics;
import org.bukkit.Bukkit;
import org.bukkit.command.PluginCommand;
import org.bukkit.entity.Entity;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTransformEvent;
import org.bukkit.event.server.ServerLoadEvent;
import org.bukkit.plugin.java.JavaPlugin;
import space.arim.morepaperlib.scheduling.GracefulScheduling;

import java.util.logging.Level;

public final class EnchantedBosses extends JavaPlugin implements Listener {

    private static EnchantedBosses enchantedBosses;
    private BossHandler bossHandler;
    private SpawnHandler spawnHandler;
    private ConfigHandler configHandler;
    private static FoliaHacks foliaHacks;
    private static BukkitAudiences bukkitAudiences;

    @Override
    public void onEnable() {
        EnchantedBosses.foliaHacks = new FoliaHacks(this);
        enchantedBosses = this;
        ConfigurationData.setup(this);
        bukkitAudiences = BukkitAudiences.create(this);
        configHandler = new ConfigHandler(this);
        bossHandler = new BossHandler(this, configHandler);
        handleSkillsLibrary();
        PluginCommand spawnBossCommand = this.getCommand("boss");
        BossCommand bossCommand = new BossCommand(bossHandler);
        spawnBossCommand.setExecutor(bossCommand);
        spawnBossCommand.setTabCompleter(bossCommand);
        this.getServer().getPluginManager().registerEvents(new BossBarListeners(), this);
        this.getServer().getPluginManager().registerEvents(this, this);
        this.getServer().getPluginManager().registerEvents(new Events(), this);
        this.getServer().getPluginManager().registerEvents(new DamageChanger(), this);
    }

    public static BukkitAudiences getBukkitAudiences() {
        return bukkitAudiences;
    }

    public void handleSkillsLibrary() {
        Effects.registerEffect("MINION", Minion.class);
        Effects.registerEffect("RIDER", Rider.class);
        Conditions.register("SKILLENTITY", BossCondition.class);
        Conditions.register("BOSS", BossCondition.class);
    }

    @EventHandler
    public void onLoad(ServerLoadEvent e) {
        configHandler.loadBosses();
        spawnHandler = new SpawnHandler(bossHandler);
        Bukkit.getServer().getPluginManager().registerEvents(spawnHandler, this);
        handleMetrics();
    }

    public void handleMetrics() {
        Metrics metrics = new Metrics(this, 19851);
        if (!metrics.isEnabled()) {
            getLogger().log(Level.WARNING, "You have disabled bstats, this is very sad :(");
        }
        metrics.addCustomChart(new Metrics.SimplePie("enchanted_bosses_premium_usage", () -> configHandler.isPremium() ? "Yes" : "No"));
    }

    @EventHandler
    public void onTransform(EntityTransformEvent e) {
        if (e.getTransformReason() != EntityTransformEvent.TransformReason.SPLIT) {
            BossHandler bossHandler = EnchantedBosses.getInstance().getBossHandler();
            SkillEntity skillEntity = bossHandler.getBoss(e.getEntity());
            if (skillEntity != null) {
                for (Entity entity : e.getTransformedEntities()) {
                    bossHandler.setBoss(skillEntity, entity);
                }
                if (bossHandler.isMinion(e.getEntity())) {
                    for (Entity entity : e.getTransformedEntities()) {
                        bossHandler.setAsMinion(entity);
                    }
                }
            }

        }
    }


    public BossHandler getBossHandler() {
        return bossHandler;
    }

    public static EnchantedBosses getInstance() {
        return enchantedBosses;
    }

    public SpawnHandler getSpawnHandler() {
        return spawnHandler;
    }

    public ConfigHandler getConfigHandler() {
        return configHandler;
    }

    public static FoliaHacks getFoliaHacks() {
        return foliaHacks;
    }

    public static GracefulScheduling getScheduling() {
        return foliaHacks.getScheduling();
    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        spawnHandler.disable();
    }
}
