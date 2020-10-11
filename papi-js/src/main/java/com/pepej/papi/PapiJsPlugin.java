package com.pepej.papi;


import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.reflect.TypeToken;
import com.pepej.papi.environment.script.Script;
import com.pepej.papi.environment.settings.EnvironmentSettings;
import com.pepej.papi.internal.LoaderUtils;
import com.pepej.papi.internal.PapiImplementationPlugin;
import com.pepej.papi.bindings.BindingsBuilder;
import com.pepej.papi.bindings.BindingsSupplier;
import com.pepej.papi.environment.ScriptEnvironment;
import com.pepej.papi.environment.loader.ScriptLoadingExecutor;
import com.pepej.papi.logging.SystemLogger;
import com.pepej.papi.menu.scheme.MenuScheme;
import com.pepej.papi.menu.scheme.SchemeMapping;
import com.pepej.papi.metadata.MetadataKey;
import com.pepej.papi.plugin.PapiBukkitPlugin;
import com.pepej.papi.scheduler.Ticks;
import com.pepej.papi.terminable.composite.CompositeTerminable;
import com.pepej.papi.text.Text;
import io.github.classgraph.ClassGraph;
import io.github.classgraph.ClassInfo;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.YamlConfiguration;

import javax.annotation.Nonnull;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;

/**
 * Uses {@link ScriptController} and papi to provide a javascript plugin environment for Bukkit.
 */
@PapiImplementationPlugin
public class PapiJsPlugin extends PapiBukkitPlugin implements PapiJs {

    private static final String[] DEFAULT_IMPORT_INCLUDES = new String[]{
            // include all of the packages in papi
            "com.pepej.papi",
            "com.flowpowered.math",
            // include all of the packages in bukkit
            "org.bukkit",
            "com.destroystokyo.paper",
            "org.spigotmc.event"
    };
    private static final String[] DEFAULT_IMPORT_EXCLUDES = new String[]{
            // exclude craftbukkit classes
            "org.bukkit.craftbukkit",
    };

    private ScriptController controller;
    private ScriptEnvironment environment;

