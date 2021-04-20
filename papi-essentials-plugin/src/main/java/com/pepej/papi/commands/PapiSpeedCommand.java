package com.pepej.papi.commands;

import com.pepej.papi.command.Commands;
import com.pepej.papi.terminable.TerminableConsumer;
import com.pepej.papi.terminable.module.TerminableModule;
import com.pepej.papi.utils.Players;
import com.pepej.papi.utils.TabHandlers;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Collections;

public class PapiSpeedCommand implements TerminableModule {

    public void setup(@NonNull TerminableConsumer consumer) {
        Commands.create()
                .description("Speed command")
                .assertPlayer()
                .assertPermission("papi.essentials.commands.speed")
                .assertUsage("<level>")
                .handler(context -> {
                    int level = context.arg(0).parseOrFail(Integer.class);
                    if (level < 0 || level > 10) {
                        context.replyError("Уровень должен быть не меньше 0 и не больше 10");
                        return;
                    }
                    if (context.sender().isFlying()) {
                        context.sender().setFlySpeed(0.1f*level);
                    } else {
                        context.sender().setWalkSpeed(0.2f*level);

                    }


                })
                .registerAndBind(consumer, "speed", "spd", "скорость", "с");
    }
}