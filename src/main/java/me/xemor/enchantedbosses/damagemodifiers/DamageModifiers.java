package me.xemor.enchantedbosses.damagemodifiers;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import me.xemor.enchantedbosses.reward.BasicReward;
import me.xemor.enchantedbosses.reward.Reward;

import java.util.HashMap;

public class DamageModifiers {

    private static final BiMap<String, Class<? extends DamageModifier>> modifierToData = HashBiMap.create();

    static {
        registerDamageModifier("BLANK", BlankDamageModifier.class);
        registerDamageModifier("EASED", EasedDamageModifier.class);
        registerDamageModifier("EASEDPLAYERADJUSTED", EasedPlayerAdjustedDamageModifier.class);
        registerDamageModifier("PLAYERADJUSTED", PlayerAdjustedDamageModifier.class);
    }

    public static void registerDamageModifier(String name, Class<? extends DamageModifier> effectDataClass) {
        modifierToData.put(name, effectDataClass);
    }

    public static NamedType[] getNamedTypes() {
        return modifierToData.entrySet().stream().map((entry) -> new NamedType(entry.getValue(), entry.getKey())).toArray(NamedType[]::new);
    }
}
