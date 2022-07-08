package me.glaremasters.newsbooks;

import cloud.commandframework.bukkit.CloudBukkitCapabilities;
import cloud.commandframework.execution.CommandExecutionCoordinator;
import cloud.commandframework.paper.PaperCommandManager;
import me.glaremasters.newsbooks.commands.BookCommand;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;
import java.util.Map;

public final class Newsbooks extends JavaPlugin {
    private final Map<String, String> sites = new HashMap<>();

    @Override
    public void onEnable() {
        saveDefaultConfig();
        loadConfig();

        registerCommands();
    }

    @Override
    public void onDisable() {
    }

    private void loadConfig() {
        final ConfigurationSection section = this.getConfig().getConfigurationSection("sources");

        for (final String site : section.getKeys(false)) {
            this.sites.put(site, section.getString(site));
        }
    }

    private void registerCommands() {
        final PaperCommandManager<CommandSender> commandManager;

        try {
            commandManager = PaperCommandManager.createNative(
                    this, CommandExecutionCoordinator.simpleCoordinator()
            );

            if (commandManager.hasCapability(CloudBukkitCapabilities.ASYNCHRONOUS_COMPLETION)) {
                commandManager.registerAsynchronousCompletions();
            }

            new BookCommand(this).register(commandManager);
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    public Map<String, String> sites() {
        return sites;
    }
}
