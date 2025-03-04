package dev.zoid.warpracticecore.builder;

import dev.zoid.warpracticecore.utils.Managers;
import com.mojang.authlib.GameProfile;
import com.mojang.authlib.properties.Property;
import it.unimi.dsi.fastutil.Pair;
import it.unimi.dsi.fastutil.objects.ObjectArrayList;
import lombok.Getter;
import lombok.Setter;
import net.minecraft.network.protocol.game.ClientGamePacketListener;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerEntity;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_21_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import net.kyori.adventure.text.Component;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.world.entity.decoration.ArmorStand;

import java.io.IOException;
import java.util.*;
import java.util.logging.Level;

import static net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER;

@Getter
public class NPCBuilder {
    World spawn = Bukkit.getWorld("spawn");
    public final List<FakePlayerHolder> createdFakePlayers = new ArrayList<>();

    public FakePlayerHolder build(FakePlayerBuilder fakePlayerBuilder, HologramLinesBuilder hologramLinesBuilder) {
        FakePlayerHolder fakePlayerHolder = NPCBuilder.FakePlayerHolder.of(
                fakePlayerBuilder
        );
        hologramLinesBuilder.getLines().forEach(lineText -> {

        });

        createdFakePlayers.add(fakePlayerHolder);
        return fakePlayerHolder;
    }

    public void addViewer(Player player) {
        for (FakePlayerHolder FakePlayerHolder : createdFakePlayers) {
            FakePlayerHolder.addViewer(player);
        }
    }

    public void removeViewer(Player player) {
        for (FakePlayerHolder FakePlayerHolder : createdFakePlayers) {
            FakePlayerHolder.removeViewer(player);
        }
    }

    @Getter
    @Setter
    public static class FakePlayerHolder {
        private final double x;
        private double y;
        private final double z;

        private final ServerPlayer entity;

        private final List<Player> viewers = ObjectArrayList.of();
        private final List<Component> lines = ObjectArrayList.of();
        private final List<HologramPacketHolder> packetQueues = ObjectArrayList.of();

        private List<Packet<?>> playerPackets = ObjectArrayList.of();

        public FakePlayerHolder(FakePlayerBuilder builder) {
            this.x = builder.x;
            this.y = builder.y;
            this.z = builder.z;

            this.entity = builder.entity;
        }


        public void addViewer(Player player) {
            ServerGamePacketListenerImpl connection = ((CraftPlayer) player).getHandle().connection;
            getPacketQueue().forEach(connection::send);

            viewers.add(player);
        }

        public void updateViewers() {
            for (Player player : viewers) {
                packetQueues.forEach(packetHolder -> ((CraftPlayer) player).getHandle().connection.send(packetHolder.METADATA));
            }
        }

        public void removeViewer(Player player) {
            viewers.remove(player);
        }

        public List<Packet<?>> getPacketQueue() {
            List<Packet<?>> packetQueue = ObjectArrayList.of();
            if (!playerPackets.isEmpty()) {
                packetQueue.add(new ClientboundPlayerInfoUpdatePacket(ADD_PLAYER, entity));

                ServerEntity serverEntity = new ServerEntity(
                        Managers.WORLD.nmsSpawn,
                        this.entity,
                        0,
                        false,
                        packet -> {},
                        Set.of()
                );
                packetQueue.add(entity.getAddEntityPacket(serverEntity));

                SynchedEntityData data = this.entity.getEntityData();
                data.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) (0x01 | 0x04 | 0x08 | 0x10 | 0x20 | 0x40));

                packetQueue.add(new ClientboundSetEntityDataPacket(this.entity.getId(), data.getNonDefaultValues()));
            }

