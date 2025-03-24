package com.ar.askgaming.warzone.Warzone;

import java.util.List;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.ar.askgaming.universalnotifier.UniversalNotifier;
import com.ar.askgaming.universalnotifier.Managers.AlertManager.Alert;
import com.ar.askgaming.warzone.WarzonePlugin;
import com.ar.askgaming.warzone.CustomEvents.WarzoneStartEvent;

import net.md_5.bungee.api.ChatColor;

public class WarzoneManager extends BukkitRunnable{

    private WarzonePlugin plugin;
    private Warzone warzone;

    public WarzoneManager(WarzonePlugin main){
        plugin = main;

        runTaskTimer(main, 0, 20*60);

        warzone = new Warzone();
    }

    public enum WarzoneState {
        IN_PROGRESS,
        WAINTING,
    }
    private String getLang(String key, Player p){
        return plugin.getLang().getFrom(key, p);
    }

    //#region start
    public void start(){

        if (plugin.getServer().getPluginManager().getPlugin("UniversalNotifier") != null) {
            UniversalNotifier notifier = UniversalNotifier.getInstance();
            String start = plugin.getConfig().getString("notifier.start");
            notifier.getNotificationManager().broadcastToAll(Alert.CUSTOM, start);
        } 

        WarzoneStartEvent event = new WarzoneStartEvent(warzone);
        Bukkit.getPluginManager().callEvent(event);

        for (Player p : Bukkit.getOnlinePlayers()){
            String title = getLang("start.title", p);
            String subtitle = getLang("start.subtitle", p);
            p.sendTitle(title, subtitle, 20, 40, 20);
            p.playSound(p.getLocation(), Sound.ENTITY_WITHER_SPAWN, 10, 1);
            p.sendMessage(getLang("start.message", p));
        }

        warzone.spawnBoss();
        warzone.setState(WarzoneState.IN_PROGRESS);

    }
    //#region stop
    public void stop(){

        if (warzone.getState() == WarzoneState.WAINTING) {
            return;
        }
        String killer = warzone.getLastKiller();
        if (plugin.getServer().getPluginManager().getPlugin("UniversalNotifier") != null) {
            UniversalNotifier notifier = UniversalNotifier.getInstance();
            String start = plugin.getConfig().getString("notifier.boss_killed").replace("%player%", killer != null ? killer : "Server");
            notifier.getNotificationManager().broadcastToAll(Alert.CUSTOM, start);
        } 
        Wither wither = warzone.getWither();
        if (wither != null && !wither.isDead()){
            wither.remove();
        }

        for (Player p : Bukkit.getOnlinePlayers()){
            String title = getLang("stop.title", p);
            String subtitle = getLang("stop.subtitle", p);
            p.sendTitle(title, subtitle, 20, 40, 20);
            p.playSound(p.getLocation(), Sound.ENTITY_WITHER_DEATH, 10, 1);
            p.sendMessage(getLang("stop.message", p));
        }

        plugin.getConfig().set("last_warzone", System.currentTimeMillis() / 60000);
        plugin.saveConfig();
    }
    //#region warp
    public void warp(Player p){

        final int z = p.getLocation().getBlockZ(), x = p.getLocation().getBlockX();
        p.sendMessage(getLang("warp",p));

        new BukkitRunnable() {		
            int count = 3;
            
            @Override
            public void run() {	      
                
                if (count == 0) {  
                    p.sendMessage(getLang("enter",p));
                    p.teleport(warzone.getLocation());        		
                    cancel(); 
                    return;
                }	    	    	                                    	    	                        
                if (z != p.getLocation().getBlockZ() || x != p.getLocation().getBlockX()){
                    p.sendMessage(getLang("cant_move",p));
                    cancel();
                    return;
                }
                count--;  
            }
        }.runTaskTimer(plugin, 0L, 20L); 
    }

    public Warzone getWarzone() {
        return warzone;
    }
    //#region run
    @Override
    public void run() {

        if (warzone.getState() == WarzoneState.IN_PROGRESS) {
            return;
        }

        if (warzone.getLocation() == null){
            return;
        }
        if (!plugin.getConfig().getBoolean("automatic_respawn", true)) {
            return;
        }
                
        long actualTime = System.currentTimeMillis() / 60000;
		long lastWarzone = plugin.getConfig().getLong("last_warzone",0);

        long respawnTime = plugin.getConfig().getLong("cooldown_in_minutes",1140);

        if ((actualTime - lastWarzone) >= respawnTime) {
            start();          
        }
    }
    //#region next
    public String getNext() {
        long actualTime = System.currentTimeMillis() / 60000;
        long lastWarzone = plugin.getConfig().getLong("last_warzone",0);
        long respawnTime = plugin.getConfig().getLong("cooldown_in_minutes",1440);
        long time = respawnTime - (actualTime - lastWarzone);

        if (time < 0) {time = 0;}

        long hours = time / 60;
        long minutes = time % 60;
        String text = getLang("next", null).replace("%hours%", String.valueOf(hours)).replace("%min%", String.valueOf(minutes));
    
        return text;

    }

    //#region rewards
    public void proccesRewards(Player p, Location loc) {

        FileConfiguration cfg = plugin.getConfig();

        double chance = Math.random();

        Set<String> keys = cfg.getConfigurationSection("custom_drops").getKeys(false);
        for (String key : keys){
            ItemStack item = cfg.getItemStack("custom_drops." + key + ".item");
            String text = cfg.getString("custom_drops." + key + ".broadcast_text");
            plugin.getLogger().info("chance: " + cfg.getDouble("custom_drops." + key + ".chance") + " >= " + chance);
            if (cfg.getDouble("custom_drops." + key + ".chance") >= chance) {
                
                if (!text.equals("")) {
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', text));
                }
                if (item != null) {
                    loc.getWorld().dropItem(loc, item);
                }
            }
        }

        for (String key : cfg.getConfigurationSection("rewards").getKeys(false)){
					
            String txt = cfg.getString("rewards." + key + ".broadcast_text");
            List<String> drop = cfg.getStringList("rewards." + key + ".drops");
            List<String> commands = cfg.getStringList("rewards." + key + ".commands");
                                
            if (cfg.getDouble("rewards." + key + ".chance") >= chance) {
                                    
                if (!txt.equals("")){ 
                    Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', txt));
                }
                for (String item : drop) {
                    loc.getWorld().dropItemNaturally(loc, new ItemStack(Material.valueOf(item)));
                }
                
                for (String s : commands) {						
                    Bukkit.getServer().dispatchCommand(Bukkit.getConsoleSender(), s.replaceAll("%player%", p.getName()));
                }
            }
        }
    }
}
