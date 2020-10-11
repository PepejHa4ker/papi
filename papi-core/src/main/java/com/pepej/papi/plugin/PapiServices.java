package com.pepej.papi.plugin;

import com.pepej.papi.bossbar.BossBarFactory;
import com.pepej.papi.bossbar.BukkitBossBarFactory;
import com.pepej.papi.hologram.BukkitHologramFactory;
import com.pepej.papi.hologram.HologramFactory;
import com.pepej.papi.hologram.individual.IndividualHologramFactory;
import com.pepej.papi.hologram.individual.PacketIndividualHologramFactory;
import com.pepej.papi.messaging.bungee.BungeeCord;
import com.pepej.papi.messaging.bungee.BungeeCordImpl;
import com.pepej.papi.npc.CitizensNpcFactory;
import com.pepej.papi.npc.NpcFactory;
import com.pepej.papi.scoreboard.PacketScoreboardProvider;
import com.pepej.papi.scoreboard.ScoreboardProvider;
import com.pepej.papi.signprompt.PacketSignPromptFactory;
import com.pepej.papi.signprompt.SignPromptFactory;

final class PapiServices {
    private PapiServices() {
    }

    static void setup(PapiBukkitPlugin plugin) {
        plugin.provideService(HologramFactory.class, new BukkitHologramFactory());
        plugin.provideService(BungeeCord.class, new BungeeCordImpl(plugin));
        if (plugin.isPluginPresent("ProtocolLib")) {
            PacketScoreboardProvider scoreboardProvider = new PacketScoreboardProvider(plugin);
            plugin.provideService(ScoreboardProvider.class, scoreboardProvider);
            plugin.provideService(PacketScoreboardProvider.class, scoreboardProvider);

            SignPromptFactory signPromptFactory = new PacketSignPromptFactory();
            plugin.provideService(SignPromptFactory.class, signPromptFactory);

            try {
                IndividualHologramFactory hologramFactory = new PacketIndividualHologramFactory();
                plugin.provideService(IndividualHologramFactory.class, hologramFactory);
            } catch (Throwable t) {
                // ignore??
            }
        }
        if (plugin.isPluginPresent("Citizens")) {
            CitizensNpcFactory npcManager = plugin.bind(new CitizensNpcFactory());
            plugin.provideService(NpcFactory.class, npcManager);
            plugin.provideService(CitizensNpcFactory.class, npcManager);
        }
        if (classExists("org.bukkit.boss.BossBar")) {
            BossBarFactory bossBarFactory = new BukkitBossBarFactory(plugin.getServer());
            plugin.provideService(BossBarFactory.class, bossBarFactory);
        }
    }

    private static boolean classExists(String clazz) {
        try {
            Class.forName(clazz);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

}