package com.pepej.papi.utils;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.pepej.papi.Papi;
import com.pepej.papi.services.Services;
import com.pepej.papi.npc.NpcFactory;
import com.pepej.papi.text.Text;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Arrays;
import java.util.Collection;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;
import java.util.stream.Stream;

/**
 * A collection of Player related utilities
 */
public final class Players {

    /**
     * Gets a player by uuid.
     *
     * @param uuid the uuid
     * @return a player, or null
     */
    @Nullable
    public static Player getNullable(@NonNull final UUID uuid) {
        return Papi.server().getPlayer(uuid);
    }

    /**
     * Gets a player by uuid.
     *
     * @param uuid the uuid
     * @return an optional player
     */
    public static Optional<Player> get(@NonNull final UUID uuid) {
        return Optional.ofNullable(getNullable(uuid));
    }

    /**
     * Gets a player by username.
     *
     * @param username the players username
     * @return the player, or null
     */
    @Nullable
    public static Player getNullable(@NonNull final String username) {
        return Papi.server().getPlayerExact(username);
    }

    /**
     * Gets a player by username.
     *
     * @param username the players username
     * @return an optional player
     */
    public static Optional<Player> get(@NonNull final String username) {
        return Optional.ofNullable(getNullable(username));
    }

    /**
     * Gets all players on the server.
     *
     * @return all players on the server
     */
    public static Collection<Player> all() {
        //noinspection unchecked
        return (Collection<Player>) Bukkit.getOnlinePlayers();
    }

    /**
     * Gets a stream of all players on the server.
     *
     * @return a stream of all players on the server
     */
    public static Stream<Player> stream() {
        return all().stream();
    }

    /**
     * Applies a given action to all players on the server
     *
     * @param consumer the action to apply
     */
    public static void forEach(@NonNull final Consumer<Player> consumer) {
        all().forEach(consumer);
    }

    /**
     * Applies an action to each object in the iterable, if it is a player.
     *
     * @param objects the objects to iterate
     * @param consumer the action to apply
     */
    public static void forEachIfPlayer(@NonNull final Iterable<Object> objects, Consumer<Player> consumer) {
        for (Object o : objects) {
            if (o instanceof Player) {
                Player player = (Player) o;
                if (!Services.getNullable(NpcFactory.class).isPapiNPC(player)) {
                    consumer.accept(player);
                }
            }
        }
    }

    /**
     * Gets a stream of all players within a given radius of a point
     *
     * @param center the point
     * @param radius the radius
     * @return a stream of players
     */
    public static Stream<Player> streamInRange(@NonNull final Location center, final double radius) {
        return center.getWorld().getNearbyEntities(center, radius, radius, radius).stream()
                     .filter(e -> e instanceof Player && !Services.getNullable(NpcFactory.class).isPapiNPC(e))
                     .map(e -> ((Player) e));
    }

    /**
     * Applies an action to all players within a given radius of a point
     *
     * @param center the point
     * @param radius the radius
     * @param consumer the action to apply
     */
    public static void forEachInRange(@NonNull final Location center, final double radius, @NonNull final Consumer<Player> consumer) {
        streamInRange(center, radius).forEach(consumer);
    }

    /**
     * Messages a sender a set of messages.
     *
     * @param sender the sender
     * @param type the type of messages
     * @param messages the messages to send
     */

    public static void msg(@NonNull final CommandSender sender, @NonNull final MessageType type, @NonNull final String... messages) {
        for (String message : messages) {
            sender.sendMessage(Text.colorize(type.getPrefix() + message));
        }
    }

    public static void msg(@NonNull final CommandSender sender, @NonNull final TextComponent component) {
        msg(sender, MessageType.INFO, component);
    }

    public static void msg(@NonNull final CommandSender sender, @NonNull final MessageType type, @NonNull final TextComponent component) {
        TextComponent textComponent = new TextComponent(Text.colorize(type.getPrefix()));
        textComponent.addExtra(component);
        sender.spigot().sendMessage(textComponent);
    }
    public static void msg(@NonNull final CommandSender sender, @NonNull final String... messages) {
        msg(sender, MessageType.INFO, messages);
    }

    @Nullable
    public static OfflinePlayer getOfflineNullable(@NonNull final UUID uuid) {
        return Papi.server().getOfflinePlayer(uuid);
    }

