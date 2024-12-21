package com.ar.askgaming.warzone.Warzone;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import com.ar.askgaming.warzone.WarzonePlugin;
import com.ar.askgaming.warzone.CustomEvents.WarzoneEndEvent;
import com.ar.askgaming.warzone.CustomEvents.WarzoneStartEvent;

public class WarzoneManager {

    private WarzonePlugin plugin;
    private Warzone warzone;
    private Location location;

    public WarzoneManager(WarzonePlugin main){
        plugin = main;
        location = plugin.getConfig().getLocation("location");
    }
        //Methods
    public void start(){
        warzone = new Warzone(plugin, location);
        plugin.getLang().langBroadcast("start.message");

        for (Player p : Bukkit.getOnlinePlayers()){
            String title = plugin.getLang().getLang("start.title", p);
            String subtitle = plugin.getLang().getLang("start.subtitle", p);
            p.sendTitle(title, subtitle, 20, 40, 20);
            p.playSound(p, Sound.ENTITY_WITHER_SPAWN, 10, 1);
        }
        WarzoneStartEvent event = new WarzoneStartEvent(warzone);
        Bukkit.getPluginManager().callEvent(event);

    }

    public void stop(){

        if (warzone.getBoss().getWhiter() != null && !warzone.getBoss().getWhiter().isDead()){
            warzone.getBoss().getWhiter().remove();
        }
        for (Player p : Bukkit.getOnlinePlayers()){
            String title = plugin.getLang().getLang("stop.title", p);
            String subtitle = plugin.getLang().getLang("stop.subtitle", p);
            p.sendTitle(title, subtitle, 20, 40, 20);
        }
        WarzoneEndEvent event = new WarzoneEndEvent(warzone);
        Bukkit.getPluginManager().callEvent(event);
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

}
