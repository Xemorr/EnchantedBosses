package me.xemor.enchantedbosses.bossbar;

import net.kyori.adventure.bossbar.BossBar;
import org.bukkit.configuration.ConfigurationSection;

import java.util.Locale;
import java.util.Set;
import java.util.stream.Collectors;

public class BossBarData {

    private boolean enabled;
    private Set<BossBar.Flag> flags;
    private BossBar.Color color;
    private BossBar.Overlay style;

    public BossBarData(ConfigurationSection configurationSection) {
        if (configurationSection == null) {
            enabled = false;
        }
        else {
            enabled = configurationSection.getBoolean("enabled", false);
            if (enabled) {
                this.flags = configurationSection.getStringList("flags").stream().map(String::toUpperCase).map(BossBar.Flag::valueOf).collect(Collectors.toSet());
                color = BossBar.Color.valueOf(configurationSection.getString("color", "PURPLE").toUpperCase(Locale.ROOT));
                style = BossBar.Overlay.valueOf(configurationSection.getString("style", "PROGRESS").toUpperCase(Locale.ROOT));
            }
        }
    }

    public boolean isEnabled() {
        return enabled;
    }

    public Set<BossBar.Flag> getFlags() {
        return flags;
    }

    public BossBar.Color getColor() {
        return color;
    }

    public BossBar.Overlay getStyle() {
        return style;
    }
}
