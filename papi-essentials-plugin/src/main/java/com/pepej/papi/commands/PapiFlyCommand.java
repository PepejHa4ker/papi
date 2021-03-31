package com.pepej.papi.commands;

import com.pepej.papi.command.Commands;
import com.pepej.papi.terminable.TerminableConsumer;
import com.pepej.papi.terminable.module.TerminableModule;
import com.pepej.papi.utils.StringUtils;
import com.pepej.papi.utils.TabHandlers;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;

import static com.pepej.papi.text.Text.colorize;
import static java.lang.String.format;

public class PapiFlyCommand implements TerminableModule {

    @Override
    public void setup(@NonNull final TerminableConsumer consumer) {
        Commands.create()
                .assertUsage("[on/off] [player]")
                .assertPermission("papi.essentials.commands.fly")
                .description("Fly command")
                .tabHandler(context -> {
                    if (context.args().size() == 1) {
                        return TabHandlers.onOff(context.rawArg(0));
                    } else {
                        return TabHandlers.players(context.rawArg(1));
                    }
                })
                .handler(context -> {
                    if (context.args().size() < 2) {
                        if (context.sender() instanceof ConsoleCommandSender) {
                            context.replyError("С консоли можно использовать только на других!");
                        } else {
                            Player sender = (Player) context.sender();

                            if (context.args().size() == 1) {
                                boolean state = StringUtils.parseBoolean(context.rawArg(0));
                                toggleFly(sender, state);
                            } else {
                                toggleFly(sender, !sender.getAllowFlight());
                            }
                        }
                    } else {
                        Player player = context.arg(1).parseOrFail(Player.class);
                        boolean state = StringUtils.parseBoolean(context.rawArg(0));
                        toggleFly(player, state);
                        String color = state ? "&a" : "&c";
                        String message = state ? "включен" : "выключен";
                        if (!context.sender().equals(player)) {
                            context.replyAnnouncement(format("Полёт для %s теперь %s%s", player.getName(), color, message));
                        }
                    }

                })
                .registerAndBind(consumer, "fly", "f", "полет", "полёт");

    }

    private static void toggleFly(Player player, boolean state) {
        player.setFallDistance(0);
        player.setAllowFlight(state);
        if (!player.getAllowFlight()) {
            player.setFlying(false);
        }
        String color = state ? "&a" : "&c";
        String message = state ? "включен" : "выключен";
        player.sendMessage(colorize(format("&b∗&a Полёт теперь %s%s", color, message)));

    }
}
