package com.ar.askgaming.warzone.CustomEvents;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.ar.askgaming.warzone.WarzonePlugin;

public class WarzoneStartListener implements Listener{

    public WarzoneStartListener(WarzonePlugin plugin) {
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }
    @EventHandler
    public void onWarzoneStart(WarzoneStartEvent event) {
        //TODO
    }

}