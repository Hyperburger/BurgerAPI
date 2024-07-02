package hyperburger.me.burgerapi;

import hyperburger.me.burgerapi.nms.NMS;
import hyperburger.me.burgerapi.nms.v1_20_R3.NMSHandler;
import org.bukkit.Location;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public final class BurgerAPI extends JavaPlugin {


    @Override
    public void onEnable() {
        // Plugin startup logic

        NMS.init();
        this.getCommand("spawnnpc").setExecutor(new SpawnNPCCommand(this));
        getLogger().info("NMSAPI has been enabled!");

    }

    @Override
    public void onDisable() {
        // Plugin shutdown logic
        getLogger().info("NMSAPI has been disabled!");
    }



}
