package me.xemor.enchantedbosses;

import me.xemor.configurationdata.entity.EntityData;
import me.xemor.enchantedbosses.bossbar.BossBarData;
import me.xemor.enchantedbosses.damagemodifiers.BlankDamageModifier;
import me.xemor.enchantedbosses.damagemodifiers.DamageModifier;
import me.xemor.enchantedbosses.damagemodifiers.DamageModifiers;
import me.xemor.enchantedbosses.reward.Reward;
import me.xemor.enchantedbosses.reward.Rewards;
import me.xemor.enchantedbosses.skills.conditions.BossCondition;
import me.xemor.enchantedbosses.spawning.EggHandler;
import me.xemor.enchantedbosses.spawning.SpawnData;
import me.xemor.enchantedbosses.spawning.SpawnEgg;
import me.xemor.skillslibrary2.Skill;
import me.xemor.skillslibrary2.SkillsLibrary;
import me.xemor.skillslibrary2.conditions.Conditions;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.gradle.api.internal.artifacts.transform.InputArtifactDependenciesAnnotationHandler;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.time.LocalTime;
import java.util.*;
import java.util.logging.Level;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigHandler {

    private final EnchantedBosses enchantedBosses;
    private final File dataFolder;
    private FileConfiguration config;
    private File bossesFolder;

    private boolean isPremium;

    public ConfigHandler(EnchantedBosses enchantedBosses) {
        this.enchantedBosses = enchantedBosses;
        enchantedBosses.saveDefaultConfig();
        dataFolder = enchantedBosses.getDataFolder();
        config = enchantedBosses.getConfig();
        handleBossesFolder();
        isPremium = this.enchantedBosses.getResource("premium-checksum.txt") != null;
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void reloadConfigs() {
        enchantedBosses.reloadConfig();
        this.config = enchantedBosses.getConfig();
        SkillsLibrary.getSkillsManager().unregisterAllSkills(enchantedBosses);
        loadBosses(EnchantedBosses.getInstance().getBossHandler());
        EnchantedBosses.getInstance().getSpawnHandler().reload();
        loadEggs();
    }

    public void handleBossesFolder() {
        bossesFolder = new File(dataFolder, "entities");
        if (bossesFolder.mkdir()) { //if the folder is generated, then add all the bosses in.
            try {
                URI entities;
                try {
                    entities = getClass().getClassLoader().getResource("entities").toURI();
                } catch (URISyntaxException e) {
                    e.printStackTrace();
                    return;
                }
                Path myPath;
                if (entities.getScheme().equals("jar")) {
                    FileSystem fileSystem = FileSystems.newFileSystem(entities, Collections.emptyMap());
                    myPath = fileSystem.getPath("entities");
                } else {
                    myPath = Paths.get(entities);
                }
                Stream<Path> walk = Files.walk(myPath, 1);
                Iterator<Path> it = walk.iterator();
                it.next();
                while (it.hasNext()){
                    String path = it.next().toString();
                    if (path.charAt(0) == '/') {
                        path = path.substring(1);
                    }
                    enchantedBosses.saveResource(path, false);
                }
                walk.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public List<ConfigurationSection> getBossesConfigurationSection() {
        List<ConfigurationSection> sections = new ArrayList<>();
        List<File> files = new ArrayList<>();
        File[] notDeepFiles = bossesFolder.listFiles();
        for (File file : notDeepFiles) {
            if (file.isDirectory()) {
                files.addAll(Arrays.asList(file.listFiles()));
            }
            else {
                files.add(file);
            }
        }
        for (File file : files) {
            YamlConfiguration yamlConfiguration = YamlConfiguration.loadConfiguration(file);
            Map<String, Object> values = yamlConfiguration.getValues(false);
            for (Object yamlObject : values.values()) {
                if (yamlObject instanceof ConfigurationSection) {
                    sections.add((ConfigurationSection) yamlObject);
                }
            }
        }
        return sections;
    }
    
    public void loadEggs() {
        EggHandler eggHandler = EnchantedBosses.getInstance().getEggHandler();
        eggHandler.clearRegisteredEggs();
        ConfigurationSection spawneggs = config.getConfigurationSection("spawneggs");
        for (Map.Entry<String, Object> item : spawneggs.getValues(false).entrySet()) {
            if (item.getValue() instanceof ConfigurationSection spawnEgg) {
                eggHandler.registerEgg(new SpawnEgg(item.getKey(), spawnEgg));
            }
        }
    }

    public void loadBosses(BossHandler bossHandler) {
        List<ConfigurationSection> sections = getBossesConfigurationSection();
        HashMap<String, SkillEntity> nameToBoss = new HashMap<>();
        int count = 0;
        for (ConfigurationSection bossSection : sections) {
            count++;
            if (count > 4 && !isPremium) {
                EnchantedBosses.getInstance().getLogger().severe("You have reached the maximum bosses allowance for the free tier of Enchanted Bosses!");
                EnchantedBosses.getInstance().getLogger().severe("Please buy the full version to have more than 4 bosses!");
                Bukkit.getServer().getOnlinePlayers().forEach((p) -> {
                    p.sendMessage("You have reached the maximum bosses allowance for the free tier of Enchanted Bosses!");
                    p.sendMessage("Please buy the full version to have more than 4 bosses!");
                });
                break;
            }
            String bossName = bossSection.getName();
            Component colouredBossName = MiniMessage.miniMessage().deserialize(bossSection.getString("colouredName", bossName));
            String bossDescription = bossSection.getString("description", bossName + " description");
            ConfigurationSection entitySection = bossSection.getConfigurationSection("entity");
            EntityData entityData;
            if (entitySection == null) entityData = EntityData.create(EntityType.ZOMBIE);
            else entityData = EntityData.create(entitySection);
            SkillEntity skillEntity = new SkillEntity(bossName, colouredBossName, bossDescription, entityData);
            loadSkills(bossSection, skillEntity);
            skillEntity.setReward(loadReward(bossSection));
            skillEntity.setBossBarData(loadBossBar(bossSection));
            skillEntity.setAutoSpawnData(loadAutoSpawn(bossSection));
            skillEntity.setDamageModifier(loadDamageModifier(bossSection));
            nameToBoss.put(bossName, skillEntity);
        }
        bossHandler.registerBosses(nameToBoss);
    }

    private Reward loadReward(ConfigurationSection bossSection) {
        ConfigurationSection rewardsSection = bossSection.getConfigurationSection("rewards");
        if (rewardsSection == null) return null;
        int rewardType = Rewards.getReward(rewardsSection.getString("type"));
        return Reward.create(rewardType, rewardsSection);
    }

    private BossBarData loadBossBar(ConfigurationSection bossSection) {
        ConfigurationSection bossBar = bossSection.getConfigurationSection("bossbar");
        return new BossBarData(bossBar);
    }

    private SpawnData loadAutoSpawn(ConfigurationSection bossSection) {
        ConfigurationSection autospawn = bossSection.getConfigurationSection("autospawn");
        if (autospawn == null) {
            return null;
        }
        return new SpawnData(autospawn);
    }

    private DamageModifier loadDamageModifier(ConfigurationSection bossSection) {
        ConfigurationSection damageModifierSection = bossSection.getConfigurationSection("damagemodifier");
        if (damageModifierSection == null) {
            return new BlankDamageModifier(null);
        }
        int type = DamageModifiers.getDamageModifier(damageModifierSection.getString("type", "BLANK"));
        return DamageModifier.create(type, damageModifierSection);
    }

    private void loadSkills(ConfigurationSection bossSection, SkillEntity skillEntity) {
        ConfigurationSection skillsSection = bossSection.getConfigurationSection("skills");
        if (skillsSection == null) return;
        for (Object value : skillsSection.getValues(false).values()) {
            if (value instanceof ConfigurationSection skillSection) {
                Skill skill = new Skill(skillSection);
                skill.getTriggerData().getConditions().addCondition(new BossCondition(Conditions.getCondition("BOSS"), Collections.singletonList(skillEntity.getName())));
                SkillsLibrary.getSkillsManager().registerSkill(skill, enchantedBosses);
            }
            else {
                Bukkit.getLogger().log(Level.SEVERE, skillEntity.getName() + " has encountered an invalid effect at " + skillsSection.getCurrentPath());
            }
        }
    }

    public String getBossSpawnMessage() {
        return config.getString("language.boss_spawned", "A <name> has spawned at <x>, <y>, <z> in world <world>!");
    }

    public String getBossTimeMessage() {
        return config.getString("language.boss_time", "A boss will spawn in <hour> hours, <minutes> minutes and <seconds> seconds!");
    }

    public String getNoBossTimeMessage() {
        return config.getString("language.no_boss_time", "Bosses are not currently spawning on a timer!");
    }

    public String getBossSpawnFailMessage() {
        return config.getString("language.boss_spawn_fail", "*CRACK* The boss egg failed for some reason!");
    }

    public String getBossDeathMessage() {
        return config.getString("language.boss_death_broadcast", "A <boss> has been killed by <player>");
    }

    public String getInvalidBossMessage() {
        return config.getString("language.invalid_boss", "<dark_red>You have entered an invalid boss name!");
    }

    public String getInvalidPlayerMessage() {
        return config.getString("language.invalid_player", "<dark_red>You have entered an invalid player name!");
    }

    public String getInvalidLocationMessage() {
        return config.getString("language.invalid_location", "<dark_red>You have entered an invalid location");
    }

    public String getNoPermissionMessage() {
        return config.getString("language.no_permission", "<dark_red>You do not have permission to do this!");
    }

    public List<LocalTime> getBossSpawnTimes() {
        return config.getStringList("autospawn.times").stream().map(LocalTime::parse).sorted().collect(Collectors.toList());
    }

    public double getDefaultEntityWeight() {
        return config.getDouble("default_entity_weighting", 10D);
    }

    public void setTextConvertComplete() {
        config.set("textconvert",true);
    }

    public boolean getTextConvertStatus(){
        return config.getBoolean("textconvert",false);
    }
}
