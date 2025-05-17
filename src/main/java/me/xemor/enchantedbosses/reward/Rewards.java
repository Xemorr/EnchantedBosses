package me.xemor.enchantedbosses.reward;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;

public class Rewards {

    private static final BiMap<String, Class<? extends Reward>> rewardToData = HashBiMap.create();

    static {
        registerReward("BASIC", BasicReward.class);
        registerReward("TIERED", TieredReward.class);
    }

    public static void registerReward(String name, Class<? extends Reward> rewardClass) {
        rewardToData.put(name, rewardClass);
    }

    public static NamedType[] getNamedTypes() {
        return rewardToData.entrySet().stream().map((entry) -> new NamedType(entry.getValue(), entry.getKey())).toArray(NamedType[]::new);
    }
}
