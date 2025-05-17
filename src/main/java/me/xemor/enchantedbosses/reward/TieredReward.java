package me.xemor.enchantedbosses.reward;

import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.enchantedbosses.EnchantedBosses;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class TieredReward implements Reward {

    @JsonPropertyWithDefault
    private List<RewardTier> receivers = Collections.emptyList();

    @Override
    public void giveRewards(@NotNull LivingEntity boss, @Nullable Player killer) {

    }

    public static final class RewardTier {

        @JsonPropertyWithDefault
        private List<String> commands = Collections.emptyList();
        @JsonPropertyWithDefault
        private List<ItemStack> items = Collections.emptyList();

        public RewardTier() {}

        public List<String> getCommands() {
            return commands;
        }

        public List<ItemStack> getItems() {
            return items;
        }
    }

}

