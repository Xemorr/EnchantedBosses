package me.xemor.enchantedbosses.damagemodifiers;

import org.bukkit.configuration.ConfigurationSection;

public class BlankDamageModifier extends DamageModifier {

    public BlankDamageModifier(ConfigurationSection section) {
        super(section);
    }

    public BlankDamageModifier() {
        super(null);
    }

    @Override
    public double modify(double x, long numberOfPlayers) {
        return x;
    }

}
