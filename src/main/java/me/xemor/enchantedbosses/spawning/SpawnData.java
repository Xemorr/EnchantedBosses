package me.xemor.enchantedbosses.spawning;

import com.fasterxml.jackson.annotation.JsonProperty;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.comparison.SetData;
import org.bukkit.block.Biome;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.EntityType;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class SpawnData {

    @JsonPropertyWithDefault
    private Mode mode = Mode.DISABLED;
    @JsonPropertyWithDefault
    private double weighting = 1;
    @JsonPropertyWithDefault
    private int spaceNeeded = 0;
    @JsonPropertyWithDefault
    private boolean spawnOnAir = true;
    @JsonPropertyWithDefault
    private boolean biomesWhitelist = true;
    @JsonPropertyWithDefault
    private SetData<Biome> biomes = new SetData<>();
    @JsonPropertyWithDefault
    private List<String> worlds = Collections.emptyList();
    @JsonPropertyWithDefault
    private int maxHeight = -1;
    @JsonPropertyWithDefault
    private SetData<EntityType> replace = new SetData<>();

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
