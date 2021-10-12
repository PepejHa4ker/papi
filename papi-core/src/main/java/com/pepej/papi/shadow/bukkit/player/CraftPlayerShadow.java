package com.pepej.papi.shadow.bukkit.player;

import com.mojang.authlib.GameProfile;
import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.bukkit.BukkitShadowFactory;
import com.pepej.papi.shadow.bukkit.ObcClassTarget;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

@ObcClassTarget("entity.CraftPlayer")
public interface CraftPlayerShadow extends Shadow {

    @NotNull
    GameProfile getProfile();

    @Contract("!null -> new;")
    static CraftPlayerShadow create(@Nullable Player handle) {
        if (handle != null) {
            return BukkitShadowFactory.global().shadow(CraftPlayerShadow.class, handle);
        } else {
            return null;
        }
    }

}
