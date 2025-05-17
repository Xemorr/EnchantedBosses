package me.xemor.enchantedbosses.reward;

import me.xemor.configurationdata.ItemStackData;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.ExperienceOrb;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerExpChangeEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ThreadLocalRandom;

public class BasicReward implements Reward {

    @JsonPropertyWithDefault
    private List<String> commands = Collections.emptyList();
    @JsonPropertyWithDefault
    private Map<String, ItemStack> items = Collections.emptyMap();
    @JsonPropertyWithDefault
    private int experience = 100;

    @Override
    public void giveRewards(LivingEntity boss, Player killer) {
        Location location = boss.getLocation();
        World world = location.getWorld();
        for (ItemStack item : items.values()) {
            ThreadLocalRandom rng = ThreadLocalRandom.current();
            world.dropItem(location, item, (it) -> it.setVelocity(new Vector(rng.nextDouble() / 4, 0.5, rng.nextDouble() / 4)));
        }
        if (killer != null) {
            for (String command : commands) {
                String parsedCommand = command.replaceAll("<player>", killer.getName());
                Bukkit.dispatchCommand(Bukkit.getConsoleSender(), parsedCommand);
            }
        }
        world.spawn(location, ExperienceOrb.class, (experienceOrb -> experienceOrb.setExperience(experience)));
    }

}
