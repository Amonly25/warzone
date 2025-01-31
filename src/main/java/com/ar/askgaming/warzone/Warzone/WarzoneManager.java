package com.ar.askgaming.warzone.Warzone;

import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Sound;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import com.ar.askgaming.warzone.WarzonePlugin;
import com.ar.askgaming.warzone.CustomEvents.WarzoneStartEvent;

import net.md_5.bungee.api.ChatColor;

public class WarzoneManager extends BukkitRunnable{

    private WarzonePlugin plugin;
    private Warzone warzone;
    private Location location;

    public WarzoneManager(WarzonePlugin main){
        plugin = main;
        location = plugin.getConfig().getLocation("location");

        runTaskTimer(main, 0, 20*60);
    }
        //Methods
    public void start(){

        if (warzone != null){
            return;
        }

        warzone = new Warzone(plugin, location);
        WarzoneStartEvent event = plugin.getWarzoneStartEvent();
        event.setWarzone(warzone);
        Bukkit.getPluginManager().callEvent(event);

        plugin.getLang().langBroadcast("start.message");

        for (Player p : Bukkit.getOnlinePlayers()){
            String title = plugin.getLang().getLang("start.title", p);
            String subtitle = plugin.getLang().getLang("start.subtitle", p);
            p.sendTitle(title, subtitle, 20, 40, 20);
            p.playSound(p, Sound.ENTITY_WITHER_SPAWN, 10, 1);
        }

    }

    public void stop(){

        if (warzone == null){
            return;
        }
        
        if (warzone.getBoss().getWhiter() != null && !warzone.getBoss().getWhiter().isDead()){
            warzone.getBoss().getWhiter().remove();
        }
        for (Player p : Bukkit.getOnlinePlayers()){
            String title = plugin.getLang().getLang("stop.title", p);
            String subtitle = plugin.getLang().getLang("stop.subtitle", p);
            p.sendTitle(title, subtitle, 20, 40, 20);
        }

        plugin.getConfig().set("last_warzone", System.currentTimeMillis() / 60000);
        plugin.saveConfig();
        warzone.cancel();
        warzone = null;
        location.getWorld().setTime(0);
        plugin.getLang().langBroadcast("stop.message");

    }

    public void warp(Player p){

        final int z = p.getLocation().getBlockZ(), x = p.getLocation().getBlockX();
        p.sendMessage(plugin.getLang().getLang("warp",p));

        new BukkitRunnable() {		
            int count = 3;
            
            @Override
            public void run() {	      
                
                if (count == 0) {  
                    p.sendMessage(plugin.getLang().getLang("enter",p));
                    p.teleport(location);        		
                    cancel(); 
                    return;
                }	    	    	                                    	    	                        
                if (z != p.getLocation().getBlockZ() || x != p.getLocation().getBlockX()){
                    p.sendMessage(plugin.getLang().getLang("cant_move",p));
                    cancel();
                    return;
                }
                count--;  
            }
        }.runTaskTimer(plugin, 0L, 20L); 
    }
    public Location getLocation() {
        return location;
    }
    public Warzone getWarzone() {
        return warzone;
    }
    public void setLocation(Location location) {
        plugin.getConfig().set("location", location);
        plugin.saveConfig();
        this.location = location;
    }
    @Override
    public void run() {

        if (getWarzone() != null){
            return;
        }

        if (getLocation() == null){
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
    public String getNext() {
        long actualTime = System.currentTimeMillis() / 60000;
        long lastWarzone = plugin.getConfig().getLong("last_warzone",0);
        long respawnTime = plugin.getConfig().getLong("cooldown_in_minutes",1440);
        long time = respawnTime - (actualTime - lastWarzone);

        if (time < 0) {time = 0;}

        long hours = time / 60;
        long minutes = time % 60;
        String text = plugin.getLang().getLang("next", null).replace("%hours%", String.valueOf(hours)).replace("%min%", String.valueOf(minutes));
    
        return text;

    }

    public Wither getWarzoneBoss() {
        Warzone warzone = plugin.getWarzoneManager().getWarzone();
        if (warzone == null) {
            return null;
        }

        WarzoneBoss boss = warzone.getBoss();
        if (boss == null) {
            return null;
        }

        Wither witherBoss = boss.getWhiter();
        if (witherBoss == null) {
            return null;
        }
        return witherBoss;
    }
    public void proccesRewards(Player p, Location loc) {

        FileConfiguration cfg = plugin.getConfig();

        double chance = Math.random();

        for (String key : cfg.getConfigurationSection("custom_drops").getKeys(false)){
            ItemStack item = plugin.getConfig().getItemStack("custom_drops." + key + ".item");
            String text = cfg.getString("custom_drops." + key + ".broadcast_text");
            if (!text.equals("")) {
                Bukkit.broadcastMessage(ChatColor.translateAlternateColorCodes('&', text));
            }
            if (item != null && cfg.getDouble("custom_drops." + key + ".chance") >= chance) {
                loc.getWorld().dropItemNaturally(loc, item);
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
