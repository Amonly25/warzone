package com.ar.askgaming.warzone;

import org.bukkit.plugin.java.JavaPlugin;

import com.ar.askgaming.warzone.Listeners.EntityDamageListener;
import com.ar.askgaming.warzone.Listeners.EntityDeathListener;
import com.ar.askgaming.warzone.Misc.Commands;
import com.ar.askgaming.warzone.Misc.LangManager;
import com.ar.askgaming.warzone.Warzone.WarzoneManager;

public class WarzonePlugin extends JavaPlugin{


    //Api variables and methods

    private WarzoneManager warzoneManager;
    private LangManager lang;

    //PLugin methods
    public void onEnable() {      

        saveDefaultConfig();
        lang = new LangManager(this);

        warzoneManager = new WarzoneManager(this);

        new EntityDamageListener(this);
        new EntityDeathListener(this);

        new Commands(this);

    }

    public void onDisable() {      

    }

    public WarzoneManager getWarzoneManager() {
        return warzoneManager;
    }
    public LangManager getLang() {
        return lang;
    }
}
