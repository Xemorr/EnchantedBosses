package me.xemor.enchantedbosses.commands;

import me.xemor.enchantedbosses.ConfigHandler;
import me.xemor.enchantedbosses.EnchantedBosses;
import me.xemor.enchantedbosses.SkillEntity;
import me.xemor.enchantedbosses.BossHandler;
import me.xemor.enchantedbosses.events.SkillEntitySpawnEvent;
import net.kyori.adventure.audience.Audience;
import net.kyori.adventure.text.minimessage.MiniMessage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class SpawnCommand implements SubCommand {

    private BossHandler bossHandler;

    public SpawnCommand(BossHandler bossHandler) {
        this.bossHandler = bossHandler;
    }

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Audience audience = EnchantedBosses.getBukkitAudiences().sender(sender);
        String bossName = args[1];
        SkillEntity skillEntity = bossHandler.getBoss(bossName);
        if (skillEntity == null) {
            audience.sendMessage(MiniMessage.miniMessage().deserialize(EnchantedBosses.getInstance().getConfigHandler().getInvalidBossMessage()));
            return;
        }
        if (args.length == 2 && sender instanceof Player player) {
            bossHandler.spawn(skillEntity, player.getLocation());
        }
        else if (args.length == 3) {
            Player player = Bukkit.getPlayer(args[2]);
            if (player == null) {
                audience.sendMessage(MiniMessage.miniMessage().deserialize(EnchantedBosses.getInstance().getConfigHandler().getInvalidPlayerMessage()));
                return;
            }
            bossHandler.spawn(skillEntity, player.getLocation());
        }
        else if (args.length >= 5) {
            try {
                double x = Double.parseDouble(args[2]);
                double y = Double.parseDouble(args[3]);
                double z = Double.parseDouble(args[4]);
                World world = null;
                if (args.length >= 6) {
                    world = Bukkit.getWorld(args[5]);
                }
                else if (sender instanceof Player player) {
                    world = player.getWorld();
                }
                if (world == null) {
                    audience.sendMessage(MiniMessage.miniMessage().deserialize(EnchantedBosses.getInstance().getConfigHandler().getInvalidLocationMessage()));
                    return;
                }
                bossHandler.spawn(skillEntity, new Location(world, x, y, z));
            } catch (NumberFormatException e) {
                audience.sendMessage(MiniMessage.miniMessage().deserialize(EnchantedBosses.getInstance().getConfigHandler().getInvalidLocationMessage()));
            }
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        if (args.length == 2) {
            return EnchantedBosses.getInstance().getBossHandler().getBosses().stream().map(SkillEntity::getName).filter((it) -> it.startsWith(args[1])).toList();
        } else if (args.length == 3) {
            return Stream.concat(Bukkit.getOnlinePlayers().stream().map(Player::getName), Arrays.stream(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}).mapToObj(String::valueOf)).filter((it) -> it.startsWith(args[2])).toList();
        } else if (args.length == 6) {
            return Bukkit.getWorlds().stream().map(World::getName).filter((it) -> it.startsWith(args[5])).toList();
        } else {
            return Arrays.stream(new int[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10}).mapToObj(String::valueOf).filter((it) -> it.startsWith(args[args.length - 1])).toList();
        }
    }
}
