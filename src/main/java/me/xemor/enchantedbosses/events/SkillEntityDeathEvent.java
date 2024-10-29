package me.xemor.enchantedbosses.events;

import me.xemor.enchantedbosses.SkillEntity;
import org.bukkit.Bukkit;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

public class SkillEntityDeathEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();
    private LivingEntity livingEntity;
    private SkillEntity skillEntity;
    private LivingEntity killer;

    public SkillEntityDeathEvent(LivingEntity livingEntity, SkillEntity skillEntity, LivingEntity killer) {
        this.livingEntity = livingEntity;
        this.skillEntity = skillEntity;
        this.killer = killer;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    public LivingEntity getEntity() {
        return livingEntity;
    }

    public SkillEntity getSkillEntity() {
        return skillEntity;
    }
}
