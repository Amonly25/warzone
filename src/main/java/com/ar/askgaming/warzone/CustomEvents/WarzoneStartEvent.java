package com.ar.askgaming.warzone.CustomEvents;

import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import com.ar.askgaming.warzone.Warzone.Warzone;

public class WarzoneStartEvent extends Event {


    private HandlerList handlers = new HandlerList();

    @Override
    public HandlerList getHandlers() {
        return handlers;
    }
    private Warzone warzone;
    public WarzoneStartEvent(Warzone warzone) {
        this.warzone = warzone;
    }
    public Warzone getWarzone() {
        return warzone;
    }

}
