package com.imabanana80.tempworld;

import com.imabanana80.tempworld.command.TempWorldCommand;
import com.imabanana80.tempworld.manager.WorldManager;
import net.kyori.adventure.text.Component;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.java.JavaPlugin;
import org.incendo.cloud.SenderMapper;
import org.incendo.cloud.annotations.AnnotationParser;
import org.incendo.cloud.bukkit.CloudBukkitCapabilities;
import org.incendo.cloud.execution.ExecutionCoordinator;
import org.incendo.cloud.paper.LegacyPaperCommandManager;

import java.util.logging.Level;

public class TempWorld extends JavaPlugin {

    private static TempWorld instance;

    @Override
    public void onEnable(){
        instance = this;
        final LegacyPaperCommandManager<CommandSender> commandManager = new LegacyPaperCommandManager<>(
                instance,
                ExecutionCoordinator.simpleCoordinator(),
                SenderMapper.identity()
        );
        if (commandManager.hasCapability(CloudBukkitCapabilities.NATIVE_BRIGADIER)){
            commandManager.registerBrigadier();
        } else if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
            commandManager.registerAsynchronousCompletions();
        }
        //this.bukkitAudiences = BukkitAudiences.create(this); //enable if not paper
        AnnotationParser<CommandSender> parser = new AnnotationParser<>(commandManager, CommandSender.class);
        parser.parse(new TempWorldCommand());

        if (!Bukkit.getServer().getName().equalsIgnoreCase("paper")){
            instance.getLogger().log(Level.SEVERE, "Server is not running paper and thus may not be supported by this plugin!");
        }
    }

    @Override
    public void onDisable(){
        WorldManager.scrubWorlds();
    }

    public static TempWorld getInstance(){return instance;}
}
