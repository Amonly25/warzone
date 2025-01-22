package com.ar.askgaming.warzone.Listeners;

import org.bukkit.Bukkit;
import org.bukkit.entity.Arrow;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkull;
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
    @EventHandler
    public void onEntityDamage(EntityDamageByEntityEvent e) {
        Entity damager = e.getDamager();
        Entity entity = e.getEntity();

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
        if (damager instanceof WitherSkull){
            WitherSkull arrow = (WitherSkull) damager;
            if (arrow.getShooter().equals(witherBoss)) {
                handleWitherDamage(e);
            }
        }
        if (entity.equals(witherBoss)) {
            handleWitherBeingDamaged(e, boss);
        }
    }

    private void handleWitherDamage(EntityDamageByEntityEvent e) {
        
        if (e.getEntity() instanceof Player) {
            //Bukkit.broadcastMessage("test");
            double dmg = e.getDamage();
            int multiply = plugin.getConfig().getInt("damage_multiplier");
            if (multiply > 1) {
                e.setDamage(dmg * multiply);
            }
        } else {
            e.setCancelled(true);
        }
    }

    private void handleWitherBeingDamaged(EntityDamageByEntityEvent e, WarzoneBoss boss) {
        Entity damager = e.getDamager();
        if (damager instanceof Arrow) {
            Arrow arrow = (Arrow) damager;
            if (arrow.getShooter() instanceof Player) {
                Player player = (Player) arrow.getShooter();
                boss.createCounterAttack(e, player);
            }
        } else if (damager instanceof Player) {
            Player player = (Player) damager;
            boss.createCounterAttack(e, player);
        }
    }

}
