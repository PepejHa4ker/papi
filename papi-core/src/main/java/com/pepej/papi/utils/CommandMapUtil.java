package com.pepej.papi.utils;

import com.google.common.base.Preconditions;
import com.pepej.papi.shadow.ClassTarget;
import com.pepej.papi.shadow.Field;
import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.ShadowFactory;
import org.bukkit.Bukkit;
import org.bukkit.command.*;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;
import org.checkerframework.checker.nullness.qual.NonNull;

import java.util.Iterator;
import java.util.Map;

/**
 * Utility for interacting with the server's {@link CommandMap} instance.
 */
public final class CommandMapUtil {

    @ClassTarget(PluginCommand.class)
    private interface PluginCommandShadow extends Shadow {}

    @ClassTarget(SimplePluginManager.class)
    private interface SimplePluginCommandShadow extends Shadow {

        @Field
        CommandMap getCommandMap();

    }

    @ClassTarget(SimpleCommandMap.class)
    private interface SimpleCommandMapShadow extends Shadow {

        @Field
        Map<String, Command> getKnownCommands();

    }

    /**
     * Registers a CommandExecutor with the server
     *
     * @param plugin the plugin instance
     * @param command the command instance
     * @param aliases the command aliases
     * @param <T> the command executor class type
     * @return the command executor
     */
    @NonNull
    public static <T extends CommandExecutor> T registerCommand(@NonNull Plugin plugin, @NonNull T command, @NonNull String... aliases) {
        return registerCommand(plugin, command, null, null, null, aliases);
    }

    /**
     * Registers a CommandExecutor with the server
     *
     * @param plugin the plugin instance
     * @param command the command instance
     * @param permission the command permission
     * @param permissionMessage the message sent when the sender doesn't the required permission
     * @param description the command description
     * @param aliases the command aliases
     * @param <T> the command executor class type
     * @return the command executor
     */
    @NonNull
    public static <T extends CommandExecutor> T registerCommand(@NonNull Plugin plugin, @NonNull T command, String permission, String permissionMessage, String description, @NonNull String... aliases) {
        Preconditions.checkArgument(aliases.length != 0, "No aliases");
        for (String alias : aliases) {
            try {
                final PluginCommand cmd = (PluginCommand) ShadowFactory.global().constructShadow(PluginCommandShadow.class, alias, plugin).getShadowTarget();
                SimplePluginCommandShadow simplePluginCommandShadow = ShadowFactory.global().shadow(SimplePluginCommandShadow.class, Bukkit.getPluginManager());
                simplePluginCommandShadow.getCommandMap().register(plugin.getDescription().getName(), cmd);
                SimpleCommandMapShadow simpleCommandMapShadow = ShadowFactory.global().shadow(SimpleCommandMapShadow.class, simplePluginCommandShadow.getCommandMap());
                simpleCommandMapShadow.getKnownCommands().put(plugin.getDescription().getName().toLowerCase() + ":" + alias.toLowerCase(), cmd);
                simpleCommandMapShadow.getKnownCommands().put(alias.toLowerCase(), cmd);
                cmd.setLabel(alias.toLowerCase());
                if (permission != null) {
                    cmd.setPermission(permission);
                    if (permissionMessage != null) {
                        cmd.setPermissionMessage(permissionMessage);
                    }
                }
                if (description != null) {
                    cmd.setDescription(description);
                }

                cmd.setExecutor(command);
                if (command instanceof TabCompleter) {
                    cmd.setTabCompleter((TabCompleter) command);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return command;
    }

    /**
     * Unregisters a CommandExecutor with the server
     *
     * @param command the command instance
     * @param <T> the command executor class type
     * @return the command executor
     */
    @NonNull
    public static <T extends CommandExecutor> T unregisterCommand(@NonNull T command) {
        SimplePluginCommandShadow simplePluginCommandShadow = ShadowFactory.global().shadow(SimplePluginCommandShadow.class, Bukkit.getPluginManager());
        SimpleCommandMapShadow simpleCommandMapShadow = ShadowFactory.global().shadow(SimpleCommandMapShadow.class, simplePluginCommandShadow.getCommandMap());
        try {
            //noinspection unchecked
            Map<String, Command> knownCommands = simpleCommandMapShadow.getKnownCommands();

            Iterator<Command> iterator = knownCommands.values().iterator();
            while (iterator.hasNext()) {
                Command cmd = iterator.next();
                if (cmd instanceof PluginCommand) {
                    CommandExecutor executor = ((PluginCommand) cmd).getExecutor();
                    if (command == executor) {
                        cmd.unregister(simplePluginCommandShadow.getCommandMap());
                        iterator.remove();
                    }
                }
            }
        } catch (Exception e) {
            throw new RuntimeException("Could not unregister command", e);
        }

        return command;
    }

    private CommandMapUtil() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

}
