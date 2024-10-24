package com.ar.askgaming.warzone;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

public class Warzone {

    // Contructor
    private Main plugin;
    public Warzone(Main main){
        plugin = main;

        //Load variables from config
        location = plugin.getConfig().getLocation("location");

    }

    //Variables
    private Location location;
    private Boolean inProgress = false;

    //Setter and getter

    public Boolean getInProgress() {
        return inProgress;
    }
    public Location getLocation() {
        return location;
    }
    public void setLocation(Location location) {
        plugin.getConfig().set("location", location);
        plugin.saveConfig();
        this.location = location;
    }

    //Methods
    public void start(){
        inProgress = true;
        Bukkit.broadcastMessage(plugin.getLang("messages.start"));

        String title = plugin.getLang("start.title");
        String subtitle = plugin.getLang("start.subtitle");

        Bukkit.getOnlinePlayers().forEach(player -> {
            player.sendTitle(title, subtitle, 20, 40, 20);
            //Execute commands maybe?
            //
            player.playSound(player, Sound.ENTITY_WITHER_SPAWN, 10, 1);
        });

        location.getWorld().setTime(14000);
        plugin.getWarzoneBoss().spawnRandom();

    }

    public void stop(){
        inProgress = false;
        plugin.getWarzoneBoss().getWithersAlive().forEach(wither -> {
            if (plugin.getWarzoneBoss().isWarzoneWitherBoss(wither)){
                wither.setHealth(0);
            }
        });
        location.getWorld().setTime(0);
        Bukkit.broadcastMessage(plugin.getLang("messages.end"));

    }

    public void warp(Player p){

        if (location == null) {
            p.sendMessage("There is no location of warzone available");
            return;
        }

        final int z = p.getLocation().getBlockZ(), x = p.getLocation().getBlockX();

        new BukkitRunnable() {		
            int count = 3;
            
            @Override
            public void run() {	      
                
                if (count == 0) {  
                    p.sendMessage(plugin.getLang("messages.enter"));
                    p.teleport(getLocation());        		
                    cancel(); 
                    return;
                }	    	    	                                    	    	                        
                if (z != p.getLocation().getBlockZ() || x != p.getLocation().getBlockX()){
                    p.sendMessage(plugin.getLang("messages.cant_move"));
                    cancel();
                    return;
                }
                count--;  
            }
        }.runTaskTimer(plugin, 0L, 20L); 
    }
}
