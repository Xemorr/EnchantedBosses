package me.xemor.enchantedbosses.reward;

import java.util.HashMap;

public class Rewards {

    private static final HashMap<String, Integer> nameToReward = new HashMap<>();
    private static final HashMap<Integer, Class<? extends Reward>> rewardToData = new HashMap<>();
    private static int counter = 0;

    static {
        registerReward("BASIC", BasicReward.class);
        registerReward("TIERED", TieredReward.class);
    }

    public static void registerReward(String name, Class<? extends Reward> effectDataClass) {
        nameToReward.put(name, counter);
        rewardToData.put(counter, effectDataClass);
        counter++;
    }

    public static Class<? extends Reward> getClass(int trigger) { return rewardToData.getOrDefault(trigger, BasicReward.class); }

    public static int getReward(String name) {
        return nameToReward.getOrDefault(name, -1);
    }


}
