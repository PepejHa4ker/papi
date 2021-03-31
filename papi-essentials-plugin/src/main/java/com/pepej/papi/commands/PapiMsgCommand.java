package com.pepej.papi.commands;

import com.pepej.papi.command.Commands;
import com.pepej.papi.messaging.bungee.BungeeCord;
import com.pepej.papi.promise.Promise;
import com.pepej.papi.services.Services;
import com.pepej.papi.terminable.TerminableConsumer;
import com.pepej.papi.terminable.module.TerminableModule;
import net.luckperms.api.LuckPerms;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;


import static com.pepej.papi.text.Text.colorize;
import static java.lang.String.format;

public class PapiMsgCommand implements TerminableModule {

    public void setup(@NonNull TerminableConsumer consumer) {
        LuckPerms luckPerms = Services.load(LuckPerms.class);
        Commands.create()
                .assertUsage("<player> <message>")
                .assertPlayer()
                .assertPermission("papi.essentials.commands.msg")
                .handler(context -> {
                    Player other = context.arg(0).parseOrFail(Player.class);
                    Player sender = context.sender();
                    String message = String.join(" ", context.args().subList(1, context.args().size()));
                    String otherPrefix = luckPerms.getUserManager().getUser(other.getUniqueId()).getCachedData().getMetaData().getPrefix();
                    otherPrefix = otherPrefix == null ? "" : otherPrefix;
                    String otherSuffix = luckPerms.getUserManager().getUser(other.getUniqueId()).getCachedData().getMetaData().getSuffix();
                    otherSuffix = otherSuffix == null ? "" : otherSuffix;
                    String senderPrefix = luckPerms.getUserManager().getUser(sender.getUniqueId()).getCachedData().getMetaData().getPrefix();
                    senderPrefix = senderPrefix == null ? "" : senderPrefix;
                    String senderSuffix = luckPerms.getUserManager().getUser(sender.getUniqueId()).getCachedData().getMetaData().getSuffix();
                    senderSuffix = senderSuffix == null ? "" : senderSuffix;

                    other.sendMessage(colorize(format("&7[%s%s%s --> Вам&8:&r&e ", senderPrefix, sender.getName(), senderSuffix)) + message + colorize("&7]"));
                    other.sendMessage(colorize(format("&7[Вы --> %s%s%s&8:&r&e ", otherPrefix, other.getName(), otherSuffix)) + message + colorize("&7]"));
                })
                .registerAndBind(consumer, "msg", "m", "w", "t", "tell");
    }
}