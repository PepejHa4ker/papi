package com.pepej.papi.commands;

import com.pepej.papi.command.Commands;
import com.pepej.papi.terminable.TerminableConsumer;
import com.pepej.papi.terminable.module.TerminableModule;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import static com.pepej.papi.text.Text.colorize;
import static java.lang.String.format;

public class PapiFeedCommand implements TerminableModule {

    @Override
    public void setup(@NonNull final TerminableConsumer consumer) {
        Commands.create()
                .assertUsage("[player]")
                .assertPermission("papi.essentials.commands.feed")
                .description("Feed command")
                .handler(context -> {
                    if (context.args().isEmpty()) {
                        if (context.sender() instanceof ConsoleCommandSender) {
                            context.replyError("С консоли можно использовать только на других!");
                        } else {
                            Player sender = (Player) context.sender();
                            feed(sender);

                        }
                    } else {
                        Player player = context.arg(0).parseOrFail(Player.class);
                        feed(player);
                        if (!context.sender().equals(player)) {
                            context.replyAnnouncement(format("Еда для %s была успешно восстановлена", player.getName()));
                        }

                    }

                })
                .registerAndBind(consumer, "feed", "еда");
    }


    private static void feed(Player player) {
        player.setSaturation(20);
        player.setFoodLevel(20);
        player.setExhaustion(0F);
        player.sendMessage(colorize("&b∗&a Еда была успешно восстановлена"));
    }
}
