package me.xemor.enchantedbosses.damagemodifiers;

import me.xemor.configurationdata.JsonPropertyWithDefault;
import org.bukkit.configuration.ConfigurationSection;


//If you want to see how it works: https://www.desmos.com/calculator/pyb798rnyr
public class EasedDamageModifier implements DamageModifier {

    @JsonPropertyWithDefault
    protected double expectedMaximumDamage = 30; //This is the variable b on desmos
    @JsonPropertyWithDefault
    protected double damageCap = 15; //This is the variable k in desmos.

    @Override
    public double modify(double x, long numberOfPlayers) {
        return x > expectedMaximumDamage ? damageCap : damageCap * (1 - (1 - x/expectedMaximumDamage) * (1 - x/expectedMaximumDamage));
    }
}
