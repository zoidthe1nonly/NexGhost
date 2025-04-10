package dev.zoid.nexghost.listener;

import dev.zoid.nexghost.NexGhost;
import dev.zoid.nexghost.packet.PacketManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.inventory.ItemStack;

public class DeathListener implements Listener {

    private final NexGhost plugin;
    private final PacketManager packetManager;

    public DeathListener(NexGhost plugin) {
        this.plugin = plugin;
        this.packetManager = plugin.getPacketManager();
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        ItemStack mainHand = player.getInventory().getItemInMainHand();
        ItemStack offHand = player.getInventory().getItemInOffHand();

        boolean hasTotem = mainHand.getType() == Material.TOTEM_OF_UNDYING || offHand.getType() == Material.TOTEM_OF_UNDYING;

        if (hasTotem) {
            int selectedSlot = player.getInventory().getHeldItemSlot();
            if (mainHand.getType() == Material.TOTEM_OF_UNDYING) {
                packetManager.updateSlot(player, selectedSlot);
            }
            if (offHand.getType() == Material.TOTEM_OF_UNDYING) {
                packetManager.updateSlot(player, 45 - 36);
            }
        }
    }
}