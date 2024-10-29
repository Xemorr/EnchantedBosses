package me.xemor.enchantedbosses.damagemodifiers;

import org.bukkit.configuration.ConfigurationSection;


//If you want to see how it works: https://www.desmos.com/calculator/pyb798rnyr
public class EasedDamageModifier extends DamageModifier {

    protected final double expectedMaximumDamage; //This is the variable b on desmos
    protected final double damageCap; //This is the variable k in desmos.


    public EasedDamageModifier(ConfigurationSection section) {
        super(section);
        this.expectedMaximumDamage = section.getDouble("expectedMaximumDamage", 30);
        this.damageCap = section.getDouble("damageCap", 15);
    }

    @Override
    public double modify(double x, long numberOfPlayers) {
        return x > expectedMaximumDamage ? damageCap : damageCap * (1 - (1 - x/expectedMaximumDamage) * (1 - x/expectedMaximumDamage));
    }
}
