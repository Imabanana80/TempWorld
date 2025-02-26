package com.imabanana80.tempworld.manager;

import com.imabanana80.tempworld.TempWorld;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.WorldCreator;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.UUID;

public class WorldManager {

    private static HashMap<String, World> worlds = new HashMap<>();
    private static boolean cooldown;

    public static Location createNewWorld(String name) {
        cooldown = true;
        Bukkit.getScheduler().runTaskLater(TempWorld.getInstance(), () -> {
            cooldown = false;
        },20*10);
        String Id = UUID.randomUUID().toString();
        if (name == null) {
            name = Id;
        }
        World world = Bukkit.createWorld(new WorldCreator("world-" + Id));
        worlds.put(name, world);
        return world.getSpawnLocation();
    }

    public static Location getWorld(String name) {
        World world = worlds.get(name);
        if (world != null) {
            return world.getSpawnLocation();
        }
        return null;
    }
    public static HashMap<String, World> getWorlds() {return worlds;}
    public static boolean getCooldown() {return cooldown;}
    public static void scrubWorlds() {
        Location spawn = Bukkit.getWorlds().getFirst().getSpawnLocation();
        for (World world : worlds.values()) {
            for (Player player : world.getPlayers()) {
                player.teleport(spawn);
            }
            world.getWorldFolder().delete();
        }
    }
}
