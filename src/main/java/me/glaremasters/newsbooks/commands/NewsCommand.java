package me.glaremasters.newsbooks.commands;

import cloud.commandframework.paper.PaperCommandManager;
import org.bukkit.command.CommandSender;

public interface NewsCommand {

    void register(final PaperCommandManager<CommandSender> manager);

}
