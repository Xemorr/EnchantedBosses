package me.xemor.enchantedbosses;

import com.fasterxml.jackson.annotation.JsonAlias;
import com.fasterxml.jackson.annotation.JsonProperty;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.configurationdata.entity.EntityData;
import me.xemor.enchantedbosses.bossbar.BossBarData;
import me.xemor.enchantedbosses.damagemodifiers.BlankDamageModifier;
import me.xemor.enchantedbosses.damagemodifiers.DamageModifier;
import me.xemor.enchantedbosses.reward.Reward;
import me.xemor.enchantedbosses.spawning.SpawnData;
import me.xemor.skillslibrary2.Skill;
import net.kyori.adventure.text.Component;

import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;

public class SkillEntity {

    @JsonProperty
    private String name;
    @JsonProperty
    @JsonAlias({"coloredname", "colouredname"})
    private Component colouredName;
    @JsonProperty
    private String description;
    @JsonProperty
    private EntityData entity;
    @JsonPropertyWithDefault
    private Map<String, Skill> skills = Map.of();
    @JsonPropertyWithDefault
    @JsonAlias("rewards")
    private Reward reward = (boss, killer) -> {};
    @JsonPropertyWithDefault
    @JsonAlias("autospawn")
    private SpawnData spawnData = new SpawnData();
    @JsonPropertyWithDefault
    @JsonAlias("bossbar")
    private BossBarData bossBar = new BossBarData();
    @JsonPropertyWithDefault
    @JsonAlias("damagemodifier")
    private DamageModifier damageModifier = new BlankDamageModifier();

    // for jackson
    public SkillEntity() {}

    public Component getColouredName() {
        return colouredName;
    }

    public Collection<Skill> getSkills() {
        return skills.values();
    }

    public String getDescription() {
        return description;
    }

    public String getName() {
        return name;
    }

    public EntityData getEntityData() {
        return entity;
    }

    public Reward getReward() {
        return reward;
    }

    public void setReward(Reward reward) {
        this.reward = reward;
    }

    public BossBarData getBossBar() { return bossBar; }

    public void setBossBar(BossBarData bossBar) { this.bossBar = bossBar; }

    public SpawnData getAutoSpawnData() {
        return spawnData;
    }

    public void setAutoSpawnData(SpawnData spawnData) {
        this.spawnData = spawnData;
    }

    public DamageModifier getDamageModifier() { return damageModifier; }

    public void setDamageModifier(DamageModifier damageModifier) { this.damageModifier = damageModifier; }
}
