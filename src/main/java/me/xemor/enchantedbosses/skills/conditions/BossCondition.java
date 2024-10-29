package me.xemor.enchantedbosses.skills.conditions;

import me.xemor.configurationdata.ItemMetaData;
import me.xemor.enchantedbosses.EnchantedBosses;
import me.xemor.enchantedbosses.SkillEntity;
import me.xemor.skillslibrary2.Mode;
import me.xemor.skillslibrary2.conditions.Condition;
import me.xemor.skillslibrary2.conditions.EntityCondition;
import me.xemor.skillslibrary2.conditions.TargetCondition;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Entity;

import java.util.List;
import java.util.stream.Collectors;

public class BossCondition extends Condition implements EntityCondition, TargetCondition {

    private final List<String> bossNames;

    public BossCondition(int condition, ConfigurationSection configurationSection) {
        super(condition, configurationSection);
        this.bossNames = configurationSection.getStringList("entities").stream().map(String::toUpperCase).collect(Collectors.toList());
    }

    public BossCondition(int condition, List<String> bossNames) {
        super(condition, Mode.SELF);
        this.bossNames = bossNames.stream().map(String::toUpperCase).collect(Collectors.toList());
    }

    @Override
    public boolean isTrue(Entity skillEntity, Entity target) {
        return isTrueFor(target);
    }

    @Override
    public boolean isTrue(Entity livingEntity) {
        return isTrueFor(livingEntity);
    }

    public boolean isTrueFor(Entity entity) {
        SkillEntity boss = EnchantedBosses.getInstance().getBossHandler().getBoss(entity);
        if (boss != null) {
            if (bossNames.size() == 0) {
                return true;
            }
            return bossNames.contains(boss.getName().toUpperCase());
        }
        return false;
    }


}
