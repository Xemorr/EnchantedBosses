package me.xemor.enchantedbosses.commands;

import me.xemor.enchantedbosses.BossHandler;
import me.xemor.enchantedbosses.EnchantedBosses;
import me.xemor.enchantedbosses.SkillEntity;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class RemoveCommand implements SubCommand {
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        if (sender instanceof Player player) {
            Collection<Entity> nearbyEntities = player.getWorld().getNearbyEntities(player.getLocation(), 16, 16, 16);
            for (Entity entity : nearbyEntities) {
                SkillEntity boss = EnchantedBosses.getInstance().getBossHandler().getBoss(entity);
                if (boss == null) continue;
                else {
                    entity.remove();
                }
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return null;
    }
}
