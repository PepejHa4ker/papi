package com.pepej.papi.shadow.bukkit.itemstack;

import com.mojang.authlib.GameProfile;
import com.pepej.papi.shadow.Field;
import com.pepej.papi.shadow.Shadow;
import com.pepej.papi.shadow.bukkit.ObcClassTarget;

@ObcClassTarget("CraftMetaSkull")
public interface CraftSkullMetaShadow extends Shadow {

    @Field
    void setProfile(GameProfile profile);
}
