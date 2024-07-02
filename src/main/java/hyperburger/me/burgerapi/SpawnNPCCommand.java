package hyperburger.me.burgerapi;

import com.mojang.authlib.GameProfile;
import hyperburger.me.burgerapi.nms.NMS;
import hyperburger.me.burgerapi.nms.v1_20_R3.NMSHandler;
import net.minecraft.network.Connection;
import net.minecraft.network.protocol.Packet;
import net.minecraft.network.protocol.PacketFlow;
import net.minecraft.network.protocol.game.ClientboundAddEntityPacket;
import net.minecraft.network.protocol.game.ClientboundPlayerInfoRemovePacket;
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
import net.minecraft.world.entity.HumanoidArm;
import net.minecraft.world.entity.player.ChatVisiblity;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Server;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.v1_20_R3.CraftServer;
import org.bukkit.craftbukkit.v1_20_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_20_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.lang.reflect.Field;
import java.util.Collections;
import java.util.EnumSet;
import java.util.UUID;

public class SpawnNPCCommand implements CommandExecutor {

    private final BurgerAPI plugin;

    public SpawnNPCCommand(BurgerAPI plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("This command can only be used by players.");
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission("nmsapi.spawnnpc")) {
            player.sendMessage("You don't have permission to use this command.");
            return true;
        }

        if (args.length < 1) {

            NMS.getHandler().spawnNPC(NMS.getHandler().createNPC("bbb", player.getLocation()));
//            Location location = player.getLocation();
//
//            MinecraftServer minecraftServer = ((CraftServer) Bukkit.getServer()).getServer();
//            ServerLevel serverLevel = ((CraftWorld) location.getWorld()).getHandle();
//            ServerPlayer serverPlayer = new ServerPlayer(minecraftServer, serverLevel, new GameProfile(UUID.randomUUID(), "Billy"), ClientInformation.createDefault());
//            serverPlayer.setPos(location.getX(), location.getY(), location.getZ());
//
//            SynchedEntityData synchedEntityData = serverPlayer.getEntityData();
//            synchedEntityData.set(new EntityDataAccessor<>(17, EntityDataSerializers.BYTE), (byte) 127);
//
//            setValue(serverPlayer, "c", ((CraftPlayer) player).getHandle().connection);
//
//            sendPacket(new ClientboundPlayerInfoUpdatePacket(
//            ClientboundPlayerInfoUpdatePacket.Action.ADD_PLAYER,
//            serverPlayer), player);
//            sendPacket(new ClientboundAddEntityPacket(serverPlayer), player);
//            sendPacket(new ClientboundSetEntityDataPacket(serverPlayer.getId(), synchedEntityData.getNonDefaultValues()), player);
//
//            player.sendMessage("NPC successfully spawned.");
            return true;
        }

//        Bukkit.getScheduler().runTaskLaterAsynchronously(plugin, new Runnable() {
//                @Override
//                public void run() {
////                    sendPacket(new ClientboundPlayerInfoRemovePacket(Collections.singletonList(serverPlayer.getUUID())), player);
//                }
//            }, 40);


//        String npcName = args[0];
//        Location spawnLocation;
//
//        if (args.length > 1) {
//            Player targetPlayer = Bukkit.getPlayer(args[1]);
//            if (targetPlayer == null) {
//                player.sendMessage("Player not found: " + args[1]);
//                return true;
//            }
//            spawnLocation = targetPlayer.getLocation();
//        } else {
//            spawnLocation = player.getLocation();
//        }
//
//        try {
//            Entity npc = plugin.getNmsHandler().spawnNPC(spawnLocation, npcName);
//            if (npc != null) {
//                npc.setCustomNameVisible(true);
//                player.sendMessage("NPC " + npcName + " has been spawned.");
//            } else {
//                player.sendMessage("Failed to spawn NPC.");
//            }
//        } catch (Exception e) {
//            player.sendMessage("An error occurred while spawning the NPC: " + e.getMessage());
//            e.printStackTrace();
//        }
//
//        return true;
        return true;
    }

    public void sendPacket(Packet<?> packet, Player player) {
        ((CraftPlayer) player).getHandle().connection.send(packet);
    }

    public void setValue(Object packet, String fieldName, Object value) {
        try {
            Field field = packet.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(packet, value);
        } catch (Exception exception) {
            exception.printStackTrace();
        }
    }
}