package com.ar.askgaming.warzone;

import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

import com.ar.askgaming.warzone.CustomEvents.WarzoneStartEvent;
import com.ar.askgaming.warzone.CustomEvents.WarzoneStartListener;
import com.ar.askgaming.warzone.Listeners.EntityDamageListener;
import com.ar.askgaming.warzone.Listeners.EntityDeathListener;
import com.ar.askgaming.warzone.Listeners.EntityTargetListener;
import com.ar.askgaming.warzone.Misc.LangManager;
import com.ar.askgaming.warzone.Misc.PlaceHolderHook;
import com.ar.askgaming.warzone.Warzone.WarzoneManager;

public class WarzonePlugin extends JavaPlugin{

    private static WarzonePlugin instance;
    //Api variables and methods

    private WarzoneManager warzoneManager;
    private LangManager lang;
    private WarzoneStartEvent warzoneStartEvent;

    public WarzoneStartEvent getWarzoneStartEvent() {
        return warzoneStartEvent;
    }

    //PLugin methods
    public void onEnable() {      

        saveDefaultConfig();
        lang = new LangManager(this);
        instance = this;
        warzoneManager = new WarzoneManager(this);

        new EntityDamageListener(this);
        new EntityDeathListener(this);
        new EntityTargetListener(this);
        new WarzoneStartListener(this);
        warzoneStartEvent = new WarzoneStartEvent();

        new Commands(this);

        if (Bukkit.getPluginManager().getPlugin("PlaceholderAPI") != null) {
            new PlaceHolderHook(this).register();
        }
    }

    public void onDisable() {      
        warzoneManager.stop();
    }

    public WarzoneManager getWarzoneManager() {
        return warzoneManager;
    }
    public LangManager getLang() {
        return lang;
    }
       public static WarzonePlugin getInstance() {
        return instance;
    }

    public HandlerList getWarzoneStartHandlerList() {
        return WarzoneStartEvent.getHandlerList();
    }
}
