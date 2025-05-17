package me.xemor.enchantedbosses.reward;

import com.fasterxml.jackson.annotation.JsonTypeInfo;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@JsonTypeInfo(use = JsonTypeInfo.Id.NAME, property = "type")
public interface Reward {
    void giveRewards(@NotNull LivingEntity boss, @Nullable Player killer);
}
