package me.xemor.enchantedbosses.damagemodifiers;

import me.xemor.configurationdata.JsonPropertyWithDefault;

//https://www.desmos.com/calculator/p03hsebinc
public class PlayerAdjustedDamageModifier implements DamageModifier {

    //Recommended not to set to above one as then it will punish players for bringing teammate
    @JsonPropertyWithDefault
    private double playerScalingModifier = 1D/2D; //This is the variable w in desmos

    @Override
    public double modify(double x, long numberOfPlayers) {
        return x / (Math.pow(numberOfPlayers, playerScalingModifier));
    }

}
