package me.xemor.enchantedbosses.commands;

import me.xemor.enchantedbosses.BossHandler;
import me.xemor.enchantedbosses.configs.ConfigHandler;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

import java.util.Collections;
import java.util.List;

public class ReloadCommand implements SubCommand {

    private BossHandler bossHandler;

    public ReloadCommand(BossHandler bossHandler) {
        this.bossHandler = bossHandler;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        sender.sendMessage(ChatColor.GREEN + "Reloading..");
        ConfigHandler configHandler = bossHandler.getConfigHandler();
        configHandler.reloadConfigs();
        sender.sendMessage(ChatColor.GREEN + "Reloaded");
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
