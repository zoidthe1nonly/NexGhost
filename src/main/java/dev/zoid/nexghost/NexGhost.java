package dev.zoid.nexghost;

import dev.zoid.nexghost.listener.DeathListener;
import dev.zoid.nexghost.packet.PacketManager;
import org.bukkit.plugin.java.JavaPlugin;

public final class NexGhost extends JavaPlugin {

    private PacketManager packetManager;

    @Override
    public void onEnable() {
        try {
            packetManager = new PacketManager();
        } catch (Exception e) {
            e.printStackTrace();
            getServer().getPluginManager().disablePlugin(this);
            return;
        }
        getServer().getPluginManager().registerEvents(new DeathListener(this), this);
    }

    public PacketManager getPacketManager() {
        return packetManager;
    }
}