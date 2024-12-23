package com.ar.askgaming.warzone.Listeners;

import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityTargetLivingEntityEvent;

import com.ar.askgaming.warzone.WarzonePlugin;

public class EntityTargetListener implements Listener{

    private WarzonePlugin plugin;
    public EntityTargetListener(WarzonePlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onTargeting(EntityTargetLivingEntityEvent e) {
        Entity targeted = e.getTarget();
        if (targeted == null) {
            return;
        }
    
        Entity entity = e.getEntity();
    
        if (isWarzoneBoss(targeted)) {
            e.setCancelled(true);
            return;
        }
    
        if (entity instanceof Wither) {
            handleWitherTargeting(e, targeted, entity);
        }
    }
    
    private boolean isWarzoneBoss(Entity entity) {
        if (entity instanceof Wither) {
            Wither boss = plugin.getWarzoneManager().getWarzoneBoss();
            return boss != null && entity.equals(boss);
        }
        return false;
    }
    
    private void handleWitherTargeting(EntityTargetLivingEntityEvent e, Entity targeted, Entity entity) {
        Location warzone = plugin.getWarzoneManager().getLocation();
        if (entity.getLocation().distance(warzone) > 32) {
            entity.teleport(warzone);

        }
        if (targeted instanceof Player) {
            if (targeted.getLocation().distance(entity.getLocation()) > 32) {
                e.setCancelled(true);
            }
        } else {
            e.setCancelled(true);
        }
    }
}
