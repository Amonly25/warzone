package com.ar.askgaming.warzone;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;

public class Commands implements TabExecutor {

    //Contructor
    private Main plugin;
    public Commands(Main main) {
        plugin = main;
    }

    //TabCompleter Metho
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        
        List<String> result = new ArrayList<String>();

        if (args.length == 1) {
            result = new ArrayList<>(Arrays.asList("set_spawn","warp","spawn","start","stop"));
        }
        if (args.length == 2) {
            result = new ArrayList<>(Arrays.asList("default_boss"));
        }

        return result;
    }

    //Command Method
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if (args.length == 0) {
            sender.sendMessage("Use /warzone <subcommand>");
            return true;
        }
        if (!(sender instanceof Player)){
            sender.sendMessage("This commands must be sended by a player");
            return true;
        }
        Player player = (Player) sender;

        if (args.length == 1) {

            if (player.hasPermission("warzone.warp")) {
						
                if (args[0].equalsIgnoreCase("warp")) {
                    player.sendMessage(plugin.getLang("messages.warp"));
                    plugin.getWarzone().warp(player);
                    return true;
                }
            } else {
                player.sendMessage(plugin.getLang("messages.no_permission"));
            }

            switch (args[0].toLowerCase()) {
                case "set_spawn":
                    plugin.getWarzone().setLocation(player.getLocation());
                    player.sendMessage("You set the warzone location");
            
                break;
                case "start":
                    if (plugin.getWarzone().getLocation() !=null){
                        if (!plugin.getWarzone().getInProgress()){
                            plugin.getWarzone().start();
                        }
                    } else player.sendMessage("The warzone location is not seted correctly");
            
                break;
                case "stop":
                    if (plugin.getWarzone().getInProgress()){
                        plugin.getWarzone().stop();
                    }         
                break;
                case "test":
                    player.sendMessage(player.getUniqueId().toString());
        
                break;
                default:
                    sender.sendMessage("Unknown subcommand");
                break;
               
            }            
        }
        if (args.length == 2) {

            switch (args[0].toLowerCase()) {
                case "spawn":
                    if (plugin.getWarzoneBoss().exists(args[1].toLowerCase())) {
                        plugin.getWarzoneBoss().spawn(args[1].toLowerCase());

                    } else player.sendMessage("No boss exists with the specified name.");
                break;
                case "kill":

                    if (plugin.getWarzoneBoss().exists(args[1].toLowerCase())) {
                        String uuid = plugin.getDataHandler().getConfig(args[1].toLowerCase()).getString("uuid");

                        plugin.getWarzoneBoss().getWithersAlive().forEach(wither ->{
                            if (uuid.equals(wither.getUniqueId().toString())){
                                wither.setHealth(0);
                                sender.sendMessage("A boss has been killed by the server.");
                            }
                        });
                    }                
                break;
            }
            
        }
        return false;
    }
}
