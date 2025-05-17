package me.xemor.enchantedbosses.configs;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import me.xemor.configurationdata.ConfigurationData;
import me.xemor.enchantedbosses.EnchantedBosses;
import me.xemor.enchantedbosses.LanguageConfig;
import me.xemor.enchantedbosses.SkillEntity;
import me.xemor.enchantedbosses.damagemodifiers.DamageModifiers;
import me.xemor.enchantedbosses.reward.Rewards;
import me.xemor.enchantedbosses.skills.conditions.BossCondition;
import me.xemor.enchantedbosses.spawning.EggHandler;
import me.xemor.skillslibrary2.Skill;
import me.xemor.skillslibrary2.SkillsLibrary;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.*;
import java.time.LocalTime;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class ConfigHandler {

    private final EnchantedBosses enchantedBosses;
    private final File dataFolder;
    private FileConfiguration config;
    private EggHandler eggHandler;
    private LanguageConfig languageConfig;
    private boolean isPremium;

    public ConfigHandler(EnchantedBosses enchantedBosses) {
        this.enchantedBosses = enchantedBosses;
        enchantedBosses.saveDefaultConfig();
        dataFolder = enchantedBosses.getDataFolder();
        config = enchantedBosses.getConfig();
        handleBossesFolder();
        isPremium = this.enchantedBosses.getResource("premium-checksum.txt") != null;
        enchantedBosses.saveResource("language.yml", false);
        enchantedBosses.saveResource("eggs.yml", false);
        try {
            eggHandler = setupObjectMapper().readValue(new File(dataFolder, "eggs.yml"), EggHandler.class);
            languageConfig = setupObjectMapper().readValue(new File(dataFolder, "language.yml"), LanguageConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public boolean isPremium() {
        return isPremium;
    }

    public void reloadConfigs() {
        enchantedBosses.reloadConfig();
        this.config = enchantedBosses.getConfig();
        SkillsLibrary.getSkillsManager().unregisterAllSkills(enchantedBosses);
        loadBosses();
        EnchantedBosses.getInstance().getSpawnHandler().reload();
        try {
            eggHandler = setupObjectMapper().readValue(new File(dataFolder, "eggs.yml"), EggHandler.class);
            languageConfig = setupObjectMapper().readValue(new File(dataFolder, "language.yml"), LanguageConfig.class);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void handleBossesFolder() {
        File bossesFolder = new File(dataFolder, "entities");
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

    public void loadBosses() {
        ObjectMapper objectMapper = setupObjectMapper();
        Map<String, SkillEntity> nameToSkillEntity = Arrays.stream(new File(EnchantedBosses.getInstance().getDataFolder(), "entities").listFiles())
                .parallel()
                .map((file) -> {
                    try {
                        return objectMapper.readValue(file, SkillEntity.class);
                    } catch (IOException e) {
                        EnchantedBosses.getInstance().getLogger().severe(e.getMessage());
                        return null;
                    }
                })
                .filter(Objects::nonNull)
                .filter((it) -> it.getName() != null)
                .collect(Collectors.toMap(SkillEntity::getName, (skillEntity) -> skillEntity));
        if (nameToSkillEntity.size() > 30 && !isPremium) {
            EnchantedBosses.getInstance().getLogger().severe("You have reached the maximum bosses allowance for the free tier of Enchanted Bosses!");
            EnchantedBosses.getInstance().getLogger().severe("Please buy the full version to have more than 30 bosses!");
            Bukkit.getServer().getOnlinePlayers().forEach((p) -> {
                p.sendMessage("You have reached the maximum bosses allowance for the free tier of Enchanted Bosses!");
                p.sendMessage("Please buy the full version to have more than 30 bosses!");
            });
            return;
        }
        nameToSkillEntity.values().forEach(this::registerSkills);
        enchantedBosses.getBossHandler().registerBosses(nameToSkillEntity);
    }

    public ObjectMapper setupObjectMapper() {
        ObjectMapper objectMapper = ConfigurationData.setupObjectMapperForConfigurationData(new ObjectMapper(new YAMLFactory()));;
        objectMapper = SkillsLibrary.getInstance().setupObjectMapper(objectMapper);
        objectMapper.registerSubtypes(DamageModifiers.getNamedTypes());
        objectMapper.registerSubtypes(Rewards.getNamedTypes());
        return objectMapper;
    }

    private void registerSkills(SkillEntity skillEntity) {
        for (Skill skill : skillEntity.getSkills()) {
            skill.getTriggerData().getConditions().addCondition(new BossCondition(skillEntity.getName()));
            SkillsLibrary.getSkillsManager().registerSkill(skill, enchantedBosses);
        }
    }

    public List<LocalTime> getBossSpawnTimes() {
        return config.getStringList("autospawn.times").stream().map(LocalTime::parse).sorted().collect(Collectors.toList());
    }

    public double getDefaultEntityWeight() {
        return config.getDouble("default_entity_weighting", 10D);
    }

    public EggHandler getEggHandler() {
        return eggHandler;
    }

    public LanguageConfig getLanguageConfig() {
        return languageConfig;
    }
}
