package hyperburger.me.burgerapi.nms.v1_20_R3;

import com.mojang.authlib.GameProfile;

import hyperburger.me.burgerapi.nms.INMS;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import net.minecraft.network.Connection;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoUpdatePacket;
import net.minecraft.network.protocol.game.ClientboundSetEntityDataPacket;
import net.minecraft.network.syncher.EntityDataAccessor;
import net.minecraft.network.syncher.EntityDataSerializers;
import net.minecraft.network.syncher.SynchedEntityData;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ClientInformation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.server.network.CommonListenerCookie;
import net.minecraft.server.network.ServerGamePacketListenerImpl;
import net.minecraft.server.players.PlayerList;
import net.minecraft.world.level.GameType;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;
import org.checkerframework.checker.units.qual.C;

import java.lang.reflect.Field;
import java.util.*;

public class NMSHandler implements INMS {

    private final HashMap<ServerPlayer, ClientboundPlayerInfoUpdatePacket.Entry> playerEntryHashMap = new HashMap<>();

    @Override
    public ServerPlayer createNPC(String npcName, Location location) {

        // Get the MinecraftServer and ServerLevel instances
        final MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
        final ServerLevel serverLevel = ((CraftWorld) location.getWorld()).getHandle();

        // Create a GameProfile for the NPC
        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), npcName);

        ServerPlayer npc = new ServerPlayer(minecraftServer, serverLevel, gameProfile, ClientInformation.createDefault());

        // Create the NPC
        npc.setPos(location.getX(), location.getY(), location.getZ());

        // Set up entity metadata (skin parts)
        SynchedEntityData synchedEntityData = npc.getEntityData();
        synchedEntityData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 127);

        ClientboundPlayerInfoUpdatePacket.Entry entry = new ClientboundPlayerInfoUpdatePacket.Entry(gameProfile.getId(), gameProfile, true, 0, GameType.CREATIVE, null, null);

        playerEntryHashMap.put(npc, entry);
        return npc;
     }


     @Override
     public void spawnNPC(ServerPlayer npc) {
        // ServerPlayer serverPlayer = ((CraftPlayer) player).getHandle()

         FriendlyByteBuf friendlyByteBuf = new FriendlyByteBuf(Unpooled.buffer());
         friendlyByteBuf.writeEnumSet(EnumSet.of(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED), ClientboundPlayerInfoUpdatePacket.Action.class);
         friendlyByteBuf.writeCollection(List.of(playerEntryHashMap.get(npc)), (buf, entry) -> {
             buf.writeUUID(entry.profileId());
             ClientboundPlayerInfoUpdatePacket.Action.Writer writer = (ClientboundPlayerInfoUpdatePacket.Action.Writer) getValue(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, "h");
             writer.write(buf, entry);

             ClientboundPlayerInfoUpdatePacket.Action.Writer writer2 = (ClientboundPlayerInfoUpdatePacket.Action.Writer) getValue(ClientboundPlayerInfoUpdatePacket.Action.UPDATE_LISTED, "h");
             writer2.write(buf, entry);
         });

         MinecraftServer.getServer().getPlayerList().players.forEach(serverPlayer-> {
             ServerGamePacketListenerImpl connection = serverPlayer.connection;
             ClientboundPlayerInfoUpdatePacket packet = new ClientboundPlayerInfoUpdatePacket(friendlyByteBuf);
             connection.send(packet);
             connection.send(npc.getAddEntityPacket());
         });
    }


    @Override
    public void sendPacket(Object packet, Player player) {
        // Cast the packet and send it to the player
        ((CraftPlayer) player).getHandle().connection.send((Packet<?>) packet);
    }

    @Override
    public void setValue(Object object, String fieldName, Object value) {
        try {
            // Use reflection to set a field value
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(object, value);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }

    public Object getValue(Object ob, String fieldName) {
        try {
            // Use reflection to set a field value
            Field field = ob.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            return field.get(ob);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        return null;
    }
}


//    @Override
//    public void spawnNPC(Player player, String npcName, Location location) {
//        // Convert Bukkit player to NMS ServerPlayer
//        CraftPlayer craftPlayer = (CraftPlayer) player;
//        ServerPlayer serverPlayer = craftPlayer.getHandle();
//
//        // Get the MinecraftServer and ServerLevel instances
//        MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
//        ServerLevel serverLevel = ((CraftWorld) location.getWorld()).getHandle();
//
//        // Create a GameProfile for the NPC
//        GameProfile gameProfile = new GameProfile(UUID.randomUUID(), npcName);
//
//        ServerPlayer npc = new ServerPlayer(minecraftServer, serverLevel, gameProfile, ClientInformation.createDefault());
//        // Create the NPC
//        npc.setPos(location.getX(), location.getY(), location.getZ());
//
//        // Set up entity metadata (skin parts)
//        SynchedEntityData synchedEntityData = serverPlayer.getEntityData();
//        synchedEntityData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 127);
//
//        // Set the connection of the NPC to avoid NullPointerException
//        setValue(npc, "c", ((CraftPlayer) player).getHandle().connection);
//
//
//        // Send packets to add the NPC
//        sendPacket(new ClientboundPlayerInfoUpdatePacket(ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER, npc), player);
//        sendPacket(new ClientboundAddEntityPacket(npc), player);
//        sendPacket(new ClientboundSetEntityDataPacket(npc.getId(), synchedEntityData.getNonDefaultValues()), player);
//
//        // TODO: Add code to remove the NPC from the tab list after a short delay
//    }