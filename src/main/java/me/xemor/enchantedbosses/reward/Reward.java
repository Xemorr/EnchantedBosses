package me.xemor.enchantedbosses.reward;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

public abstract class Reward {

    private final int reward;

    public Reward(int reward, ConfigurationSection configurationSection) {
        this.reward = reward;
    }

    public abstract void giveRewards(@NotNull LivingEntity boss, @Nullable Player killer);

    @Nullable
    public static Reward create(int reward, ConfigurationSection configurationSection) {
        try {
            return Rewards.getClass(reward).getConstructor(int.class, ConfigurationSection.class).newInstance(reward, configurationSection);
        } catch (NoSuchMethodException | IllegalAccessException | InstantiationException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

}
