package com.pepej.papi.config;

import com.google.gson.JsonElement;
import com.pepej.papi.config.typeserializers.*;
import com.pepej.papi.datatree.DataTree;
import com.pepej.papi.gson.GsonSerializable;
import org.bukkit.configuration.serialization.ConfigurationSerializable;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.spongepowered.configurate.CommentedConfigurationNode;
import org.spongepowered.configurate.ConfigurationNode;
import org.spongepowered.configurate.ScopedConfigurationNode;
import org.spongepowered.configurate.gson.GsonConfigurationLoader;
import org.spongepowered.configurate.hocon.HoconConfigurationLoader;
import org.spongepowered.configurate.loader.AbstractConfigurationLoader;
import org.spongepowered.configurate.objectmapping.ObjectMapper;
import org.spongepowered.configurate.objectmapping.meta.NodeResolver;
import org.spongepowered.configurate.serialize.TypeSerializerCollection;
import org.spongepowered.configurate.yaml.NodeStyle;
import org.spongepowered.configurate.yaml.YamlConfigurationLoader;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * Misc utilities for working with Configurate
 */
public abstract class ConfigFactory<N extends ConfigurationNode, L extends AbstractConfigurationLoader<?>> {

    private static final ConfigFactory<ConfigurationNode, YamlConfigurationLoader> YAML = new ConfigFactory<ConfigurationNode, YamlConfigurationLoader>() {
        @NonNull
        @Override
        public YamlConfigurationLoader loader(@NonNull Path path) {
            YamlConfigurationLoader.Builder builder = YamlConfigurationLoader.builder()
                    .nodeStyle(NodeStyle.BLOCK)
                    .indent(2)
                    .source(() -> Files.newBufferedReader(path, StandardCharsets.UTF_8))
                    .sink(() -> Files.newBufferedWriter(path, StandardCharsets.UTF_8));

            builder.defaultOptions(builder.defaultOptions()
                    .serializers(TYPE_SERIALIZERS));
            return builder.build();
        }
    };

    private static final ConfigFactory<ConfigurationNode, GsonConfigurationLoader> GSON = new ConfigFactory<ConfigurationNode, GsonConfigurationLoader>() {
        @NonNull
        @Override
        public GsonConfigurationLoader loader(@NonNull Path path) {
            GsonConfigurationLoader.Builder builder = GsonConfigurationLoader.builder()
                    .indent(2)
                    .source(() -> Files.newBufferedReader(path, StandardCharsets.UTF_8))
                    .sink(() -> Files.newBufferedWriter(path, StandardCharsets.UTF_8));

            builder.defaultOptions(builder.defaultOptions()
                    .serializers(TYPE_SERIALIZERS));



            return builder.build();
        }
    };

    private static final ConfigFactory<CommentedConfigurationNode, HoconConfigurationLoader> HOCON = new ConfigFactory<CommentedConfigurationNode, HoconConfigurationLoader>() {
        @NonNull
        @Override
        public HoconConfigurationLoader loader(@NonNull Path path) {
            HoconConfigurationLoader.Builder builder = HoconConfigurationLoader.builder()
                    .source(() -> Files.newBufferedReader(path, StandardCharsets.UTF_8))
                    .sink(() -> Files.newBufferedWriter(path, StandardCharsets.UTF_8));

            builder.defaultOptions(builder.defaultOptions()
                    .serializers(TYPE_SERIALIZERS));
            return builder.build();
        }
    };

    private static final TypeSerializerCollection TYPE_SERIALIZERS;

    static {

        TYPE_SERIALIZERS = TypeSerializerCollection.builder()
                .register(JsonElement.class, GsonTypeSerializer.INSTANCE)
                .register(GsonSerializable.class, PapiTypeSerializer.INSTANCE)
                .register(ConfigurationSerializable.class, BukkitTypeSerializer.INSTANCE)
                .register(DataTree.class, JsonTreeTypeSerializer.INSTANCE)
                .register(String.class, StringSerializer.INSTANCE)
                .registerAnnotatedObjects(ObjectMapper.factoryBuilder()
                        .addNodeResolver(NodeResolver.onlyWithSetting())
                        .build())
                .build();
    }

    @NonNull
    public static TypeSerializerCollection typeSerializers() {
        return TYPE_SERIALIZERS;
    }

    @NonNull
    public static ConfigFactory<ConfigurationNode, YamlConfigurationLoader> yaml() {
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
    public ScopedConfigurationNode<?> load(@NonNull Path path) {
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

    @NonNull
    public L loader(@NonNull File file) {
        return loader(file.toPath());
    }

    @NonNull
    public ScopedConfigurationNode<?> load(@NonNull File file) {
        return load(file.toPath());
    }

    public void save(@NonNull File file, @NonNull ConfigurationNode node) {
        save(file.toPath(), node);
    }


}
