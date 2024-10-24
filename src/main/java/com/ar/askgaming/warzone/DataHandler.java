package com.ar.askgaming.warzone;

import java.io.File;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

public class DataHandler {

    private Main plugin;
    public DataHandler(Main main) {

        plugin = main;

        //Save default dragon configuration
        file = new File (plugin.getDataFolder(),"default_boss.yml");
        if (!file.exists()) {
            plugin.saveResource("default_boss.yml", false);
        }
        config = new YamlConfiguration();

        try {
            config.load(file);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private FileConfiguration config;
    private File file;

    public FileConfiguration getConfig(String name) {

        file = new File(plugin.getDataFolder(), name+".yml");

        if (file.exists()) {
            config = new YamlConfiguration();

            try {
                config.load(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }

        return config;
    }
    public void saveConfig(String fileName, FileConfiguration config) {

        file = new File(plugin.getDataFolder(), fileName+".yml");

        if (file.exists()) {
            try {
                config.save(file);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
}
