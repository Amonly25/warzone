package com.ar.askgaming.warzone.Warzone;

import java.util.Optional;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import com.ar.askgaming.warzone.WarzonePlugin;

public class Warzone extends BukkitRunnable{

    // Contructor
    private WarzoneBoss boss;

    public Warzone(WarzonePlugin main, Location location){
        this.location = location;

        location.getWorld().setTime(14000);
        runTaskTimer(main, 0, 20);

        boss = new WarzoneBoss(main, location);
    }

    private Location location;

    public Location getLocation() {
        return location;
    }
    public WarzoneBoss getBoss() {
        return boss;
    }
    @Override
    public void run() {
        Location loc = Optional.ofNullable(boss.getWhiter().getLocation()).orElse(location);
        if (loc == null) {
            return;
        }

        Location strikeLocation = loc.clone().add((Math.random() - 0.5) * 64, 0, (Math.random() - 0.5) * 64);
        location.getWorld().strikeLightningEffect(strikeLocation);
    }
    
}
