package me.xemor.enchantedbosses.damagemodifiers;

import me.xemor.enchantedbosses.reward.Reward;
import me.xemor.enchantedbosses.reward.Rewards;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public abstract class DamageModifier {

    public DamageModifier(ConfigurationSection section) {

    }

    public abstract double modify(double x, long numberOfPlayers);

    @Nullable
    public static DamageModifier create(int damagemodifier, ConfigurationSection configurationSection) {
        try {
            return DamageModifiers.getClass(damagemodifier).getConstructor(ConfigurationSection.class).newInstance(configurationSection);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
