package com.pepej.papi.internal.commands;

import com.pepej.papi.Commands;
import com.pepej.papi.internal.LoaderUtils;
import com.pepej.papi.terminable.TerminableConsumer;
import com.pepej.papi.terminable.module.TerminableModule;

import javax.annotation.Nonnull;

public class PapiCommands implements TerminableModule {

    @Override
    public void setup(@Nonnull final TerminableConsumer consumer) {
        Commands.create()
                .assertConsole()
                .handler(c -> {
                    LoaderUtils.getPlugin().reloadConfig();
                    c.reply("&cSuccessful");
                })
                .register("papireload", "papirl", "prl");
    }
}
