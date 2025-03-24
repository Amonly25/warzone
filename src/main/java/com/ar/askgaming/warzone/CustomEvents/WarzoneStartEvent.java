package com.ar.askgaming.warzone.CustomEvents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.ar.askgaming.warzone.Warzone.Warzone;

public class WarzoneStartEvent extends Event {

    private static final HandlerList HANDLERS = new HandlerList();

    public static HandlerList getHandlerList() {
        return HANDLERS;
    }

    @Override
    public HandlerList getHandlers() {
        return HANDLERS;
    }
    
    private Warzone warzone;

    public WarzoneStartEvent(Warzone warzone) {
        this.warzone = warzone;

    }
    public Warzone getWarzone() {
        return warzone;
    }

}
