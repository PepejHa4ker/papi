package com.pepej.papi.commands;

import com.pepej.papi.command.Commands;
import com.pepej.papi.terminable.TerminableConsumer;
import com.pepej.papi.terminable.module.TerminableModule;
import org.bukkit.attribute.Attribute;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;

import static com.pepej.papi.text.Text.colorize;

public class PapiHealCommand implements TerminableModule {

    @Override
    public void setup(@NonNull final TerminableConsumer consumer) {
        Commands.create()
                .assertUsage("[player]")
                .assertPermission("papi.essentials.commands.heal")
                .description("Heal command")
                .handler(context -> {
                    if (context.args().isEmpty()) {
                        if (context.sender() instanceof ConsoleCommandSender) {
                            context.replyError("С консоли можно использовать только на других!");
                        } else {
                            Player sender = (Player) context.sender();
                            heal(sender);

                        }
                    } else {
                        Player player = context.arg(0).parseOrFail(Player.class);
                        heal(player);
                    }

                })
                .registerAndBind(consumer, "heal", "h", "хилл");

    }

    private static void heal(Player player) {
        player.getActivePotionEffects().clear();
        player.setHealth(player.getAttribute(Attribute.GENERIC_MAX_HEALTH).getValue());
        player.setFoodLevel(20);
        player.setSaturation(20);
        player.sendMessage(colorize("&b∗&a Вы были исцелены"));
    }
}
