package com.pepej.papi.commands;

import com.pepej.papi.Papi;
import com.pepej.papi.command.Commands;
import com.pepej.papi.command.functional.handler.FunctionalTabHandler;
import com.pepej.papi.terminable.TerminableConsumer;
import com.pepej.papi.terminable.module.TerminableModule;
import com.pepej.papi.utils.TabHandlers;
import org.bukkit.Location;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.NonNull;

import static java.lang.String.format;
import static java.util.stream.Collectors.toList;


public class PapiWorldCommand implements TerminableModule {

    public void setup(@NonNull TerminableConsumer consumer) {
        Commands.create()
                .assertUsage("<world>")
                .assertPermission("papi.essentials.commands.world")
                .assertPlayer()
                .description("World command")
                .tabHandler(context -> TabHandlers.worlds(context.rawArg(0)))
                .handler(context -> {
                    if (context.args().isEmpty()) {
                        context.replyError("Мир не указан");
                        return;
                    }
                    World world = context.arg(0).parseOrFail(World.class);
                    Location playerLocation = context.sender().getLocation();
                    context.sender().teleport(new Location(world,
                            playerLocation.getX() + .5,
                            playerLocation.getY() + .5,
                            playerLocation.getZ() + .5,
                            playerLocation.getYaw(),
                            playerLocation.getPitch()
                    ));
                    context.replyAnnouncement(format("Вы были успешно телепортированы в мир %s", world.getName()));

                })

                .registerAndBind(consumer, "world");
    }


}