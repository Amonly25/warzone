package com.ar.askgaming.warzone.CustomEvents;

import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import com.ar.askgaming.warzone.WarzonePlugin;

public class WarzoneStartListener implements Listener{

    private WarzonePlugin plugin;
    public WarzoneStartListener(WarzonePlugin plugin) {
        this.plugin = plugin;

        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler
    public void onWarzoneStart(WarzoneStartEvent event) {
        //Bukkit.broadcastMessage(event.getClass().getClassLoader().toString());
        plugin.getLogger().info("Warzone has started!");
       
    }

}
