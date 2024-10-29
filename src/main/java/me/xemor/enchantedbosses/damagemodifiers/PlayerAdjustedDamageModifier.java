package me.xemor.enchantedbosses.damagemodifiers;

import org.bukkit.configuration.ConfigurationSection;

//https://www.desmos.com/calculator/p03hsebinc
public class PlayerAdjustedDamageModifier extends DamageModifier {

    //Recommended not to set to above one as then it will punish players for bringing teammates
    private double playerScalingModifier; //This is the variable w in desmos

    public PlayerAdjustedDamageModifier(ConfigurationSection section) {
        super(section);
        playerScalingModifier = (section.getDouble("playerScalingModifier", 1D/2D));
    }

    @Override
    public double modify(double x, long numberOfPlayers) {
        return x / (Math.pow(numberOfPlayers, playerScalingModifier));
    }

}
