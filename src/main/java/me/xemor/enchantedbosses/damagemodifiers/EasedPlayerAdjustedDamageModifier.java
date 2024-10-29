package me.xemor.enchantedbosses.damagemodifiers;

import org.bukkit.configuration.ConfigurationSection;


//Use this link to see how this class works. https://www.desmos.com/calculator/drpnfjm4ty
public class EasedPlayerAdjustedDamageModifier extends EasedDamageModifier {

    //Making this greater than one makes the people fighting the boss be effectively punished for having their friends with them
    private double playerScalingModifier; //This is the variable w in desmos

    public EasedPlayerAdjustedDamageModifier(ConfigurationSection section) {
        super(section);
        playerScalingModifier = (section.getDouble("playerScalingModifier", 1D/2D));
    }

    @Override
    public double modify(double x, long numberOfPlayers) {
        return x > expectedMaximumDamage ? damageCap : (damageCap / Math.pow(numberOfPlayers, playerScalingModifier)) * (1 - (1 - x/expectedMaximumDamage) * (1 - x/expectedMaximumDamage));
    }
}
