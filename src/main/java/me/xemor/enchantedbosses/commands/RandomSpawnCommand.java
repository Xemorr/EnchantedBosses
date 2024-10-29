package me.xemor.enchantedbosses.commands;

import me.xemor.enchantedbosses.EnchantedBosses;
import me.xemor.enchantedbosses.SkillEntity;
import me.xemor.enchantedbosses.spawning.SpawnHandler;
import org.bukkit.Location;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.stream.Collectors;

public class RandomSpawnCommand implements SubCommand {

    @Override
    public void onCommand(CommandSender sender, String[] args) {
        SpawnHandler spawnHandler = EnchantedBosses.getInstance().getSpawnHandler();
        if (args.length >= 2) {
            SkillEntity skillEntity = EnchantedBosses.getInstance().getBossHandler().getBoss(args[1]);
            if (sender instanceof Player) {
                int radius = Integer.parseInt(args[2]);
                Location location = ((Player) sender).getLocation();
                spawnHandler.spawnBoss(skillEntity, location.getChunk(), radius);
            }
            else {
                spawnHandler.spawnBoss(skillEntity);
            }
        } else {
            spawnHandler.spawnRandomBoss();
        }
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return EnchantedBosses.getInstance().getBossHandler().getBosses().stream().map(SkillEntity::getName).filter((it) -> it.startsWith(args[1])).collect(Collectors.toList());
    }
}
