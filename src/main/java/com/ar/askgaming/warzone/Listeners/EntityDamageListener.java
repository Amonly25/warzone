package com.ar.askgaming.warzone.Listeners;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;

import com.ar.askgaming.warzone.WarzonePlugin;
import com.ar.askgaming.warzone.Warzone.Warzone;
import com.ar.askgaming.warzone.Warzone.WarzoneBoss;

public class EntityDamageListener implements Listener{

    private WarzonePlugin plugin;
    public EntityDamageListener(WarzonePlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);

    }
    @EventHandler()
    public void onEntityDamage(EntityDamageByEntityEvent e){
        
        Entity damager = e.getDamager();
        Entity entity = e.getEntity();

        // Handle logic when dragon makes damage
        if (damager instanceof Wither) {
            Warzone warzone = plugin.getWarzoneManager().getWarzone();
            if (warzone == null) {
                return;
            }

            WarzoneBoss boss = warzone.getBoss();
            if (boss == null) {
                return;
            }

            Wither witherBoss = boss.getWhiter();
            if (witherBoss == null) {
                return;
            }
            if (damager.equals(boss)){
                if (e.getEntity() instanceof Player) {
                    double dmg = e.getDamage();
                    int multiply = plugin.getConfig().getInt("damage_multiplier");
                    if (multiply > 1) {
                        e.setDamage(dmg*multiply);
                    }
               }
            }
            return;
        }

        // Handle logic when dragon makes damage
        if (e.getEntity() instanceof Wither) {

            Warzone warzone = plugin.getWarzoneManager().getWarzone();
            if (warzone == null) {
                return;
            }

            WarzoneBoss boss = warzone.getBoss();
            if (boss == null) {
                return;
            }

            Wither witherBoss = boss.getWhiter();
            if (witherBoss == null) {
                return;
            }
            if (!entity.equals(boss)){
                return;
            }

            // Abilities
            if (e.getDamager() instanceof Arrow) {
                Arrow a = (Arrow) e.getDamager();
                
                if (a.getShooter() instanceof Player) {	
                    Player p = (Player) a.getShooter();
                    boss.createCounterAttack(e, p);
                }
                return;
            }
            if (e.getDamager() instanceof Player) {
                Player p = (Player) e.getDamager();
                boss.createCounterAttack(e, p);
            }
        }
    }

}
