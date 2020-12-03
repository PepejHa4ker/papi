package com.pepej.papi.npc;

import com.pepej.papi.events.Events;
import com.pepej.papi.scheduler.Schedulers;
import com.pepej.papi.metadata.ExpiringValue;
import com.pepej.papi.metadata.Metadata;
import com.pepej.papi.metadata.MetadataKey;
import com.pepej.papi.metadata.MetadataMap;
import com.pepej.papi.terminable.composite.CompositeTerminable;
import net.citizensnpcs.api.CitizensAPI;
import net.citizensnpcs.api.event.NPCClickEvent;
import net.citizensnpcs.api.event.NPCLeftClickEvent;
import net.citizensnpcs.api.event.NPCRightClickEvent;
import net.citizensnpcs.api.npc.MemoryNPCDataStore;
import net.citizensnpcs.api.npc.NPC;
import net.citizensnpcs.api.npc.NPCRegistry;
import net.citizensnpcs.api.trait.Trait;
import net.citizensnpcs.api.trait.TraitInfo;
import net.citizensnpcs.npc.skin.SkinnableEntity;
import net.citizensnpcs.trait.LookClose;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.entity.Entity;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.Player;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.player.PlayerFishEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.UUID;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

/**
 * Implementation of {@link NpcFactory} using Citizens.
 */
public class CitizensNpcFactory implements NpcFactory {
    private static final MetadataKey<Boolean> RECENT_NPC_CLICK_KEY = MetadataKey.createBooleanKey("papi-recent-npc-click");

    private NPCRegistry npcRegistry;
    private final CompositeTerminable registry = CompositeTerminable.create();

    public CitizensNpcFactory() {

        // create an enable hook for citizens
        Events.subscribe(PluginEnableEvent.class)
              .filter(e -> e.getPlugin().getName().equals("Citizens"))
              .expireAfter(1) // only call once
              .handler(e -> init());
    }

    private void init() {
        // create npc registry
        this.npcRegistry = CitizensAPI.createNamedNPCRegistry("com/pepej/papi", new MemoryNPCDataStore());

        // ensure our trait is registered
        registerTrait();

        // handle click events
        Events.merge(NPCClickEvent.class, NPCRightClickEvent.class, NPCLeftClickEvent.class)
              .handler(e -> handleClick(e.getNPC(), e.getClicker())).bindWith(this.registry);

        // don't let players move npcs
        Events.subscribe(PlayerFishEvent.class)
              .filter(e -> e.getCaught() != null)
              .filter(e -> isPapiNPC(e.getCaught()))
              .handler(e -> e.setCancelled(true))
              .bindWith(this.registry);

        Events.subscribe(EntityDamageByEntityEvent.class)
              .filter(e -> isPapiNPC(e.getEntity()))
              .handler(e -> e.setCancelled(true))
              .bindWith(this.registry);

        // update npcs every 10 ticks
        Schedulers.sync().runRepeating(this::tickNpcs, 10L, 10L).bindWith(this.registry);
    }

    private boolean isPapiNPC(Entity entity) {
        NPC npc = this.npcRegistry.getNPC(entity);
        return npc != null && npc.hasTrait(ClickableTrait.class);
    }

    private void handleClick(NPC npc, Player clicker) {
        if (npc.hasTrait(ClickableTrait.class)) {
            if (processMetadata(clicker)) {
                return;
            }
            npc.getTrait(ClickableTrait.class).onClick(clicker);
        }
    }

    // returns true if the action should be blocked
    private boolean processMetadata(Player p) {
        return !Metadata.provideForPlayer(p).putIfAbsent(RECENT_NPC_CLICK_KEY, ExpiringValue.of(true, 100, TimeUnit.MILLISECONDS));
    }

    private void tickNpcs() {
        for (NPC npc : this.npcRegistry) {
            if (!npc.isSpawned() || !npc.hasTrait(ClickableTrait.class)) continue;

            Npc papiNpc = npc.getTrait(ClickableTrait.class).npc;

            // ensure npcs stay in the same position
            Location loc = npc.getEntity().getLocation();
            if (loc.getBlockX() != papiNpc.getInitialSpawn().getBlockX() || loc.getBlockZ() != papiNpc.getInitialSpawn().getBlockZ()) {
                npc.teleport(papiNpc.getInitialSpawn().clone(), PlayerTeleportEvent.TeleportCause.PLUGIN);
            }

            // don't let players stand near npcs
            for (Entity entity : npc.getStoredLocation().getWorld().getNearbyEntities(npc.getStoredLocation(), 1.0, 1.0, 1.0)) {
                if (!(entity instanceof Player) || this.npcRegistry.isNPC(entity)) continue;

                final Player p = (Player) entity;

                if (p.getGameMode() == GameMode.CREATIVE || p.getGameMode() == GameMode.SPECTATOR) {
                    continue;
                }

                if (npc.getEntity().getLocation().distance(p.getLocation()) < 3.5) {
                    p.setVelocity(p.getLocation().getDirection().multiply(-0.5).setY(0.4));
                }
            }
        }
    }

