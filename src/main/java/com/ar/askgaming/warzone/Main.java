package com.ar.askgaming.warzone;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import com.ar.askgaming.warzone.Listeners.EntitySpawnListener;

public class Main extends JavaPlugin{


    //Api variables and methods
    private Warzone warzone;
    private WarzoneBoss warzoneBoss;
    private DataHandler data;

    public DataHandler getDataHandler() {
        return data;
    }

    public WarzoneBoss getWarzoneBoss() {
        return warzoneBoss;
    }

    public Warzone getWarzone() {
        return warzone;
    }

    //PLugin methods
    public void onEnable() {      

        saveDefaultConfig();
        data = new DataHandler(this);

        warzone = new Warzone(this);
        warzoneBoss = new WarzoneBoss(this);

        Bukkit.getPluginCommand("warzone").setExecutor(new Commands(this));

        Bukkit.getPluginManager().registerEvents(new EntitySpawnListener(this), this);
    }

    public void onDisable() {      

    }

    //Get land and set color
     public String getLang(String s){
        if (getConfig().get(s) != null) {
            return ChatColor.translateAlternateColorCodes('&', getConfig().getString(s));
        }
        return "undefined";
    }
}
