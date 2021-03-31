package com.pepej.papi.commands;

import com.pepej.papi.command.Commands;
import com.pepej.papi.terminable.TerminableConsumer;
import com.pepej.papi.terminable.module.TerminableModule;
import com.pepej.papi.utils.TabHandlers;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;

import static java.lang.String.format;

public class PapiGiveExpCommand implements TerminableModule {

    public void setup(@NonNull TerminableConsumer consumer) {
        Commands.create()
                .assertUsage("<exp> [player]")
                .assertPermission("papi.essentials.command.exp")
                .description("Give experience command")
                .tabHandler(context -> {
                    if (context.args().size() == 1) {
                        return Collections.emptyList();
                    } else {
                        return TabHandlers.players(context.rawArg(1));
                    }
                })
                .handler(context -> {
                    int exp = context.arg(0).parseOrFail(Integer.class);
                    if (context.args().size() > 1) {
                        Player player = context.arg(1).parseOrFail(Player.class);
                        player.giveExp(exp);
                        context.replyAnnouncement(format("Успешно выдано %s опыта игроку %s", exp, player.getName()));
                    } else {
                        ((Player) context.sender()).giveExp(exp);
                        context.replyAnnouncement(format("Успешно выдано %s опыта", exp));

                    }

                })
                .registerAndBind(consumer, "giveexp", "giveexperience");
    }
}