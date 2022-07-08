package me.glaremasters.newsbooks.commands;

import cloud.commandframework.arguments.standard.StringArgument;
import cloud.commandframework.paper.PaperCommandManager;
import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.rometools.rome.feed.synd.SyndEntry;
import com.rometools.rome.feed.synd.SyndFeed;
import com.rometools.rome.io.FeedException;
import com.rometools.rome.io.SyndFeedInput;
import com.rometools.rome.io.XmlReader;
import me.glaremasters.newsbooks.Newsbooks;
import net.kyori.adventure.inventory.Book;
import net.kyori.adventure.text.Component;
import net.kyori.adventure.text.event.HoverEvent;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class BookCommand implements NewsCommand {
    private final Newsbooks newsbooks;
    private final Cache<String, SyndFeed> cache = CacheBuilder.newBuilder()
            .expireAfterWrite(5, TimeUnit.MINUTES)
            .build();

    public BookCommand(Newsbooks newsbooks) {
        this.newsbooks = newsbooks;
    }

    @Override
    public void register(PaperCommandManager<CommandSender> manager) {
        manager.command(manager.commandBuilder("newsbook")
                .senderType(Player.class)
                .argument(StringArgument.<CommandSender>newBuilder("site")
                        .withSuggestionsProvider((ctx, label) -> newsbooks.sites().keySet().stream().toList()))
                .handler(context -> {
                    final Player player = (Player) context.getSender();
                    final String site = context.get("site");
                    final Book.Builder book = Book.builder();

                    try {
                        SyndFeed feed;
                        if (cache.asMap().containsKey(site)) {
                            feed = cache.getIfPresent(site);
                        } else {
                            feed = new SyndFeedInput().build(new XmlReader(new URL(newsbooks.sites().get(site))));
                        }
                        cache.put(site, feed);

                        final List<SyndEntry> entries = feed.getEntries();

                        for (final SyndEntry entry : entries) {
                            final Component title = Component.text(entry.getTitle());
                            final Component description = Component.text(entry.getDescription().getValue());

                            book.addPage(title.hoverEvent(HoverEvent.showText(description)));
                        }

                        player.openBook(book.build());
                    } catch (IOException | FeedException ex) {
                        ex.printStackTrace();
                    }
                }));
    }
}
