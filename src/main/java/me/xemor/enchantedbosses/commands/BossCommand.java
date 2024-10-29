package me.xemor.enchantedbosses.commands;

import me.xemor.enchantedbosses.EnchantedBosses;
import me.xemor.enchantedbosses.SkillEntity;
import me.xemor.enchantedbosses.BossHandler;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Egg;

import java.util.*;
import java.util.stream.Collectors;

public class BossCommand implements CommandExecutor, TabExecutor {

    private BossHandler bossHandler;
    private SpawnCommand spawnCommand;
    private TimeCommand timeCommand;
    private ReloadCommand reloadCommand;
    private RandomSpawnCommand randomSpawnCommand;
    private EggCommand eggCommand;

    private RemoveCommand removeCommand;

    public BossCommand(BossHandler bossHandler) {
        this.bossHandler = bossHandler;
        spawnCommand = new SpawnCommand(bossHandler);
        reloadCommand = new ReloadCommand(bossHandler);
        timeCommand = new TimeCommand();
        randomSpawnCommand = new RandomSpawnCommand();
        eggCommand = new EggCommand();
        removeCommand = new RemoveCommand();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length >= 1) {
            SubCommandType subCommandType = getSubCommandType(args[0]);
            SubCommand subCommand = getSubCommand(subCommandType);
            if (subCommand == null) {
                sender.sendMessage(ChatColor.RED + "You have entered an invalid sub command!");
                return true;
            }
            if (sender.hasPermission("enchantedbosses." + subCommandType.toString().toLowerCase())) {
                subCommand.onCommand(sender, args);
            }
            else {
                Audience audience = EnchantedBosses.getBukkitAudiences().sender(sender);
                audience.sendMessage(MiniMessage.miniMessage().deserialize(EnchantedBosses.getInstance().getConfigHandler().getNoPermissionMessage()));
            }
        }
        return true;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        List<String> tabCompletion = new ArrayList<>();
        if (args.length == 1) {
            for (SubCommandType subCommand : SubCommandType.values()) {
                String subCommandStr = subCommand.toString().toLowerCase();
                if (subCommandStr.startsWith(args[0].toLowerCase())) {
                    tabCompletion.add(subCommandStr);
                }
            }
        }
        if (args.length >= 2) {
            SubCommandType subCommandType = getSubCommandType(args[0]);
            SubCommand subCommand = getSubCommand(subCommandType);
            if (subCommand == null) return tabCompletion;
            else tabCompletion = subCommand.onTabComplete(sender, args);
        }
        return tabCompletion;
    }

    public SubCommand getSubCommand(SubCommandType name) {
        if (name == null) {
            return null;
        }
        return switch (name) {
            case SPAWN -> spawnCommand;
            case RELOAD -> reloadCommand;
            case TIME -> timeCommand;
            case RANDOMSPAWN -> randomSpawnCommand;
            case EGG -> eggCommand;
            case REMOVE -> removeCommand;
            default -> null;
        };
    }

    public SubCommandType getSubCommandType(String name) {
        try {
            return SubCommandType.valueOf(name.toUpperCase());
        } catch(IllegalArgumentException e) {
            return null;
        }
    }
}
