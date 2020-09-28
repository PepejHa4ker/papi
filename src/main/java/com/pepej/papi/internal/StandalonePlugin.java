package com.pepej.papi.internal;

import com.pepej.papi.Commands;
import com.pepej.papi.cooldown.Cooldown;
import com.pepej.papi.cooldown.CooldownMap;
import com.pepej.papi.maven.MavenLibraries;
import com.pepej.papi.maven.MavenLibrary;
import com.pepej.papi.plugin.PapiJavaPlugin;
import com.pepej.papi.utils.Players;
import org.bukkit.entity.Player;

import java.util.concurrent.TimeUnit;


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
        CooldownMap<Player> cooldowns = CooldownMap.create(Cooldown.of(30, TimeUnit.MINUTES));
        Commands.create()
                .assertPlayer()
                .assertFunction(c -> {
                    if (cooldowns.test(c.sender())) {
                        return true;
                    }
                    Players.msg(c.sender(), "&6До бонуса осталось " + cooldowns.remainingTime(c.sender(), TimeUnit.MINUTES) + " минут!");
                    return false;
                })
                .handler(context -> {
                    context.reply("&a+500$");
                })
                .registerAndBind(this, "бонус", "bonus");

    }

}
