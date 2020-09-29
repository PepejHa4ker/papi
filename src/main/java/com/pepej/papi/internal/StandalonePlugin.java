package com.pepej.papi.internal;

import com.pepej.papi.internal.commands.PapiCommands;
import com.pepej.papi.internal.event.EventListener;
import com.pepej.papi.maven.MavenLibraries;
import com.pepej.papi.maven.MavenLibrary;
import com.pepej.papi.plugin.PapiJavaPlugin;


@PapiImplementationPlugin
@MavenLibraries({
        @MavenLibrary(groupId = "com.google.code.findbugs", artifactId = "jsr305", version = "3.0.0"), @MavenLibrary(groupId = "com.flowpowered", artifactId = "flow-math", version = "1.0.0"),
        @MavenLibrary(groupId = "net.kyori", artifactId = "text-adapter-bukkit", version = "3.0.5"), @MavenLibrary(groupId = "net.kyori", artifactId = "text-serializer-legacy", version = "3.0.4"),
        @MavenLibrary(groupId = "net.kyori", artifactId = "text-serializer-gson", version = "3.0.4"), @MavenLibrary(groupId = "ninja.leaping.configurate", artifactId = "configurate-hocon", version = "3.3"),
        @MavenLibrary(groupId = "ninja.leaping.configurate", artifactId = "configurate-gson", version = "3.3"), @MavenLibrary(groupId = "ninja.leaping.configurate", artifactId = "configurate-json", version = "3.3"),
        @MavenLibrary(groupId = "ninja.leaping.configurate", artifactId = "configurate-yaml", version = "3.3")})
public final class StandalonePlugin extends PapiJavaPlugin {
    @Override
    protected void enable() {
       this.bindModule(new EventListener());
       this.bindModule(new PapiCommands());


    }

}
