package manaki.plugin.skybattle.util;

import io.lumine.xikage.mythicmobs.MythicMobs;
import net.minecraft.server.v1_16_R3.EntityPlayer;
import net.minecraft.server.v1_16_R3.PacketPlayOutWorldBorder;
import net.minecraft.server.v1_16_R3.WorldBorder;
import net.minecraft.server.v1_16_R3.WorldServer;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Random;

public class Utils {

    public static void random(List list, int amount) {
        int toRemove = list.size() - amount;
        for (int i = 0 ; i < toRemove ; i++) {
            list.remove(new Random().nextInt(list.size()));
        }
    }

    public static Location randomSafeLocation(Location center, int r) {
        for (int i = 0 ; i < 5 ; i++) {
            var rx = center.getBlockX() + randomInt(-1 * r, r);
            var rz = center.getBlockZ() + randomInt(-1 * r, r);
            var ry = center.getY() + 10;
            var rl = new Location(center.getWorld(), rx, ry, rz);

            var l = getGroundBlock(rl).clone().add(0, 1, 0);
            if (l.getBlock().getType() == Material.AIR || !l.getBlock().isSolid()) return l;
        }

        return null;
    }

    public static Location getGroundBlock(Location loc) {
        Location locBelow = loc.clone();
        if (locBelow.getY() <= -64) return locBelow;
        if(locBelow.getBlock().getType() == Material.AIR || !locBelow.getBlock().isSolid()) {
            locBelow = loc.subtract(0, 1, 0);
            locBelow = getGroundBlock(locBelow);
        }
        return locBelow;
    }

    public static double random(double min, double max) {
        return (new Random().nextInt(new Double((max - min) * 1000).intValue()) + min * 1000) / 1000;
    }

    public static int randomInt(int min, int max) {
        return new Random().nextInt(max - min + 1) + min;
    }

    public static boolean rate(double chance) {
        if (chance >= 100)
            return true;
        double rate = chance * 100;
        int random = new Random().nextInt(10000);
        return random < rate;
    }

    public static int countMythicMobsAround(Player p, double r) {
        int c = 0;
        for (LivingEntity le : MythicMobs.inst().getMobManager().getAllMythicEntities()) {
            if (le != p && le.getWorld() == p.getWorld() && le.getLocation().distanceSquared(p.getLocation()) <= r * r) c++;
        }
        return c;
    }

    public static int countPlayersAround(Player p, double r) {
        int c = 0;
        for (Player pc : Bukkit.getOnlinePlayers()) {
            if (pc != p && pc.getWorld() == p.getWorld() && pc.getLocation().distanceSquared(p.getLocation()) <= r * r) c++;
        }
        return c;
    }

    public static void toSpawn(Player player) {
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spawn " + player.getName());
    }

    public static void sendBorder(Player p, int centerX, int centerZ, int radius) {
        WorldBorder wb = new WorldBorder();
        wb.world = ((CraftWorld) p.getWorld()).getHandle();
        wb.setCenter(centerX, centerZ);
        wb.setSize(radius);
        wb.setWarningDistance(0);
        EntityPlayer player = ((CraftPlayer) p).getHandle();
        wb.world = (WorldServer) player.world;
        player.playerConnection.sendPacket(new PacketPlayOutWorldBorder(wb, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_SIZE));
        player.playerConnection.sendPacket(new PacketPlayOutWorldBorder(wb, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_CENTER));
        player.playerConnection.sendPacket(new PacketPlayOutWorldBorder(wb, PacketPlayOutWorldBorder.EnumWorldBorderAction.SET_WARNING_BLOCKS));
        player.playerConnection.sendPacket(new PacketPlayOutWorldBorder(wb, PacketPlayOutWorldBorder.EnumWorldBorderAction.LERP_SIZE));
    }

}
