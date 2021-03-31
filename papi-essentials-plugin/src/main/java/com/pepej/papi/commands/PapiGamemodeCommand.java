package com.pepej.papi.commands;

import com.pepej.papi.command.CommandInterruptException;
import com.pepej.papi.command.Commands;
import com.pepej.papi.command.functional.handler.FunctionalTabHandler;
import com.pepej.papi.terminable.TerminableConsumer;
import com.pepej.papi.terminable.module.TerminableModule;
import com.pepej.papi.utils.Players;
import com.pepej.papi.utils.TabHandlers;
import org.bukkit.GameMode;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;
import java.util.Locale;

import static com.pepej.papi.text.Text.colorize;
import static java.util.stream.Collectors.toList;

public class PapiGamemodeCommand implements TerminableModule {

    @SuppressWarnings("deprecation")
    public void setup(@NonNull TerminableConsumer consumer) {

        Commands.create()
                .assertUsage("<mode> [player]")
                .description("Gamemode command")
                .assertPermission("papi.essentials.commands.gamemode")
                .tabHandler(context -> {
                    if (context.args().size() == 1) {
                        return TabHandlers.gamemodes(context.rawArg(0));
                    }
                    else {
                        return TabHandlers.players(context.rawArg(1));
                    }
                })
                .handler(context -> {
                    GameMode gameMode = context.arg(0)
                                               .parse(GameMode.class)
                                               .orElseGet(() -> {
                                                   try {
                                                       return GameMode.getByValue(context.arg(0).parseOrFail(Integer.class));
                                                   } catch (CommandInterruptException ignored) {
                                                   }
                                                   return null;
                                               });
                    if (gameMode == null) {
                        context.replyError("Неверно указан режим");
                        return;
                    }

                    if (context.args().size() == 2) {
                        Player player = context.arg(1).parseOrFail(Player.class);
                        player.setGameMode(gameMode);
                        if (!context.sender().equals(player)) {
                            player.sendMessage(colorize("&b∗&a Ваш игровой режим теперь: " + gameMode.name().toLowerCase(Locale.ROOT)));
                        }

                    } else {
                        if (context.sender() instanceof ConsoleCommandSender) {
                            context.replyError("С консоли можно использовать только на других!");
                        }
                        else {
                            Player player = (Player) context.sender();
                            player.setGameMode(gameMode);
                            player.sendMessage(colorize("&b∗&a Ваш игровой режим теперь: " + gameMode.name().toLowerCase(Locale.ROOT)));
                            return;
                        }
                    }
                    context.replyAnnouncement("Игровой режим установлен на: " + gameMode.name().toLowerCase(Locale.ROOT));
                })
                .registerAndBind(consumer, "gm", "gamemode", "гм", "режим");
    }
}