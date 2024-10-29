package me.xemor.enchantedbosses.commands;

import org.bukkit.command.CommandSender;

import java.util.Collection;
import java.util.List;

public interface SubCommand {

    void onCommand(CommandSender sender, String[] args);
    List<String> onTabComplete(CommandSender sender, String[] args);

}
