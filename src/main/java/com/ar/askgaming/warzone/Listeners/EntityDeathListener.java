package com.ar.askgaming.warzone.Listeners;

import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;

import com.ar.askgaming.warzone.WarzonePlugin;
import com.ar.askgaming.warzone.Warzone.Warzone;
import com.ar.askgaming.warzone.Warzone.WarzoneManager.WarzoneState;

public class EntityDeathListener implements Listener{

    private WarzonePlugin plugin;
    public EntityDeathListener(WarzonePlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler()
    public void onEntityDeath(EntityDeathEvent e){
        Entity entity = e.getEntity();
        Warzone warzone = plugin.getWarzoneManager().getWarzone();

        if (warzone.getState() == WarzoneState.WAINTING){
            return;
        }

        if (entity instanceof WitherSkeleton) {

            if (plugin.getConfig().getBoolean("remove_wither_skeleton_drops_on_warzone", true)){
                e.getDrops().clear();
                return;
            }
        }
        if (entity instanceof Wither) {

            Wither witherBoss = warzone.getWither();
            
            if (witherBoss == null || !witherBoss.isValid()){ 
                return;
            }
            if (!entity.equals(witherBoss)){
                return;
            }

            plugin.getWarzoneManager().stop();

            if (e.getEntity().getKiller() instanceof Player) {	
                
                Player p = (Player) e.getEntity().getKiller();

                if (plugin.getConfig().getBoolean("remove_star_drop_from_boss", true)){
                    e.getDrops().clear();
                }
                warzone.setLastKiller(p.getName());

                plugin.getWarzoneManager().proccesRewards(p, witherBoss.getLocation());
                plugin.getWarzoneManager().stop();
            }
        }
    }
}
