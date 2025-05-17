package me.xemor.enchantedbosses.damagemodifiers;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import me.xemor.enchantedbosses.reward.Reward;
import me.xemor.enchantedbosses.reward.Rewards;
import org.bukkit.configuration.ConfigurationSection;
import org.jetbrains.annotations.Nullable;

import java.lang.reflect.InvocationTargetException;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface DamageModifier {

    double modify(double x, long numberOfPlayers);

}