    @NonNull
    @Override
    public CitizensNpc spawnNpc(@NonNull Location location, @NonNull String nametag, @NonNull String skinPlayer) {
        return spawnNpc(location.clone(), nametag, npc -> npc.setSkin(skinPlayer));
    }

    @NonNull
    @Override
    public CitizensNpc spawnNpc(@NonNull Location location, @NonNull String nametag, @NonNull String skinTextures, @NonNull String skinSignature) {
        return spawnNpc(location.clone(), nametag, npc -> npc.setSkin(skinTextures, skinSignature));
    }

    private CitizensNpc spawnNpc(Location location, String nametag, Consumer<Npc> skin) {
        Objects.requireNonNull(this.npcRegistry, "npcRegistry");

        // create a new npc
        NPC npc = this.npcRegistry.createNPC(EntityType.PLAYER, nametag);

        // add the trait
        ClickableTrait trait = new ClickableTrait();
        npc.addTrait(trait);

        // create a new papiNpc instance
        CitizensNpc papiNpc = new NpcImpl(npc, trait, location.clone());
        trait.npc = papiNpc;

        // apply the skin and spawn it
        skin.accept(papiNpc);
        npc.spawn(location);

        return papiNpc;
    }

    @Override
    public void close() {

    }

    private static void registerTrait() {
        if (CitizensAPI.getTraitFactory().getTrait(ClickableTrait.class) == null) {
            try {
                CitizensAPI.getTraitFactory().registerTrait(TraitInfo.create(ClickableTrait.class));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private static final class ClickableTrait extends Trait {
        private final MetadataMap meta = MetadataMap.create();
        private Consumer<Player> clickCallback = null;
        private Npc npc = null;

        public ClickableTrait() {
            super("papi_clickable");
        }

        @Override
        public void onSpawn() {
            super.onSpawn();
            ensureLook(getNPC().getTrait(LookClose.class));
        }

        private void ensureLook(LookClose lookTraitInstance) {
            if (lookTraitInstance.toggle()) {
                return;
            }
            lookTraitInstance.toggle();
        }

        private void onClick(Player player) {
            if (this.clickCallback != null) {
                this.clickCallback.accept(player);
            }
        }
    }

    private static final class NpcImpl implements CitizensNpc {
        private final NPC npc;
        private final ClickableTrait trait;
        private final Location initialSpawn;

        private NpcImpl(NPC npc, ClickableTrait trait, Location initialSpawn) {
            this.npc = npc;
            this.trait = trait;
            this.initialSpawn = initialSpawn;
        }

        @Override
        public void setClickCallback(@Nullable Consumer<Player> clickCallback) {
            this.trait.clickCallback = clickCallback;
        }

        @NonNull
        @Override
        public MetadataMap getMeta() {
            return this.trait.meta;
        }

        @Override
        @Deprecated
        public void setSkin(@NonNull String skinPlayer) {
            try {
                this.npc.data().set(NPC.PLAYER_SKIN_UUID_METADATA, skinPlayer);
                this.npc.data().set(NPC.PLAYER_SKIN_USE_LATEST, true);

                if (this.npc instanceof SkinnableEntity) {
                    ((SkinnableEntity) this.npc).getSkinTracker().notifySkinChange(true);
                }
                if (this.npc.getEntity() instanceof SkinnableEntity) {
                    ((SkinnableEntity) this.npc.getEntity()).getSkinTracker().notifySkinChange(true);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        @Override
        public void setSkin(@NonNull String textures, @NonNull String signature) {
            try {
                this.npc.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_METADATA, textures);
                this.npc.data().set(NPC.PLAYER_SKIN_TEXTURE_PROPERTIES_SIGN_METADATA, signature);
                this.npc.data().set("cached-skin-uuid-name", "null");
                this.npc.data().set("player-skin-name", "null");
                this.npc.data().set("cached-skin-uuid", UUID.randomUUID().toString());
                this.npc.data().set(NPC.PLAYER_SKIN_USE_LATEST, false);

                if (this.npc instanceof SkinnableEntity) {
                    ((SkinnableEntity) this.npc).getSkinTracker().notifySkinChange(true);
                }
                if (this.npc.getEntity() instanceof SkinnableEntity) {
                    ((SkinnableEntity) this.npc.getEntity()).getSkinTracker().notifySkinChange(true);
                }
            } catch (Throwable e) {
                e.printStackTrace();
            }
        }

        @Override
        public void setName(@NonNull String name) {
            this.npc.setName(name);
        }

        @Override
        public void setShowNametag(boolean show) {
            this.npc.data().set(NPC.NAMEPLATE_VISIBLE_METADATA, show);
        }

        @NonNull
        @Override
        public Location getInitialSpawn() {
            return this.initialSpawn;
        }

        @NonNull
        @Override
        public NPC getNpc() {
            return this.npc;
        }
    }
}
