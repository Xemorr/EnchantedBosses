package me.xemor.enchantedbosses.bossbar;

import me.xemor.configurationdata.JsonPropertyWithDefault;
import net.kyori.adventure.bossbar.BossBar;

import java.util.Collections;
import java.util.Set;

public class BossBarData {

    @JsonPropertyWithDefault
    private boolean enabled = false;
    @JsonPropertyWithDefault
    private Set<BossBar.Flag> flags = Collections.emptySet();
    @JsonPropertyWithDefault
    private BossBar.Color color = BossBar.Color.PURPLE;
    @JsonPropertyWithDefault
    private BossBar.Overlay style = BossBar.Overlay.PROGRESS;

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
