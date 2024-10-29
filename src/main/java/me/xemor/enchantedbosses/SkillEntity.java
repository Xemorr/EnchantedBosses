package me.xemor.enchantedbosses;

import me.xemor.configurationdata.entity.EntityData;
import me.xemor.enchantedbosses.bossbar.BossBarData;
import me.xemor.enchantedbosses.damagemodifiers.DamageModifier;
import me.xemor.enchantedbosses.reward.Reward;
import me.xemor.enchantedbosses.spawning.SpawnData;
import net.kyori.adventure.text.Component;

import java.util.AbstractMap;
import java.util.HashMap;

public class SkillEntity {

    private final String name;
    private final Component colouredName;
    private final String description;
    private final EntityData entityData;
    private Reward reward;
    private SpawnData spawnData;
    private BossBarData bossBarData;
    private DamageModifier damageModifier;

    public SkillEntity(String name, Component colouredName, String description, EntityData entityData) {
        this.name = name;
        this.colouredName = colouredName;
        this.description = description;
        this.entityData = entityData;
    }


    public Component getColouredName() {
        return colouredName;
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public EntityData getEntityData() {
        return entityData;
    }

    public Reward getReward() {
        return reward;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }

    public BossBarData getBossBarData() { return bossBarData; }

    public void setBossBarData(BossBarData bossBarData) { this.bossBarData = bossBarData; }

    public SpawnData getAutoSpawnData() {
        return spawnData;
    }

    public void setAutoSpawnData(SpawnData spawnData) {
        this.spawnData = spawnData;
    }

    public DamageModifier getDamageModifier() { return damageModifier; }

    public void setDamageModifier(DamageModifier damageModifier) { this.damageModifier = damageModifier; }
}
