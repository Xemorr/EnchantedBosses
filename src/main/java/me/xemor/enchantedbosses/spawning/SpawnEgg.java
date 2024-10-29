package me.xemor.enchantedbosses.spawning;

import me.xemor.configurationdata.ItemStackData;
import me.xemor.enchantedbosses.EnchantedBosses;
import me.xemor.enchantedbosses.SkillEntity;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class SpawnEgg {

    private final String name;
    private final List<SkillEntity> skillEntities;
    private final ItemStack itemStack;

    public SpawnEgg(String name, ConfigurationSection configurationSection) {
        this.name = name;
        skillEntities = configurationSection.getStringList("bosses").stream().map((it) -> EnchantedBosses.getInstance().getBossHandler().getBoss(it)).collect(Collectors.toList());
        ConfigurationSection itemSection = configurationSection.getConfigurationSection("item");
        if (itemSection == null) {
            EnchantedBosses.getInstance().getLogger().severe("You have forgotten to specify an item for a SpawnEgg! " + configurationSection.getCurrentPath() + ".item");
            itemStack = new ItemStack(Material.STONE);
        }
        else {
            itemStack = new ItemStackData(itemSection).getItem();
        }
    }

    public boolean spawn(Chunk centreChunk) {
        if (skillEntities.size() == 0) {
            return false;
        }
        Random random = new Random();
        int iterations = 0;
        SpawnHandler spawnHandler = EnchantedBosses.getInstance().getSpawnHandler();
        Chunk[] chunkSquare = spawnHandler.getChunkSquare(centreChunk, 4);
        int rng = random.nextInt(skillEntities.size());
        SkillEntity bossToSpawn = skillEntities.get(rng);
        while (spawnHandler.spawnBoss(bossToSpawn, chunkSquare).isEmpty() && iterations < skillEntities.size() * 2) {
            rng = random.nextInt(skillEntities.size());
            bossToSpawn = skillEntities.get(rng);
            iterations++;
        }
        return iterations < skillEntities.size() * 2;
    }

    public List<SkillEntity> getSkillEntities() {
        return skillEntities;
    }

    public ItemStack getItemStack() {
        return itemStack;
    }

    public String getName() {
        return name;
    }
}
