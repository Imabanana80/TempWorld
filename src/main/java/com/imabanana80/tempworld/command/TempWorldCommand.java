package com.imabanana80.tempworld.command;

import com.imabanana80.tempworld.TempWorld;
import com.imabanana80.tempworld.manager.WorldManager;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.ClickEvent;
import net.kyori.adventure.text.format.NamedTextColor;
import net.kyori.adventure.text.format.TextDecoration;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.incendo.cloud.annotations.Argument;
import org.incendo.cloud.annotations.Command;
import org.incendo.cloud.annotations.Permission;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class TempWorldCommand {
    @Command("tempworld [name]")
    @Permission("tempworld.create")
    public void onTempworldCreate(
            CommandSender sender,
            @Argument( value = "name" ) String name
    ) {
        if (sender instanceof Player player) {
            if (WorldManager.getCooldown()) {
                player.sendMessage(Component.text("Another temporary world was just created! Please wait a moment and try again.", NamedTextColor.RED));
                return;
            }
            player.sendMessage(Component.text("Creating temporary world... please wait a moment.", NamedTextColor.GREEN));
            player.sendMessage(Component.text("Warning: worlds are automatically deleted on server stop!", NamedTextColor.DARK_RED));
            Location location = WorldManager.createNewWorld(name);
            Bukkit.getScheduler().runTaskTimer(TempWorld.getInstance(), task -> {
                if (location.isWorldLoaded()) {
                    player.teleport(location);
                    player.sendMessage(Component.text("Teleporting...", NamedTextColor.DARK_GRAY));
                    task.cancel();
                }
            }, 0, 1);
        }
    }

    @Command("worlds")
    @Permission("tempworld.list")
    public void onTempworldList(
            CommandSender sender
    ) {
        if (sender instanceof Player player) {
            HashMap<String, World> worlds = WorldManager.getWorlds();
            if (worlds.isEmpty()) {
                player.sendMessage(Component.text("There are no active temporary worlds!", NamedTextColor.AQUA));
                return;
            }
            player.sendMessage(Component.text("There are " + worlds.size() + " active temporary worlds: ", NamedTextColor.AQUA));
            player.sendMessage(Component.text("                          ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.STRIKETHROUGH, true));
            for (String name : worlds.keySet()) {
                World world = worlds.get(name);
                List<String> players = new ArrayList<>();
                for (Player target : world.getPlayers()) {
                    players.add(target.getName());
                }
                player.sendMessage(Component.text("World: " + name, NamedTextColor.GREEN)
                        .hoverEvent(Component.text("Click to teleport"))
                        .clickEvent(ClickEvent.clickEvent(ClickEvent.Action.RUN_COMMAND, "/world " + name)));
                player.sendMessage(Component.text("Id: " + world.getName().replace("world-", ""), NamedTextColor.GRAY));
                if (!players.isEmpty()) {
                    player.sendMessage(Component.text("Players: " + players, NamedTextColor.YELLOW));
                } else {
                    player.sendMessage(Component.text("Players: No connected players!", NamedTextColor.YELLOW));
                }
                player.sendMessage(Component.text("                          ", NamedTextColor.DARK_GRAY).decoration(TextDecoration.STRIKETHROUGH, true));
            }
        }
    }

    @Command("world <name>")
    @Permission("tempworld.teleport")
    public void onTempworldTeleport(
            CommandSender sender,
            @Argument( value = "name" ) String name
    ) {
        System.out.println(sender);
        if (sender instanceof Player player) {
            Location location = WorldManager.getWorld(name);
            if (location == null) {
                player.sendMessage(Component.text("Invalid World Name! Run /worlds to see a list", NamedTextColor.RED));
            }
            Bukkit.getScheduler().runTaskTimer(TempWorld.getInstance(), task -> {
                if (location.isWorldLoaded()) {
                    player.teleport(location);
                    player.sendMessage(Component.text("Teleporting...", NamedTextColor.DARK_GRAY));
                    task.cancel();
                }
            }, 0, 1);
        }
    }

    @Command("cleanworlds")
    @Permission("tempworld.clean")
    public void onTempworldCleanAll(
            CommandSender sender
    ) {
        WorldManager.scrubWorlds();
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (player.hasPermission("tempworld.clean")) {
                player.sendMessage(Component.text("Scrubbing all temporary worlds!", NamedTextColor.AQUA));
            }
        }
    }
}
