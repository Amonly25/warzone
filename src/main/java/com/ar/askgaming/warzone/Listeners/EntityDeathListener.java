package com.ar.askgaming.warzone.Listeners;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
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

                //Handling drops
                double chance = Math.random();

                if (cfg.getBoolean("remove_star_drops")){
                    e.getDrops().clear();
                }
                //Handling rewards

                for (String key : cfg.getConfigurationSection("rewards").getKeys(false)){
					
				    String txt = cfg.getString("rewards." + key + ".broadcast_text");
				    List<String> drop = cfg.getStringList("rewards." + key + ".drops");
				    List<String> commands = cfg.getStringList("rewards." + key + ".commands");
				    				    
				    if (cfg.getDouble("rewards." + key + ".chance") >= chance) {
				    					    
				    	if (!txt.equals("")){ 
						    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', txt));
				    	}
				    	for (String item : drop) {
				    		e.getDrops().add(new ItemStack(Material.valueOf(item)));
				    	}
					    
						for (String s : commands) {						
							Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s.replaceAll("%player%", p.getName()));
						}
				    }
				}
            }
        }
    }

}
