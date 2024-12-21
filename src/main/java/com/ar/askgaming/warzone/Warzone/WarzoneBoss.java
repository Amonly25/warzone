package com.ar.askgaming.warzone.Warzone;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.attribute.Attribute;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.entity.Wither;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;
import org.joml.Random;

import com.ar.askgaming.warzone.WarzonePlugin;

import net.md_5.bungee.api.ChatColor;

public class WarzoneBoss {
    
    private String name;
    private double health;
    private Wither whiter;
    private List<String> abilities = new ArrayList<>();

    public Wither getWhiter() {
        return whiter;
    }

    private WarzonePlugin plugin;
    public WarzoneBoss(WarzonePlugin main, Location loc){
        this.plugin = main;

        FileConfiguration conf = main.getConfig();

        name = conf.getString("name");
        health = conf.getDouble("health");

        whiter = loc.getWorld().spawn(loc, Wither.class);
        whiter.setCustomName(name);
        whiter.setCustomNameVisible(true);

        whiter.getAttribute(Attribute.GENERIC_MAX_HEALTH).setBaseValue(health);
        whiter.setHealth(health);

        ConfigurationSection abilities = main.getConfig().getConfigurationSection("abilities");
        if (abilities == null) {
            main.getLogger().warning("No abilities found in config.yml");
            return;
        }
        for (String key : abilities.getKeys(false)) {
            this.abilities.add(key);
            Bukkit.getLogger().info("Ability: " + key);
        }
       
    }
    public void createCounterAttack(EntityDamageByEntityEvent event, Player damager) {

        FileConfiguration config = plugin.getConfig();
        String ability = getRandom();

        String name = ability.toString().toLowerCase();

        double skillChance = config.getDouble("abilities." + name + ".chance",0.3);
        String skillMessage = config.getString("abilities." + name + ".msg_to_player","");

        double random = Math.random();

        if (skillChance >= random) {

            if (!skillMessage.equals("")) {
                damager.sendMessage(ChatColor.translateAlternateColorCodes('&', skillMessage));
            }

            boolean superLightining = config.getBoolean("abilities." + name + ".super_lightning",false);
            if (superLightining) {
                superLightining(damager);
            }
            boolean blockDamage = config.getBoolean("abilities." + name + ".block_damage",false);
            if (blockDamage) {
                event.setCancelled(true);
            }

            int knockback = config.getInt("abilities." + name + ".knockback_power",0);
            if (knockback > 0) {
                damager.setVelocity(damager.getLocation().getDirection().multiply(-knockback));
            }

            double damage = config.getDouble("abilities." + name + ".damage_player",0);
            if (damage > 0) {
                damager.damage(damage);
            }

            boolean explosion = config.getBoolean("abilities." + name + ".explosion",false);
            if (explosion) {
                damager.getWorld().createExplosion(damager.getLocation(), 5, true);
            }
            
            playSoundIfExist(name, damager);  
            addPotionEffect(name, damager);
            sendCommandIfExist(name, damager);
        }
    }

    private void superLightining(Player damager) {
        Location loc = damager.getLocation();
        damager.getWorld().strikeLightning(loc);

        Location[] surroundingLocations = new Location[] {
            loc.clone().add(3, 0, 0), // 2 bloques al este
            loc.clone().add(-3, 0, 0), // 2 bloques al oeste
            loc.clone().add(0, 0, 3), // 2 bloques al sur
            loc.clone().add(0, 0, -3) // 2 bloques al sur
        };

        for (Location l : surroundingLocations) {
            damager.getWorld().strikeLightning(l);
        }
    }
    private void sendCommandIfExist(String name, Player damager) {
        List<String> commands = plugin.getConfig().getStringList("abilities." + name + ".commands");
        if (commands != null) {
            for (String command : commands) {
                plugin.getServer().dispatchCommand(plugin.getServer().getConsoleSender(), command.replace("%player%", damager.getName()));
            }
        }
    }
    private void addPotionEffect(String name, Player damager) {
        List<String> effects = plugin.getConfig().getStringList("abilities." + name + ".effects");
        if (effects != null) {
            for (String effect : effects) {
                String[] split = effect.split(":");
                try {
                    int duration = Integer.parseInt(split[1]);
                    int amplifier = Integer.parseInt(split[2]);
                    PotionEffectType type = org.bukkit.Registry.EFFECT.match(split[0].toUpperCase()); // Check if the effect is in the

                    damager.addPotionEffect(new PotionEffect(type, duration, amplifier));
                } catch (Exception e) {
                    plugin.getLogger().warning("Wrong potion effect format: " + effect);
                    e.printStackTrace();
                }
            }
        }
    }
    private void playSoundIfExist(String name, Player damager) {
        List<String> sound = plugin.getConfig().getStringList("abilities." + name + ".sounds");
        if (sound != null) {
            for (String s : sound) {
                try {
                    damager.playSound(damager.getLocation(), Sound.valueOf(s.toUpperCase()), 3, 1);
                } catch (IllegalArgumentException e) {
                    plugin.getLogger().warning("Invalid sound: " + s);
                    return;
                }
            }
        }
    }
    private String getRandom() {
        int length = abilities.size();
        int randomIndex = new Random().nextInt(length);
        return abilities.get(randomIndex);
    }
}
