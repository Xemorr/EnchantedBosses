package me.xemor.enchantedbosses.commands;

import me.xemor.enchantedbosses.configs.ConfigHandler;
import me.xemor.enchantedbosses.EnchantedBosses;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.minimessage.MiniMessage;
import net.kyori.adventure.text.minimessage.tag.resolver.Placeholder;
import org.bukkit.command.CommandSender;

import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class TimeCommand implements SubCommand {
    @Override
    public void onCommand(CommandSender sender, String[] args) {
        Optional<LocalDateTime> optionalNextTime = EnchantedBosses.getInstance().getSpawnHandler().getOptionalNextTime();
        ConfigHandler configHandler = EnchantedBosses.getInstance().getConfigHandler();
        Component component;
        if (optionalNextTime.isPresent()) {
            LocalDateTime nextTime = optionalNextTime.get();
            LocalDateTime timeUntilNextBoss = LocalDateTime.ofEpochSecond(nextTime.toEpochSecond(ZoneOffset.UTC) - LocalDateTime.now().toEpochSecond(ZoneOffset.UTC), 0, ZoneOffset.UTC);
            component = MiniMessage.miniMessage().deserialize(configHandler.getLanguageConfig().getBossTime(),
                    Placeholder.unparsed("hour",String.valueOf(timeUntilNextBoss.getHour())),
                    Placeholder.unparsed("minutes",String.valueOf(timeUntilNextBoss.getMinute())),
                    Placeholder.unparsed("seconds",String.valueOf(timeUntilNextBoss.getSecond())));
        }
        else {
            component = MiniMessage.miniMessage().deserialize(configHandler.getLanguageConfig().getNoBossTime());
        }
        EnchantedBosses.getBukkitAudiences().sender(sender).sendMessage(component);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, String[] args) {
        return Collections.emptyList();
    }
}
