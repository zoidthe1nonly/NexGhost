
package dev.zoid.nexghost.packet;

import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class PacketManager {

    private static String bukkitVersion;
    private final String version;
    private final Class<?> craftPlayerClass;
    private final Class<?> craftItemStackClass;
    private final Class<?> serverPlayerClass;
    private final Class<?> itemStackClass;
    private final Class<?> containerSetSlotPacketClass;
    private final Class<?> connectionClass;
    private final Method getHandleMethod;
    private final Method asNMSCopyMethod;
    private final Constructor<?> containerSetSlotPacketConstructor;
    private final Field connectionField;
    private final Method sendPacketMethod;

    public PacketManager() throws Exception {
        bukkitVersion = Bukkit.getBukkitVersion();
        if (bukkitVersion.contains("1.19.4")) {
            version = "v1_19_R3";
        } else if (bukkitVersion.contains("1.19")) {
            version = "v1_19_R1";
        } else if (bukkitVersion.contains("1.20.6")) {
            version = "v1_20_R4";
        } else if (bukkitVersion.contains("1.20.5")) {
            version = "v1_20_R4";
        } else if (bukkitVersion.contains("1.20.4")) {
            version = "v1_20_R3";
        } else if (bukkitVersion.contains("1.20.2")) {
            version = "v1_20_R2";
        } else if (bukkitVersion.contains("1.20")) {
            version = "v1_20_R1";
        } else if (bukkitVersion.contains("1.21.4")) {
            version = "v1_21_R3";
        } else if (bukkitVersion.contains("1.21.3")) {
            version = "v1_21_R2";
        } else if (bukkitVersion.contains("1.21.1")) {
            version = "v1_21_R1";
        } else if (bukkitVersion.contains("1.21")) {
            version = "v1_21_R1";
        } else {
            throw new IllegalStateException("Unsupported Minecraft version: " + bukkitVersion);
        }

        craftPlayerClass = Class.forName("org.bukkit.craftbukkit." + version + ".entity.CraftPlayer");
        craftItemStackClass = Class.forName("org.bukkit.craftbukkit." + version + ".inventory.CraftItemStack");
        serverPlayerClass = Class.forName("net.minecraft.server.level.ServerPlayer");
        itemStackClass = Class.forName("net.minecraft.world.item.ItemStack");
        containerSetSlotPacketClass = Class.forName("net.minecraft.network.protocol.game.ClientboundContainerSetSlotPacket");

        getHandleMethod = craftPlayerClass.getMethod("getHandle");
        asNMSCopyMethod = craftItemStackClass.getMethod("asNMSCopy", org.bukkit.inventory.ItemStack.class);
        containerSetSlotPacketConstructor = containerSetSlotPacketClass.getConstructor(int.class, int.class, int.class, itemStackClass);

        connectionField = serverPlayerClass.getDeclaredField("connection");
        connectionField.setAccessible(true);
        connectionClass = connectionField.getType();
        sendPacketMethod = connectionClass.getMethod("send", Class.forName("net.minecraft.network.protocol.Packet"));
    }

    public static String getVersion() {
        return bukkitVersion;
    }

    public void updateSlot(Player player, int slot) {
        try {
            Object serverPlayer = getHandleMethod.invoke(craftPlayerClass.cast(player));
            Object nmsItemStack = asNMSCopyMethod.invoke(null, new ItemStack(Material.AIR));

            Object connection = connectionField.get(serverPlayer);

            Object packet1 = containerSetSlotPacketConstructor.newInstance(-1, -1, slot + 36, nmsItemStack);
            sendPacketMethod.invoke(connection, packet1);

            Object packet2 = containerSetSlotPacketConstructor.newInstance(0, 0, slot + 36, nmsItemStack);
            sendPacketMethod.invoke(connection, packet2);

            Object packet3 = containerSetSlotPacketConstructor.newInstance(-2, -2, slot + 36, nmsItemStack);
            sendPacketMethod.invoke(connection, packet3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateOffHandSlot(Player player) {
        try {
            Object serverPlayer = getHandleMethod.invoke(craftPlayerClass.cast(player));
            Object nmsItemStack = asNMSCopyMethod.invoke(null, new ItemStack(Material.AIR));

            Object connection = connectionField.get(serverPlayer);

            Object packet1 = containerSetSlotPacketConstructor.newInstance(-1, -1, 45, nmsItemStack);
            sendPacketMethod.invoke(connection, packet1);

            Object packet2 = containerSetSlotPacketConstructor.newInstance(0, 0, 45, nmsItemStack);
            sendPacketMethod.invoke(connection, packet2);

            Object packet3 = containerSetSlotPacketConstructor.newInstance(-2, -2, 45, nmsItemStack);
            sendPacketMethod.invoke(connection, packet3);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
