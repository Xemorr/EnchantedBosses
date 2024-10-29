package me.xemor.enchantedbosses.reward;

import me.xemor.configurationdata.ItemStackData;
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
import java.util.List;

public class BasicReward extends Reward {

    List<String> commands;
    List<ItemStack> items = new ArrayList<>();
    int experience;

    public BasicReward(int reward, ConfigurationSection configurationSection) {
        super(reward, configurationSection);
        commands = configurationSection.getStringList("commands");
        ConfigurationSection itemsSection = configurationSection.getConfigurationSection("items");
        if (itemsSection != null) {
            for (Object itemObject : itemsSection.getValues(false).values()) {
                if (itemObject instanceof ConfigurationSection) {
                    ConfigurationSection itemSection = (ConfigurationSection) itemObject;
                    ItemStack item = new ItemStackData(itemSection).getItem();
                    items.add(item);
                }
            }
        }
        experience = configurationSection.getInt("experience", 100);
    }

    @Override
    public void giveRewards(LivingEntity boss, Player killer) {
        Location location = boss.getLocation();
        World world = location.getWorld();
        for (ItemStack item : items) {
            world.dropItem(location, item, (it) -> it.setVelocity(new Vector(Math.random() / 4, 0.5, Math.random() / 4)));
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
