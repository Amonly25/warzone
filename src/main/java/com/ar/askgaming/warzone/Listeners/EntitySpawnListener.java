package com.ar.askgaming.warzone.Listeners;

import org.bukkit.Location;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.CreatureSpawnEvent;

import com.ar.askgaming.warzone.Main;
import com.ar.askgaming.warzone.Warzone;

public class EntitySpawnListener implements Listener {

    private Main plugin;
    public EntitySpawnListener(Main main){
        plugin = main;
    }

	@EventHandler(priority = EventPriority.HIGHEST)
	public void onSpawn(CreatureSpawnEvent e) {
				
		Location loc = plugin.getWarzone().getLocation();
		
		if (e.getEntityType() == EntityType.WITHER) { 
				
			Wither w = (Wither) e.getEntity();
			
			if (e.getLocation().equals(loc)) {
				
				//plugin.getWarzoneBoss().setWither?
			}																			
		}		
	}
}