            packetQueues.forEach(packetHolder -> packetQueue.addAll(packetHolder.getPacketQueue()));
            return packetQueue;
        }

        public void decrementYOffset() {
            this.y -= 0.3D;
        }

        public static FakePlayerHolder of(FakePlayerBuilder builder) {
            return new FakePlayerHolder(builder);
        }
    }

    @Getter
    public static class HologramPacketHolder {
        public ArmorStand armorStand;

        public Packet<ClientGamePacketListener> ADD_ENTITY;
        public ClientboundSetEntityDataPacket METADATA;

        public HologramPacketHolder(ArmorStand armorStand) {
            this.armorStand = armorStand;

            ServerEntity serverEntity = new ServerEntity(
                    Managers.WORLD.nmsSpawn,
                    this.armorStand,
                    0,
                    false,
                    packet -> {},
                    Set.of()
            );
            this.ADD_ENTITY = this.armorStand.getAddEntityPacket(serverEntity);
            buildMetadataPacket();
        }

        private void buildMetadataPacket() {
            this.METADATA = new ClientboundSetEntityDataPacket(this.armorStand.getId(), this.armorStand.getEntityData().getNonDefaultValues());
        }

        public List<Packet<?>> getPacketQueue() {
            return ObjectArrayList.of(ADD_ENTITY, METADATA);
        }

        public void setCustomName(Component name) {
            this.armorStand.getBukkitEntity().customName(name);
            buildMetadataPacket();
        }
    }

    @Getter
    public static class HologramLinesBuilder {
        private final List<String> lines;

        public HologramLinesBuilder(String... lines) {
            this.lines = Arrays.asList(lines);
        }

        public static HologramLinesBuilder create(String... lines) {
            return new HologramLinesBuilder(lines);
        }
    }

    private static int globalID = 0;
    public static class FakePlayerBuilder {
        public final double x;
        public final double y;
        public final double z;
        private final ServerPlayer entity;

        public FakePlayerBuilder(double x,
                                 double y,
                                 double z,
                                 int id,
                                 UUID skinUUID
        ) {
            this.x = x;
            this.y = y;
            this.z = z;

            GameProfile gameProfile = new GameProfile(UUID.randomUUID(), "NPC-" + id);
            Pair<String, String> skinData = fetchSkinData(skinUUID);
            gameProfile.getProperties().put("textures", new Property("textures", skinData.key(), skinData.value()));
            entity = new ServerPlayer(MinecraftServer.getServer(), Managers.WORLD.nmsOverworld, gameProfile, ClientInformation.createDefault());
        }

        public Packet<ClientGamePacketListener> getPacket() {
            ServerEntity serverEntity = new ServerEntity(
                    Managers.WORLD.nmsSpawn,
                    this.entity,
                    0,
                    false,
                    packet -> {},
                    Set.of()
            );
            return this.entity.getAddEntityPacket(serverEntity);
        }

        public FakePlayerBuilder of(double x, double y, double z, UUID skinUUID) {
            return new FakePlayerBuilder(x, y, z, globalID++, skinUUID);
        }
    }

    private static Pair<String, String> fetchSkinData(@NotNull UUID uniqueId) {
        try {
            return java.util.concurrent.CompletableFuture.supplyAsync(() -> {
                try {
                    java.net.URL url = new java.net.URL(String.format("https://sessionserver.mojang.com/session/minecraft/profile/%s/?unsigned=false", uniqueId.toString()));
                    java.io.InputStreamReader streamReader = new java.io.InputStreamReader(url.openStream());

                    com.google.gson.JsonObject properties = com.google.gson.JsonParser.parseReader(streamReader).getAsJsonObject().get("properties").getAsJsonArray()
                            .get(0).getAsJsonObject();

                    final String texture = properties.get("value").getAsString();
                    final String signature = properties.get("signature").getAsString();

                    return Pair.of(texture, signature);
                } catch (IOException ex) {
                    Bukkit.getLogger().log(Level.SEVERE, "There was an error fetching session data for " + uniqueId, ex);
                    return null;
                }
            }).get(10L, java.util.concurrent.TimeUnit.SECONDS);
        } catch (InterruptedException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "The thread was interrupted when fetching Mojang skin data", ex);
            return null;
        } catch (java.util.concurrent.TimeoutException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Mojang skin data request timed out after 10 seconds", ex);
            return null;
        } catch (java.util.concurrent.ExecutionException ex) {
            Bukkit.getLogger().log(Level.SEVERE, "Mojang skin data request threw an exception", ex);
            return null;
        }
    }
}
