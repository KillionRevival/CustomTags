package com.flyerzrule.mc.customtags.events;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import lombok.Getter;
import lombok.Setter;
import net.kyori.adventure.text.Component;

public class CustomComponentMessageEvent extends Event implements Cancellable {
    private static final HandlerList handlers = new HandlerList();

    @Setter
    @Getter
    private boolean isCancelled;

    @Getter
    private final List<Component> originalComponents;

    @Setter
    @Getter
    private List<Component> components;

    @Getter
    private final Player player;

    public CustomComponentMessageEvent(Player player, List<Component> originalComponents) {
        this.originalComponents = originalComponents;
        this.components = originalComponents;
        this.player = player;
        this.isCancelled = false;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }
}
