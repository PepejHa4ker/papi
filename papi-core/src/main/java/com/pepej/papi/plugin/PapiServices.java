package com.pepej.papi.plugin;

import com.pepej.papi.bossbar.BossBarFactory;
import com.pepej.papi.bossbar.BukkitBossBarFactory;
import com.pepej.papi.hologram.HologramFactory;
import com.pepej.papi.hologram.individual.IndividualHologramFactory;
import com.pepej.papi.messaging.bungee.BungeeCord;
import com.pepej.papi.npc.CitizensNpcFactory;
import com.pepej.papi.npc.NpcFactory;
import com.pepej.papi.scoreboard.ScoreboardProvider;
import com.pepej.papi.signprompt.PacketSignPromptFactory;
import com.pepej.papi.signprompt.SignPromptFactory;

final class PapiServices {

    private PapiServices() {
        throw new UnsupportedOperationException("This class cannot be initialized");
    }

    static void setup(PapiPlugin plugin) {
        plugin.provideService(HologramFactory.class);
        plugin.provideService(BungeeCord.class);
        if (plugin.isPluginPresent("ProtocolLib")) {
            plugin.provideService(ScoreboardProvider.class);
            SignPromptFactory signPromptFactory = new PacketSignPromptFactory();
            plugin.provideService(SignPromptFactory.class, signPromptFactory);
            plugin.provideService(IndividualHologramFactory.class);
        }

        if (plugin.isPluginPresent("Citizens")) {
            CitizensNpcFactory npcManager = plugin.bind(new CitizensNpcFactory());
            plugin.provideService(NpcFactory.class, npcManager);
            plugin.provideService(CitizensNpcFactory.class, npcManager);
        }
        plugin.provideService(BossBarFactory.class, new BukkitBossBarFactory(plugin.getServer()));
    }


}