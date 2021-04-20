package com.pepej.papi;

import com.pepej.papi.ap.Plugin;
import com.pepej.papi.ap.PluginDependency;
import com.pepej.papi.commands.*;
import com.pepej.papi.internal.PapiImplementationPlugin;
import com.pepej.papi.plugin.PapiJavaPlugin;
import net.luckperms.api.LuckPerms;
import net.luckperms.api.LuckPermsProvider;

@PapiImplementationPlugin(moduleName = PapiEssentials.MODULE_ID)
@Plugin(name = "papi-essentials", version = "1.0", description = "Essential spigot plugin powered by papi library", depends = {@PluginDependency("papi"), @PluginDependency("LuckPerms")})
public class PapiEssentials extends PapiJavaPlugin {

    public static final String MODULE_ID = "Papi Essentials";

    @Override
    public void onPluginEnable() {
        provideService(LuckPerms.class, LuckPermsProvider.get());
        bindModule(new PapiGamemodeCommand());
        bindModule(new PapiFlyCommand());
        bindModule(new PapiHealCommand());
        bindModule(new PapiWorldCommand());
        bindModule(new PapiFeedCommand());
        bindModule(new PapiTpCommand());
        bindModule(new PapiSummonCommand());
        bindModule(new PapiMsgCommand());
        bindModule(new PapiClearInvCommand());
        bindModule(new PapiGiveExpCommand());
        bindModule(new PapiSetExpCommand());
        bindModule(new PapiSpeedCommand());
    }
}
