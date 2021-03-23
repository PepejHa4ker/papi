package com.pepej.papi;

import com.pepej.papi.ap.Plugin;
import com.pepej.papi.ap.PluginDependency;
import com.pepej.papi.internal.PapiImplementationPlugin;
import com.pepej.papi.plugin.PapiJavaPlugin;

@PapiImplementationPlugin(moduleName = "Papi Essentials")
@Plugin(name = "papi-essentials", version = "1.0", description = "Essential spigot plugin powered by papi library", depends = @PluginDependency("papi"))
public class PapiEssentials extends PapiJavaPlugin {

    @Override
    public void onPluginEnable() {
        //TODO
    }
}
