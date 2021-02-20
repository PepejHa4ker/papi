package com.pepej.papi.config;

import com.google.common.reflect.TypeToken;
import com.google.gson.JsonElement;
import com.pepej.papi.config.typeserializers.*;
import com.pepej.papi.datatree.DataTree;
import com.pepej.papi.gson.GsonSerializable;
import ninja.leaping.configurate.ConfigurationNode;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.gson.GsonConfigurationLoader;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMapper;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import ninja.leaping.configurate.objectmapping.serialize.TypeSerializerCollection;
import ninja.leaping.configurate.yaml.YAMLConfigurationLoader;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.yaml.snakeyaml.DumperOptions;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Misc utilities for working with Configurate
 */
public abstract class ConfigFactory<N extends ConfigurationNode, L extends ConfigurationLoader<N>> {

    private static final ConfigFactory<ConfigurationNode, YAMLConfigurationLoader> YAML = new ConfigFactory<ConfigurationNode, YAMLConfigurationLoader>() {
        @NonNull
        @Override
        public YAMLConfigurationLoader loader(@NonNull Path path) {
            YAMLConfigurationLoader.Builder builder = YAMLConfigurationLoader.builder()
                                                                             .setFlowStyle(DumperOptions.FlowStyle.BLOCK)
                                                                             .setIndent(2)
                                                                             .setSource(() -> Files.newBufferedReader(path, StandardCharsets.UTF_8))
                                                                             .setSink(() -> Files.newBufferedWriter(path, StandardCharsets.UTF_8));

            builder.setDefaultOptions(builder.getDefaultOptions().withSerializers(TYPE_SERIALIZERS));
            return builder.build();
        }
    };

    private static final ConfigFactory<ConfigurationNode, GsonConfigurationLoader> GSON = new ConfigFactory<ConfigurationNode, GsonConfigurationLoader>() {
        @NonNull
        @Override
        public GsonConfigurationLoader loader(@NonNull Path path) {
            GsonConfigurationLoader.Builder builder = GsonConfigurationLoader.builder()
                                                                             .setIndent(2)
                                                                             .setSource(() -> Files.newBufferedReader(path, StandardCharsets.UTF_8))
                                                                             .setSink(() -> Files.newBufferedWriter(path, StandardCharsets.UTF_8));

            builder.setDefaultOptions(builder.getDefaultOptions().withSerializers(TYPE_SERIALIZERS));
            return builder.build();
        }
    };

    private static final ConfigFactory<CommentedConfigurationNode, HoconConfigurationLoader> HOCON = new ConfigFactory<CommentedConfigurationNode, HoconConfigurationLoader>() {
        @NonNull
        @Override
        public HoconConfigurationLoader loader(@NonNull Path path) {
            HoconConfigurationLoader.Builder builder = HoconConfigurationLoader.builder()
                                                                               .setSource(() -> Files.newBufferedReader(path, StandardCharsets.UTF_8))
                                                                               .setSink(() -> Files.newBufferedWriter(path, StandardCharsets.UTF_8));

            builder.setDefaultOptions(builder.getDefaultOptions().withSerializers(TYPE_SERIALIZERS));
            return builder.build();
        }
    };

    private static final TypeSerializerCollection TYPE_SERIALIZERS;
    static {
        TypeSerializerCollection papiSerializers = TypeSerializerCollection.create();
        papiSerializers.register(TypeToken.of(JsonElement.class), GsonTypeSerializer.INSTANCE);
        papiSerializers.register(TypeToken.of(GsonSerializable.class), PapiTypeSerializer.INSTANCE);
        papiSerializers.register(TypeToken.of(ConfigurationSerializable.class), BukkitTypeSerializer.INSTANCE);
        papiSerializers.register(TypeToken.of(DataTree.class), JsonTreeTypeSerializer.INSTANCE);
        papiSerializers.register(TypeToken.of(String.class), ColoredStringTypeSerializer.INSTANCE);

        TYPE_SERIALIZERS = papiSerializers.newChild();
    }

    @NonNull
    public static TypeSerializerCollection typeSerializers() {
        return TYPE_SERIALIZERS;
    }

    @NonNull
    public static ConfigFactory<ConfigurationNode, YAMLConfigurationLoader> yaml() {
        return YAML;
    }

    @NonNull
    public static ConfigFactory<ConfigurationNode, GsonConfigurationLoader> gson() {
        return GSON;
    }

    @NonNull
    public static ConfigFactory<CommentedConfigurationNode, HoconConfigurationLoader> hocon() {
        return HOCON;
    }

    private ConfigFactory() {

    }

    @NonNull
    public abstract L loader(@NonNull Path path);

    @NonNull
    public N load(@NonNull Path path) {
        try {
            return loader(path).load();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void save(@NonNull Path path, @NonNull ConfigurationNode node) {
        try {
            loader(path).save(node);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public <T> void load(@NonNull Path path, T object) {
        try {
            L loader = loader(path);
            ObjectMapper<T>.BoundInstance mapper = objectMapper(object);

            if (!Files.exists(path)) {
                // create a new empty node
                N node = loader.createEmptyNode();
                // write the content of the object to the node
                mapper.serialize(node);
                // save the node
                loader.save(node);
            } else {
                // load the node from the file
                N node = loader.load();
                // populate the config object
                mapper.populate(node);
            }
        } catch (ObjectMappingException | IOException e) {
            throw new ConfigurationException(e);
        }
    }

    @NonNull
    public L loader(@NonNull File file) {
        return loader(file.toPath());
    }

    @NonNull
    public N load(@NonNull File file) {
        return load(file.toPath());
    }

    public void save(@NonNull File file, @NonNull ConfigurationNode node) {
        save(file.toPath(), node);
    }

    public <T> void load(@NonNull File file, T object) {
        load(file.toPath(), object);
    }

    @NonNull
    public static <T> ObjectMapper<T> classMapper(@NonNull Class<T> clazz) {
        try {
            return ObjectMapper.forClass(clazz);
        } catch (ObjectMappingException e) {
            throw new ConfigurationException(e);
        }
    }

    @NonNull
    public static <T> ObjectMapper<T>.BoundInstance objectMapper(@NonNull T object) {
        try {
            return ObjectMapper.forObject(object);
        } catch (ObjectMappingException e) {
            throw new ConfigurationException(e);
        }
    }

    @NonNull
    public static <T> T generate(@NonNull Class<T> clazz, @NonNull ConfigurationNode node) {
        try {
            return classMapper(clazz).bindToNew().populate(node);
        } catch (ObjectMappingException e) {
            throw new ConfigurationException(e);
        }
    }

    @NonNull
    public static <T> T populate(@NonNull T object, @NonNull ConfigurationNode node) {
        try {
            return objectMapper(object).populate(node);
        } catch (ObjectMappingException e) {
            throw new ConfigurationException(e);
        }
    }
}
