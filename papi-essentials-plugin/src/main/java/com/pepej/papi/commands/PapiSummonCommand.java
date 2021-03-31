package com.pepej.papi.commands;

import com.pepej.papi.command.Commands;
import com.pepej.papi.terminable.TerminableConsumer;
import com.pepej.papi.terminable.module.TerminableModule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import static java.lang.String.format;

public class PapiSummonCommand implements TerminableModule {

    public void setup(@NonNull TerminableConsumer consumer) {

        Commands.create()
                .description("Summon command")
                .assertPlayer()
                .assertPermission("papi.essentials.commands.summon")
                .assertUsage("<player>")
                .handler(context -> {
                    Player summoned = context.arg(0).parseOrFail(Player.class);
                    summoned.teleport(context.sender());
                    context.replyAnnouncement(format("Игрок %s успешно призван", summoned.getName()));

                })
                .registerAndBind(consumer, "s", "sum", "призвать");
    }
}