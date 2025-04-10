package dev.zoid.nexghost.listener;

import dev.zoid.nexghost.NexGhost;
import dev.zoid.nexghost.packet.PacketManager;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

public class DeathListener implements Listener {

    private final NexGhost plugin;
    private final PacketManager packetManager;
    private final Map<UUID, Long> commandExecutionTime = new HashMap<>();
    private static final long COMMAND_COOLDOWN = 250;

    public DeathListener(NexGhost plugin) {
        this.plugin = plugin;
        this.packetManager = plugin.getPacketManager();
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDeath(PlayerDeathEvent event) {
        Player player = event.getEntity();
        UUID playerId = player.getUniqueId();

        if (commandExecutionTime.containsKey(playerId)) {
            long lastCommand = commandExecutionTime.get(playerId);
            if (System.currentTimeMillis() - lastCommand < COMMAND_COOLDOWN) {
                removeAllTotems(player);
            }
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onCommandPreprocess(PlayerCommandPreprocessEvent event) {
        String cmd = event.getMessage().toLowerCase();
        if (cmd.startsWith("/kill") || cmd.startsWith("/damage") || cmd.startsWith("/minecraft:kill") || cmd.startsWith("/minecraft:damage")) {
            Player player = event.getPlayer();
            commandExecutionTime.put(player.getUniqueId(), System.currentTimeMillis());

            new BukkitRunnable() {
                @Override
                public void run() {
                    removeAllTotems(player);
                }
            }.runTask(plugin);

            new BukkitRunnable() {
                @Override
                public void run() {
                    removeAllTotems(player);
                }
            }.runTaskLater(plugin, 1L);
        }
    }

    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerDamage(EntityDamageEvent event) {
        if (!(event.getEntity() instanceof Player)) return;
        Player player = (Player) event.getEntity();
        UUID playerId = player.getUniqueId();

        if (commandExecutionTime.containsKey(playerId)) {
            long lastCommand = commandExecutionTime.get(playerId);
            if (System.currentTimeMillis() - lastCommand < COMMAND_COOLDOWN) {
                removeAllTotems(player);

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        if (!player.isDead()) {
                            removeAllTotems(player);
                        }
                    }
                }.runTaskLater(plugin, 1L);
            }
        }
    }

    private void removeAllTotems(Player player) {
        for (int i = 0; i < 9; i++) {
            ItemStack item = player.getInventory().getItem(i);
            if (item != null && item.getType() == Material.TOTEM_OF_UNDYING) {
                packetManager.updateSlot(player, i);
            }
        }

        ItemStack offHand = player.getInventory().getItemInOffHand();
        if (offHand != null && offHand.getType() == Material.TOTEM_OF_UNDYING) {
            packetManager.updateOffHandSlot(player);
        }
    }
}
