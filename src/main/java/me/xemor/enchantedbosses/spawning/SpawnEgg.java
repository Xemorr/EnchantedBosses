package me.xemor.enchantedbosses.spawning;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
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

    @JsonProperty
    @JsonAlias("bosses")
    private List<String> skillEntities;
    @JsonProperty
    private ItemStack item;

    public boolean spawn(Chunk centreChunk) {
        if (skillEntities.isEmpty()) {
            return false;
        }
        Random random = new Random();
        int iterations = 0;
        SpawnHandler spawnHandler = EnchantedBosses.getInstance().getSpawnHandler();
        Chunk[] chunkSquare = spawnHandler.getChunkSquare(centreChunk, 4);

        SkillEntity bossToSpawn;
        do {
            int rng = random.nextInt(skillEntities.size());
            String bossName = skillEntities.get(rng);
            bossToSpawn = EnchantedBosses.getInstance().getBossHandler().getBoss(bossName);
            if (bossToSpawn == null) {
                EnchantedBosses.getInstance().getLogger().warning("Boss " + bossName + " is not a valid boss!");
            }
        } while (spawnHandler.spawnBoss(bossToSpawn, chunkSquare).isEmpty() && iterations < skillEntities.size() * 2);
        return iterations < skillEntities.size() * 2;
    }

    public ItemStack getItemStack() {
        return item;
    }
}
