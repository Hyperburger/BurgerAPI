package hyperburger.me.burgerapi.nms;

import net.minecraft.server.level.ServerPlayer;
import org.bukkit.Location;
import org.bukkit.entity.Player;

public interface INMS {

    ServerPlayer createNPC(String npcName, Location location);
    void spawnNPC(ServerPlayer npc);
    void sendPacket(Object packet, Player player);
    void setValue(Object object, String fieldName, Object value);
}
