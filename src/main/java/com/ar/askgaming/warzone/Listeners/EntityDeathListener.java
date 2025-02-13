package com.ar.askgaming.warzone.Listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.entity.WitherSkeleton;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.inventory.ItemStack;

import com.ar.askgaming.warzone.WarzonePlugin;
import com.ar.askgaming.warzone.Warzone.Warzone;
import com.ar.askgaming.warzone.Warzone.WarzoneBoss;

import net.md_5.bungee.api.ChatColor;

public class EntityDeathListener implements Listener{

    private WarzonePlugin plugin;
    public EntityDeathListener(WarzonePlugin plugin) {
        this.plugin = plugin;
        plugin.getServer().getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler()
    public void onEntityDeath(EntityDeathEvent e){
        Entity entity = e.getEntity();
        if (entity instanceof WitherSkeleton) {
            Warzone warzone = plugin.getWarzoneManager().getWarzone();
            if (warzone == null) {
                return;
            }
            WarzoneBoss boss = warzone.getBoss();
            if (boss == null) {
                return;
            }
            if (plugin.getConfig().getBoolean("remove_wither_skeleton_drops_on_warzone", true)){
                e.getDrops().clear();
                return;
            }

        }
        if (entity instanceof Wither) {
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
            if (!entity.equals(witherBoss)){
                return;
            }
            plugin.getWarzoneManager().stop();
            if (e.getEntity().getKiller() instanceof Player) {	
                
                // Set Dragon killes
                Player p = (Player) e.getEntity().getKiller();

                FileConfiguration cfg = plugin.getConfig();

                if (cfg.getBoolean("remove_star_drop_from_boss", true)){
                    e.getDrops().clear();
                }
                //Handling rewards

                plugin.getWarzoneManager().proccesRewards(p, witherBoss.getLocation());
            }
        }
    }

}
