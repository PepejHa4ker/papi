package com.pepej.papi.commands;

import com.pepej.papi.command.Commands;
import com.pepej.papi.terminable.TerminableConsumer;
import com.pepej.papi.terminable.module.TerminableModule;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

public class PapiTpCommand implements TerminableModule {

    public void setup(@NonNull TerminableConsumer consumer) {

        Commands.create()
                .description("Tp command")
                .assertPlayer()
                .assertPermission("papi.essentials.commands.tp")
                .assertUsage("<player/location>")
                .handler(context -> {
                    Location to;
                    if (context.arg(0).parse(Player.class).isPresent()) {
                        to = context.arg(0).parseOrFail(Player.class).getLocation();
                    } else {
                        double x = context.arg(0).parseOrFail(Double.class);
                        double y = context.arg(1).parseOrFail(Double.class);
                        double z = context.arg(2).parseOrFail(Double.class);
                        World world = context.arg(3).parse(World.class).orElse(context.sender().getWorld());
                        to = new Location(world, x, y, z);
                    }
                    context.sender().teleport(to);
                    context.replyAnnouncement("Вы были успешно телепортированы");

                })
                .registerAndBind(consumer, "tp", "teleport", "тп", "телепорт");

    }
}