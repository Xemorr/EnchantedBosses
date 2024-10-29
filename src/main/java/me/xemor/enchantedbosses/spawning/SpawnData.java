package me.xemor.enchantedbosses.spawning;

import me.xemor.configurationdata.comparison.SetData;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.List;
import java.util.stream.Collectors;

public class SpawnData {
    private final Mode mode;
    private double weighting;
    private int spaceNeeded;
    private boolean spawnOnAir;
    private boolean biomesWhitelist;
    private SetData<Biome> biomes;
    private List<String> worlds;
    private int maxHeight;
    private SetData<EntityType> replace;

    public SpawnData(ConfigurationSection configurationSection) {
        boolean enabled = configurationSection.getBoolean("enabled", Boolean.FALSE);
        if (enabled) {
            mode = Mode.BOSS;
        }
        else {
            mode = Mode.valueOf(configurationSection.getString("mode", "DISABLED"));
        }
        if (mode != Mode.DISABLED) {
            weighting = configurationSection.getDouble("weighting", 1);
            biomes = new SetData<>(Biome.class, "biomes", configurationSection);
            worlds = configurationSection.getStringList("worlds");
            biomesWhitelist = configurationSection.getBoolean("biomesWhitelist", true);
            spaceNeeded = configurationSection.getInt("spaceNeeded", 0);
            spawnOnAir = configurationSection.getBoolean("spawnOnAir", true);
            maxHeight = configurationSection.getInt("maxHeight", -1);
            replace = new SetData<>(EntityType.class, "replace", configurationSection);
        }
    }

    public Mode getMode() {
        return mode;
    }

    public double getWeighting() {
        return weighting;
    }

    public int getSpaceNeeded() {
        return spaceNeeded;
    }

    public int getMaxHeight() {
        return maxHeight;
    }

    public boolean matchesBiome(Biome biome) {
         return biomes.inSet(biome) == biomesWhitelist;
    }

    public boolean shouldSpawnOnAir() {
        return spawnOnAir;
    }

    public List<String> getWorlds() {
        return worlds;
    }

    public SetData<EntityType> getReplace() {
        return replace;
    }

    public enum Mode {
        DISABLED, REPLACE, BOSS;
    }
}
