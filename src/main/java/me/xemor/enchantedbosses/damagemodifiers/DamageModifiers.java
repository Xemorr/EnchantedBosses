package me.xemor.enchantedbosses.damagemodifiers;

import me.xemor.enchantedbosses.reward.BasicReward;
import me.xemor.enchantedbosses.reward.Reward;

import java.util.HashMap;

public class DamageModifiers {

    private static final HashMap<String, Integer> nameToModifier = new HashMap<>();
    private static final HashMap<Integer, Class<? extends DamageModifier>> modifierToData = new HashMap<>();
    private static int counter = 0;

    static {
        registerDamageModifier("BLANK", BlankDamageModifier.class);
        registerDamageModifier("EASED", EasedDamageModifier.class);
        registerDamageModifier("EASEDPLAYERADJUSTED", EasedPlayerAdjustedDamageModifier.class);
        registerDamageModifier("PLAYERADJUSTED", PlayerAdjustedDamageModifier.class);
    }

    public static void registerDamageModifier(String name, Class<? extends DamageModifier> effectDataClass) {
        nameToModifier.put(name, counter);
        modifierToData.put(counter, effectDataClass);
        counter++;
    }

    public static Class<? extends DamageModifier> getClass(int trigger) { return modifierToData.getOrDefault(trigger, BlankDamageModifier.class); }

    public static int getDamageModifier(String name) {
        return nameToModifier.getOrDefault(name, -1);
    }


}