    @Override
        protected void enable() {
        // load config
        getLogger().info("Loading configuration...");
        YamlConfiguration config = loadConfig("config.yml");

        // search for packages which match the default import patterns
        getLogger().info("Scanning the classpath to resolve default package imports...");

        ClassGraph classGraph = new ClassGraph()
                .whitelistPackages(DEFAULT_IMPORT_INCLUDES)
                .blacklistPackages(DEFAULT_IMPORT_EXCLUDES)
                .addClassLoader(getServer().getClass().getClassLoader());

        // add the classloaders for papi implementation plugins
        LoaderUtils.getPapiImplementationPlugins().forEach(pl -> classGraph.addClassLoader(pl.getClass().getClassLoader()));

        Set<String> defaultPackages = classGraph.scan()
                                                .getAllClasses()
                                                .stream()
                                                .map(ClassInfo::getPackageName)
                                                .collect(Collectors.toSet());

        // setup the script controller
        getLogger().info("Initialising script controller...");
        this.controller = ScriptController.builder()
                                          .logger(SystemLogger.usingJavaLogger(getLogger()))
                                          .defaultEnvironmentSettings(EnvironmentSettings.builder()
                                                                                         .loadExecutor(new PapiLoadingExecutor())
                                                                                         .runExecutor(Schedulers.sync())
                                                                                         .pollRate(Ticks.to(config.getLong("poll-interval", 20L), TimeUnit.MILLISECONDS), TimeUnit.MILLISECONDS)
                                                                                         .initScript(config.getString("init-script", "init.js"))
                                                                                         .withBindings(new GeneralScriptBindings())
                                                                                         .withBindings(new PapiScriptBindings(this))
                                                                                         .withDefaultPackageImports(defaultPackages)
                                                                                         .build())
                                          .build();

        // get script directory
        Path scriptDirectory = Paths.get(config.getString("script-directory"));
        if (!Files.isDirectory(scriptDirectory)) {
            try {
                Files.createDirectories(scriptDirectory);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        // init a new environment for our scripts
        getLogger().info("Creating new script environment at " + scriptDirectory.toString() + " (" + scriptDirectory.toAbsolutePath().toString() + ")");
        this.environment = this.controller.setupNewEnvironment(scriptDirectory);

        getLogger().info("Done!");
    }

    @Override
    protected void disable() {
        this.controller.shutdown();
    }

    @Override
    public ScriptController getController() {
        return this.controller;
    }

    @Override
    public ScriptEnvironment getEnvironment() {
        return this.environment;
    }

    private static final class PapiLoadingExecutor implements ScriptLoadingExecutor {
        @Override
        public AutoCloseable scheduleAtFixedRate(Runnable runnable, long l, TimeUnit timeUnit) {
            return Schedulers.builder()
                             .async()
                             .every(l, timeUnit)
                             .run(runnable);
        }

        @Override
        public void execute(@Nonnull Runnable command) {
            Schedulers.async().run(command);
        }
    }

    /**
     * Some misc functions to help with using Java collections in JS
     */
    @SuppressWarnings("rawtypes")
    private static final class GeneralScriptBindings implements BindingsSupplier {
        private static final Function<Integer, AtomicInteger> ATOMIC_INTEGER = AtomicInteger::new;
        private static final Supplier<ArrayList> ARRAY_LIST = ArrayList::new;
        private static final Supplier<LinkedList> LINKED_LIST = LinkedList::new;
        private static final Supplier<HashSet> HASH_SET = HashSet::new;
        private static final Supplier<HashMap> HASH_MAP = HashMap::new;
        private static final Supplier<CopyOnWriteArrayList> COPY_ON_WRITE_ARRAY_LIST = CopyOnWriteArrayList::new;
        private static final Supplier<Set> CONCURRENT_HASH_SET = ConcurrentHashMap::newKeySet;
        private static final Supplier<ConcurrentHashMap> CONCURRENT_HASH_MAP = ConcurrentHashMap::new;
        private static final Function<Object[], ArrayList> LIST_OF = objects -> new ArrayList<>(Arrays.asList(objects));
        private static final Function<Object[], HashSet> SET_OF = objects -> new HashSet<>(Arrays.asList(objects));
        private static final Function<Object[], ImmutableList> IMMUTABLE_LIST_OF = ImmutableList::copyOf;
        private static final Function<Object[], ImmutableSet> IMMUTABLE_SET_OF = ImmutableSet::copyOf;
        private static final Function<String, UUID> PARSE_UUID = s -> {
            try {
                return UUID.fromString(s);
           } catch (IllegalArgumentException e) {
                return null;
            }
        };

        @Override
        public void supplyBindings(Script script, BindingsBuilder bindings) {
            bindings.put("newArrayList", ARRAY_LIST);
            bindings.put("newLinkedList", LINKED_LIST);
            bindings.put("newHashSet", HASH_SET);
            bindings.put("newHashMap", HASH_MAP);
            bindings.put("newCopyOnWriteArrayList", COPY_ON_WRITE_ARRAY_LIST);
            bindings.put("newConcurrentHashSet", CONCURRENT_HASH_SET);
            bindings.put("newConcurrentHashMap", CONCURRENT_HASH_MAP);
            bindings.put("listOf", LIST_OF);
            bindings.put("setOf", SET_OF);
            bindings.put("immutableListOf", IMMUTABLE_LIST_OF);
            bindings.put("immutableSetOf", IMMUTABLE_SET_OF);
            bindings.put("parseUuid", PARSE_UUID);
        }
    }

    /**
     * Script bindings for papi utilities
     */
    private static final class PapiScriptBindings implements BindingsSupplier {
        private final PapiJsPlugin plugin;

        private PapiScriptBindings(PapiJsPlugin plugin) {
            this.plugin = plugin;
        }

        @Override
        public void supplyBindings(Script script, BindingsBuilder bindings) {
            // provide a terminable registry
            CompositeTerminable registry = CompositeTerminable.create();
            script.getClosables().bind(registry);
            bindings.put("registry", registry);
            //Timeunit
            bindings.put("SECONDS", TimeUnit.SECONDS);
            bindings.put("MINUTES", TimeUnit.MINUTES);
            bindings.put("HOURS", TimeUnit.HOURS);
            bindings.put("DAYS", TimeUnit.DAYS);
            // provide core server classes
            bindings.put("server", Bukkit.getServer());
            //boxed primitives
            bindings.put("int", Integer.class);
            bindings.put("str", String.class);
            bindings.put("bool", Boolean.class);
            bindings.put("long", Long.class);
            bindings.put("short", Short.class);
            bindings.put("plugin", this.plugin);
            bindings.put("services", Bukkit.getServicesManager());
            // boxed primitives

            bindings.put("colorize", (Function<Object, String>) PapiScriptBindings::colorize);
            bindings.put("newMetadataKey", (Function<Object, MetadataKey>) PapiScriptBindings::newMetadataKey);
            bindings.put("newEmptyScheme", (Supplier<MenuScheme>) PapiScriptBindings::newScheme);
            bindings.put("newScheme", (Function<SchemeMapping, MenuScheme>) PapiScriptBindings::newScheme);
        }

        private static String colorize(Object object) {
            return Text.colorize(object.toString());
        }

        private static <T> MetadataKey<T> newMetadataKey(Object id) {
            return MetadataKey.create(id.toString(), new TypeToken<T>(){});
        }

        private static MenuScheme newScheme() {
            return new MenuScheme();
        }

        private static MenuScheme newScheme(SchemeMapping mapping) {
            return new MenuScheme(mapping);
        }
    }
}
