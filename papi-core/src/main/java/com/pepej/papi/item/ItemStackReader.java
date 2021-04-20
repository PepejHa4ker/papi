package com.pepej.papi.item;

import lombok.SneakyThrows;
import org.bukkit.Material;
import org.bukkit.configuration.ConfigurationSection;
import org.spongepowered.configurate.ConfigurationNode;

import java.util.List;
import java.util.Optional;
import java.util.OptionalInt;
import java.util.stream.Collectors;

/**
 * Utility for creating {@link ItemStackBuilder}s from {@link ConfigurationSection bukkit} and {@link ConfigurationNode sponge} config files.
 */
public class ItemStackReader {

    /**
     * The default papier {@link ItemStackReader} implementation.
     */
    public static final ItemStackReader DEFAULT = new ItemStackReader();

    // Allow subclassing
    protected ItemStackReader() {}

    /**
     * Reads an {@link ItemStackBuilder} from the given config.
     *
     * @param config the config to read from
     * @return the item
     */
    public final ItemStackBuilder read(ConfigurationSection config) {
        return read(config, VariableReplacer.NOOP);
    }

    /**
     * Reads an {@link ItemStackBuilder} from the given {@link ConfigurationNode} config.
     *
     * @param config the config to read from
     * @return the item
     */
    public final ItemStackBuilder read(ConfigurationNode config) {
        return read(config, VariableReplacer.NOOP);
    }

    public ItemStackBuilder read(ConfigurationNode config, VariableReplacer variableReplacer) {
        return ItemStackBuilder.of(parseMaterial(config))
                               .apply(isb -> {
                                   parseData(config).ifPresent(isb::data);
                                   parseName(config).map(variableReplacer::replace).ifPresent(isb::name);
                                   parseLore(config).map(variableReplacer::replace).ifPresent(isb::lore);
                               });
    }

    /**
     * Reads an {@link ItemStackBuilder} from the given config.
     *
     * @param config the config to read from
     * @param variableReplacer the variable replacer to use to replace variables in the name and lore.
     * @return the item
     */
    public ItemStackBuilder read(ConfigurationSection config, VariableReplacer variableReplacer) {
        return ItemStackBuilder.of(parseMaterial(config))
                               .apply(isb -> {
                                   parseData(config).ifPresent(isb::data);
                                   parseName(config).map(variableReplacer::replace).ifPresent(isb::name);
                                   parseLore(config).map(variableReplacer::replace).ifPresent(isb::lore);
                               });
    }

    protected Material parseMaterial(ConfigurationNode config) {
        return parseMaterial(config.node("material").getString("air"));
    }
    protected Material parseMaterial(ConfigurationSection config) {
        return parseMaterial(config.getString("material"));
    }

    protected Material parseMaterial(String name) {
        try {
            return Material.valueOf(name.toUpperCase());
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("Unable to parse material '" + name + "'");
        }
    }
    protected OptionalInt parseData(ConfigurationNode config) {
        if (config.node("data").getInt() != 0) {
            return OptionalInt.of(config.node("data").getInt(0));
        }
        return OptionalInt.empty();
    }
    protected OptionalInt parseData(ConfigurationSection config) {
        if (config.contains("data")) {
            return OptionalInt.of(config.getInt("data"));
        }
        return OptionalInt.empty();
    }
    protected Optional<String> parseName(ConfigurationNode config) {
        if (config.node("name").getString() != null) {
            return Optional.of(config.node("name").getString(""));
        }
        return Optional.empty();
    }

    protected Optional<String> parseName(ConfigurationSection config) {
        if (config.contains("name")) {
            return Optional.of(config.getString("name"));
        }
        return Optional.empty();
    }
    @SneakyThrows
    protected Optional<List<String>> parseLore(ConfigurationNode config) {
        List<String> lore = config.node("lore").getList(String.class);
        if (lore != null && !lore.isEmpty()) {
            return Optional.of(lore);
        }
        return Optional.empty();
    }

    protected Optional<List<String>> parseLore(ConfigurationSection config) {
        if (config.contains("lore")) {
            return Optional.of(config.getStringList("lore"));
        }
        return Optional.empty();
    }

    /**
     * Function for replacing variables in item names and lores.
     */
    @FunctionalInterface
    public interface VariableReplacer {

        /**
         * No-op instance.
         */
        VariableReplacer NOOP = string -> string;

        /**
         * Replace variables in the input {@code string}.
         *
         * @param string the string
         * @return the replaced string
         */
        String replace(String string);

        /**
         * Replaces variables in the input {@code list} of {@link String}s.
         *
         * @param list the list
         * @return the replaced list
         */
        default Iterable<String> replace(List<String> list) {
            return list.stream().map(this::replace).collect(Collectors.toList());
        }
    }

}
