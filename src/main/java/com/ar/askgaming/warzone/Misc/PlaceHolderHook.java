package com.ar.askgaming.warzone.Misc;

import java.util.ArrayList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;

import org.bukkit.OfflinePlayer;

import com.ar.askgaming.warzone.WarzonePlugin;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;

public class PlaceHolderHook extends PlaceholderExpansion{

    private WarzonePlugin plugin;
    public PlaceHolderHook(WarzonePlugin plugin){
        this.plugin = plugin;
    }

    @Override
    public String getAuthor() {
        return "AskGaming";
    }
    
    @Override
    public String getIdentifier() {
        return "warzone";
    }

    @Override
    public String getVersion() {
        return "1.0.0";
    }
    
    @Override
    public boolean persist() {
        return true; // This is required or else PlaceholderAPI will unregister the Expansion on reload
    }
    
    @Override
    public String onRequest(OfflinePlayer player, String params) {

        switch (params) {

            case "next":
                return plugin.getWarzoneManager().getNext();

            default:
                return "Invalid Placeholder";
        }

    }
}
