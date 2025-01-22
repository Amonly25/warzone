package com.ar.askgaming.warzone.Events;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.ar.askgaming.warzone.Warzone.Warzone;

public class WarzoneStartEvent extends Event {

    private static final HandlerList handlers = new HandlerList();

    private Warzone warzone;

    public Warzone getWarzone() {
        return warzone;
    }

    public WarzoneStartEvent(Warzone warzone) {
        this.warzone = warzone;
    }

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }

    public static HandlerList getHandlerList() {
        return handlers;
    }

}
