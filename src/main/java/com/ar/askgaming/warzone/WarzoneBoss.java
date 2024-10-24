package com.ar.askgaming.warzone;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Wither;

public class WarzoneBoss {
    
    private String name;
    private double health;
    private UUID uuid;
    private Wither whiter;

    private Main plugin;
    public WarzoneBoss(Main main){
        plugin = main;

        //Load default boss properties
        FileConfiguration conf = plugin.getDataHandler().getConfig("default_boss");

        name = conf.getString("name");
        health = conf.getDouble("health");
       
    }

    public void spawn(String bossName){
        if (exists(bossName)) {
            Location l = plugin.getWarzone().getLocation();

            if (l == null){
                plugin.getLogger().warning("Warzone location not found, cant respawn bosses.");
                return;
            }

            whiter = (Wither) plugin.getWarzone().getLocation().getWorld().spawnEntity(l,EntityType.WITHER);
            bossesAlive.add(whiter);

            loadConfig(bossName);
        }
    }

    private void loadConfig(String bossName){
        FileConfiguration conf = plugin.getDataHandler().getConfig(bossName);

        name = conf.getString("name","Undefined");
        health = conf.getDouble("health",200);

        conf.set("uuid", whiter.getUniqueId().toString());
        plugin.getDataHandler().saveConfig(bossName, conf);

        whiter.setCustomName(name);
        whiter.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        whiter.setHealth(health);	
    }

    public boolean exists(String bossName) {
        File dragon = new File(plugin.getDataFolder(), bossName+".yml");

        if (dragon.exists()){
            return true;
        } else return false;
    }

    private List<Wither> bossesAlive = new ArrayList<>();

    public List<Wither> getWithersAlive(){

        Bukkit.getWorlds().forEach(world ->{

            for (LivingEntity entity : world.getLivingEntities()) {
            
                if (entity instanceof Wither) {
                    bossesAlive.add((Wither) entity);
                }
            }
        }); 

        return bossesAlive;
    }

    public void createBoss(String bossName){

    }

    public boolean isWarzoneWitherBoss(Wither wither){
        File directory = plugin.getDataFolder();

        if (directory.exists()) {
            // Obtén una lista de todos los archivos y carpetas en la carpeta
            File[] filesList = directory.listFiles();
            
            if (filesList != null) {
                // Itera sobre el arreglo de archivos
                for (File file : filesList) {
                    if (file.isFile() && file.getName().endsWith(".yml")) {
                        if (isValidYamlConfig(file,wither)) {
                            return true;
                        } 
                    }
                }
            }
        }
        return false;
    }
    private boolean isValidYamlConfig(File file,Wither wither) {
        FileConfiguration config = new YamlConfiguration();
        try {
            config.load(file);
            
            // Verifica si contiene las claves esperadas
            if (config.contains("uuid")) {
                if (config.getString("uuid").equalsIgnoreCase(wither.getUniqueId().toString())){
                    return true;
                }
            }
        } catch (IOException | InvalidConfigurationException | IllegalArgumentException e) {
            plugin.getLogger().severe("Error in configuration " + file.getName() + ": " + e.getMessage());
        }
        return false;
    }
    public void spawnRandom() {
        // TODO get all types of boss
        spawn("default_boss");
    }
}
