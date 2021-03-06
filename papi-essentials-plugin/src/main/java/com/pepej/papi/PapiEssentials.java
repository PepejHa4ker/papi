package com.pepej.papi;

import com.pepej.papi.commands.*;
import com.pepej.papi.plugin.PapiJavaPlugin;

@PapiImplementationPlugin(moduleName = PapiEssentials.MODULE_ID)
@Plugin(name = "papi-essentials", version = "1.0", description = "Essential spigot plugin powered by papi library", softDepends = {"papi"})
public class PapiEssentials extends PapiJavaPlugin {

    public static final String MODULE_ID = "Papi Essentials";

    @Override
    public void onPluginEnable() {
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
//        provideService(LuckPerms.class, LuckPermsProvider.get());
    }
}
