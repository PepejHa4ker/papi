package com.pepej.papi.commands;

import com.pepej.papi.command.Commands;
import com.pepej.papi.terminable.TerminableConsumer;
import com.pepej.papi.terminable.module.TerminableModule;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Arrays;

import static java.lang.String.format;

public class PapiClearInvCommand implements TerminableModule {

    public void setup(@NonNull TerminableConsumer consumer) {
        Commands.create()
                .assertUsage("[player]")
                .assertPermission("papi.essentials.commands.clearinv")
                .handler(context -> {
                    Player player;
                    if (context.args().isEmpty()) {
                        player = null;
                    } else {
                        player = context.arg(0).parseOrFail(Player.class);
                    }
                    if (player == null) {
                        if (context.sender() instanceof Player) {
                            Player sender = (Player) context.sender();
                            sender.getInventory().clear();
                            context.replyAnnouncement("Инвентарь очищен");
                        } else {
                            context.replyError("С консоли можно использовать только на других!");

                        }
                    } else {
                        player.getInventory().clear();
                        context.replyAnnouncement(format("Инвентарь у %s очищен", player.getName()));
                    }
                })
                .registerAndBind(consumer, "clearinv", "invclear", "clear", "empty");
    }
}