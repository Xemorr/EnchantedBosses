package me.xemor.enchantedbosses.skills.conditions;

import me.xemor.configurationdata.ItemMetaData;
import me.xemor.configurationdata.JsonPropertyWithDefault;
import me.xemor.enchantedbosses.EnchantedBosses;
import me.xemor.enchantedbosses.SkillEntity;
import me.xemor.skillslibrary2.Mode;
import me.xemor.skillslibrary2.conditions.Condition;
import me.xemor.skillslibrary2.conditions.EntityCondition;
import me.xemor.skillslibrary2.conditions.TargetCondition;
import me.xemor.skillslibrary2.execution.Execution;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

public class BossCondition extends Condition implements EntityCondition, TargetCondition {

    @JsonPropertyWithDefault
    private List<String> bossNames = new ArrayList<>(1);

    public BossCondition() {}
    public BossCondition(String boss) {bossNames.add(boss);}

    public boolean isTrueFor(Entity entity) {
        SkillEntity boss = EnchantedBosses.getInstance().getBossHandler().getBoss(entity);
        if (boss != null) {
            if (bossNames.isEmpty()) {
                return true;
            }
            return bossNames.contains(boss.getName());
        }
        return false;
    }


    @Override
    public boolean isTrue(Execution execution, Entity entity) {
        return isTrueFor(entity);
    }

    @Override
    public CompletableFuture<Boolean> isTrue(Execution execution, Entity entity, Entity target) {
        return CompletableFuture.completedFuture(isTrueFor(target));
    }
}
