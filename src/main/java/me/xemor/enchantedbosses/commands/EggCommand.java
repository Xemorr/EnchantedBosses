package me.xemor.enchantedbosses.commands;

import me.xemor.enchantedbosses.EnchantedBosses;
import me.xemor.enchantedbosses.spawning.EggHandler;
import me.xemor.enchantedbosses.spawning.SpawnEgg;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class EggCommand implements SubCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Player receiver;
        SpawnEgg spawnEgg;
        EggHandler eggHandler = EnchantedBosses.getInstance().getConfigHandler().getEggHandler();
        if (args.length > 2) {
            receiver = Bukkit.getPlayer(args[1]);
            if (receiver == null) {
                sender.sendMessage(ChatColor.RED + "You have entered an invalid player name / they are not online!");
                return;
            }
            spawnEgg = eggHandler.getEgg(args[2]);
        } else if (args.length == 2) {
            receiver = (Player) sender;
            spawnEgg = eggHandler.getEgg(args[1]);
        } else {
            sender.sendMessage(ChatColor.RED + "You need to specify the player to give the egg to!");
            return;
        }
        if (spawnEgg == null) {
            sender.sendMessage(ChatColor.RED + "You have entered an invalid spawn egg name!");
            return;
        }
        receiver.getInventory().addItem(spawnEgg.getItemStack());
        sender.sendMessage(ChatColor.GREEN + "You have received your spawn egg!");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return EnchantedBosses.getInstance().getConfigHandler().getEggHandler().getEggs().stream().map(Map.Entry::getKey).filter((it) -> it.startsWith(args[1])).collect(Collectors.toList());
    }
}
