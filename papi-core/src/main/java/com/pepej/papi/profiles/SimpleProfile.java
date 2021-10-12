package com.pepej.papi.profiles;

import com.mojang.authlib.GameProfile;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.UUID;

public final class SimpleProfile extends AbstractProfile {

    public SimpleProfile(@NotNull UUID uniqueId, @Nullable String name) {
        super(uniqueId, name);
    }
}