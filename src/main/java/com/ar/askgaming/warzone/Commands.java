package com.ar.askgaming.warzone;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import com.ar.askgaming.warzone.Warzone.Warzone;
import com.ar.askgaming.warzone.Warzone.WarzoneManager.WarzoneState;

public class Commands implements TabExecutor {

    //Contructor
    private WarzonePlugin plugin;
    public Commands(WarzonePlugin main) {
        plugin = main;

        plugin.getServer().getPluginCommand("warzone").setExecutor(this);
    }
    private Warzone getWarzone(){
        return plugin.getWarzoneManager().getWarzone();
    }
    private String getLang(String key, Player p){
        return plugin.getLang().getFrom(key, p);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        
        if (args.length == 1) {
            List<String> list = new ArrayList<>();
            list.add("status");
            list.add("warp");
            if (sender.hasPermission("warzone.admin")) {
                return List.of("set","warp","start","stop","status","add_custom_drop","test_rewards","reload","help");
            }
            return list;
        }
        return null;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage("Use /warzone <help>");
            return true;
        }
        if (!(sender instanceof Player)){
            sender.sendMessage("This commands must be sended by a player");
            return true;
        }
        Player player = (Player) sender;

        switch (args[0].toLowerCase()) {
            case "set":
                set(player, args);
                break;
            case "warp":
                warp(player);
                break;
            case "start":
                start(player, args);
                break;
            case "stop":
                stop(player, args);
                break;
            case "status":
                status(player);
                break;
            case "add_custom_drop": 
                addCustomDrop(player, args);
                break;
            case "test_rewards":
                testRewards(player, args);
                break;
            case "reload":
                plugin.reloadConfig();
                plugin.getWarzoneManager().getWarzone().loadConfig();
                plugin.getLang().load();
                player.sendMessage("Config reloaded");
                break;
            case "help":
            default:
                help(player);
                break;
        }
        return true;
    }
    //#region warp
    private void warp(Player p){

        if (getWarzone().getLocation() == null){
            p.sendMessage("§cThe warzone location is not seted correctly");
            return;
        }

        if (!p.hasPermission("warzone.warp")) {
            p.sendMessage(getLang("no_permission", p));
        }

        plugin.getWarzoneManager().warp(p);
    }
    //#region set
    private void set(Player p,String[] args){
        if (!p.hasPermission("warzone.admin")) {
            p.sendMessage(getLang("no_permission", p));
            return;
        }
        Location loc = p.getLocation();
        getWarzone().setLocation(loc);
        p.sendMessage("§aYou set the warzone location successfully.");
    }
    //#region start
    private void start(Player p, String[] args){
        if (!p.hasPermission("warzone.admin")) {
            p.sendMessage(getLang("no_permission", p));
            return;
        }
        if (getWarzone().getLocation() == null){
            p.sendMessage("§cThe warzone location is not seted yet");
            return;

        }
        if (getWarzone().getState() == WarzoneState.IN_PROGRESS){
            p.sendMessage("§cThe warzone is already in progress");
            return;
        }
        plugin.getWarzoneManager().start();
    }
    //#region stop
    private void stop(Player p, String[] args){
        if (!p.hasPermission("warzone.admin")) {
            p.sendMessage(getLang("no_permission", p));
            return;
        }
        if (plugin.getWarzoneManager().getWarzone() == null){
            p.sendMessage("§cThe warzone is not in progress");
            return;
        }

        plugin.getWarzoneManager().stop();
    }
    //#region status
    private void status(Player p){
        boolean status = getWarzone().getState() == WarzoneState.WAINTING;
        if (status){
            p.sendMessage("Next: " + plugin.getWarzoneManager().getNext());
        }
        p.sendMessage(getLang("status", p).replace("%status%", status ? "§cOff" : "§cActive!"));
    }
    //#region help
    private void help(Player p){
        p.sendMessage("Warzone commands:");
        p.sendMessage("/warzone set - Set the warzone location");
        p.sendMessage("/warzone warp - Warp to the warzone");
        p.sendMessage("/warzone start - Start the warzone");
        p.sendMessage("/warzone stop - Stop the warzone");
        p.sendMessage("/warzone status - Show the warzone status");

    }
    //#region add_custom_drop
    private void addCustomDrop(Player p, String[] args){
        if (!p.hasPermission("warzone.admin")) {
            p.sendMessage(getLang("no_permission", p));
            return;
        }
        ItemStack item = p.getInventory().getItemInMainHand();
        if (item != null){
            String id = System.currentTimeMillis() + "";
            plugin.getConfig().set("custom_drops." + id + ".item", item);
            plugin.getConfig().set("custom_drops." + id + ".chance", 0.5);
            plugin.getConfig().set("custom_drops." + id + ".broadcast_text", "");
            plugin.saveConfig();
            p.sendMessage("Item added to custom drops to the config, see to edit chance and broadcast text.");
        } else p.sendMessage("You must hold an item in your hand.");
    }
    private void testRewards(Player p, String[] args){
        if (!p.hasPermission("warzone.admin")) {
            p.sendMessage(getLang("no_permission", p));
            return;
        }
        plugin.getWarzoneManager().proccesRewards(p, p.getLocation());
    }
}
