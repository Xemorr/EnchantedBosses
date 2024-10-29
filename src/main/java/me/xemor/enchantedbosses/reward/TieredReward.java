package me.xemor.enchantedbosses.reward;

import me.xemor.configurationdata.ItemStackData;
import me.xemor.enchantedbosses.EnchantedBosses;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

public class TieredReward extends Reward {

    HashMap<Integer,RewardTier> map = new HashMap<>();

    public TieredReward(int reward, ConfigurationSection configurationSection) {
        super(reward, configurationSection);
        ConfigurationSection receiversSection = configurationSection.getConfigurationSection("receivers");
        if (receiversSection == null) {
            EnchantedBosses.getInstance().getLogger().severe("You have not specified the receivers section! " + configurationSection.getCurrentPath() + ".receivers");
        }
        AtomicInteger counter = new AtomicInteger(0);
        receiversSection.getValues(false).values().stream()
                .filter(object -> object instanceof ConfigurationSection)
                .map(object -> (ConfigurationSection) object)
                .forEach(section -> {
                    RewardTier rewardTier = new RewardTier(section);
                    map.put(counter.getAndAdd(1), rewardTier);
                });
    }

    @Override
    public void giveRewards(@NotNull LivingEntity boss, @Nullable Player killer) {

    }

    public static final class RewardTier {

        private final List<String> commands;
        private final List<ItemStack> items;

        public RewardTier(ConfigurationSection tierSection) {
            commands = tierSection.getStringList("commands");
            items = tierSection.getConfigurationSection("items").getValues(false).values().stream()
                    .filter((object) -> object instanceof ConfigurationSection)
                    .map((object) -> (ConfigurationSection) object)
                    .map(configurationSection -> new ItemStackData(configurationSection).getItem())
                    .collect(Collectors.toList());
        }

        public List<String> getCommands() {
            return commands;
        }

        public List<ItemStack> getItems() {
            return items;
        }
    }

}