    public static Optional<OfflinePlayer> getOffline(@NonNull final UUID uuid) {
        return Optional.ofNullable(getOfflineNullable(uuid));
    }

    @Nullable
    public static OfflinePlayer getOfflineNullable(@NonNull final String username) {
        //noinspection deprecation
        return Papi.server().getOfflinePlayer(username);
    }

    public static Optional<OfflinePlayer> getOffline(@NonNull final String username) {
        return Optional.ofNullable(getOfflineNullable(username));
    }

    public static Collection<OfflinePlayer> allOffline() {
        return ImmutableList.copyOf(Bukkit.getOfflinePlayers());
    }

    public static Stream<OfflinePlayer> streamOffline() {
        return Arrays.stream(Bukkit.getOfflinePlayers());
    }

    public static void forEachOffline(@NonNull final Consumer<OfflinePlayer> consumer) {
        for (OfflinePlayer player : Bukkit.getOfflinePlayers()) {
            consumer.accept(player);
        }
    }

    public static void playSound(@NonNull final Player player, @NonNull final Sound sound) {
        player.playSound(player.getLocation(), sound, 1.0f, 1.0f);
    }

    public static void playSound(@NonNull final Player player, @NonNull final Location location, @NonNull final Sound sound) {
        player.playSound(location, sound, 1.0f, 1.0f);
    }

    public static void playSound(@NonNull final Location location, @NonNull final Sound sound) {
        location.getWorld().playSound(location, sound, 1.0f, 1.0f);
    }

    @SuppressWarnings("deprecation")
    public static void sendBlockChange(Player player, Location loc, Material type, int data) {
        player.sendBlockChange(loc, type, (byte) data);
    }

    public static void sendBlockChange(Player player, Block block, Material type, int data) {
        sendBlockChange(player, block.getLocation(), type, data);
    }

    public static void sendBlockChange(Player player, Location loc, Material type) {
        sendBlockChange(player, loc, type, 0);
    }

    public static void sendBlockChange(Player player, Block block, Material type) {
        sendBlockChange(player, block, type, 0);
    }

    public static void spawnParticle(Player player, Location location, Particle particle) {
        player.spawnParticle(particle, location, 1);
    }

    public static void spawnParticle(Location location, Particle particle) {
        location.getWorld().spawnParticle(particle, location, 1);
    }

    public static void spawnParticle(Player player, Location location, Particle particle, int amount) {
        Preconditions.checkArgument(amount > 0, "amount > 0");
        player.spawnParticle(particle, location, amount);
    }

    public static void spawnParticle(Location location, Particle particle, int amount) {
        Preconditions.checkArgument(amount > 0, "amount > 0");
        location.getWorld().spawnParticle(particle, location, amount);
    }

    public static void spawnEffect(@NonNull final Player player, @NonNull final Location location, @NonNull final Effect effect) {
        player.playEffect(location, effect, null);
    }

    public static void spawnEffect(@NonNull final Location location, @NonNull final Effect effect) {
        location.getWorld().playEffect(location, effect, null);
    }

    public static void spawnEffect(@NonNull final Player player, @NonNull final Location location, @NonNull final Effect effect, final int amount) {
        Preconditions.checkArgument(amount > 0, "amount > 0");
        for (int i = 0; i < amount; i++) {
            player.playEffect(location, effect, null);
        }
    }

    public static void spawnEffect(@NonNull final Location location, @NonNull final Effect effect, final int amount) {
        Preconditions.checkArgument(amount > 0, "amount > 0");
        for (int i = 0; i < amount; i++) {
            location.getWorld().playEffect(location, effect, null);
        }
    }

    public static void resetWalkSpeed(@NonNull final Player player) {
        player.setWalkSpeed(0.2f);
    }

    public static void resetFlySpeed(@NonNull final Player player) {
        player.setFlySpeed(0.1f);
    }

    private Players() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    public enum MessageType {
        ANNOUNCEMENT("&b∗&a "),
        INFO("&7&m&l-->&7 "),
        WARNING("&e&l!&6 "),
        ERROR("&c&l✕&6 ");

        @NonNull
        private final String prefix;

        MessageType(@NonNull final String prefix) {
            this.prefix = prefix;
        }

        @NonNull
        public String getPrefix() {
            return prefix;
        }
    }
}
