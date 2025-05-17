package me.xemor.enchantedbosses.damagemodifiers;

import org.bukkit.configuration.ConfigurationSection;

public class BlankDamageModifier implements DamageModifier {

    public BlankDamageModifier() {}

    @Override
    public double modify(double x, long numberOfPlayers) {
        return x;
    }

}
