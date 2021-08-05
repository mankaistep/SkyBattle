package manaki.plugin.skybattle.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.lumine.xikage.mythicmobs.MythicMobs;
import manaki.plugin.skybattle.SkyBattle;
import org.bukkit.*;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

public class Utils {

    public static Map<String, String> read(String s) {
        Map<String, String> map = Maps.newHashMap();
        for (String s1 : s.split(";")) {
            map.put(s1.split(":")[0], s1.split(":")[1]);
        }
        return map;
    }

    public static void clearWorldGuardTemporaryData() {
        var plugin = SkyBattle.get();
        var path = plugin.getDataFolder().getPath().replace("SkyBattle", "WorldGuard//worlds");
        var folder = new File(path);
        File[] files = folder.listFiles();
        assert files != null;
        for (File file : files) {
            var name = file.getName();
            for (String world : plugin.getMainConfig().getWorldTemplates().keySet()) {
                if (name.startsWith(world)) {
                    try {
                        FileUtils.deleteDirectory(file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    plugin.getLogger().warning("Delete config " + name + " from WorldGuard");
                }
            }
        }
    }

    public static boolean isOutsideBorder(Player p) {
        Location loc = p.getLocation();
        WorldBorder border = p.getWorld().getWorldBorder();
        double size = border.getSize()/2;
        Location center = border.getCenter();
        double x = loc.getX() - center.getX(), z = loc.getZ() - center.getZ();
        return ((x > size || (-x) > size) || (z > size || (-z) > size));
    }

    public static boolean isSameBlock(Location l1, Location l2) {
        if (l1.getWorld() != l2.getWorld()) return false;
        if (Math.abs(l1.getBlockX() - l2.getBlockX()) > 0.5) return false;
        if (Math.abs(l1.getBlockY() - l2.getBlockY()) > 0.5) return false;
        if (Math.abs(l1.getBlockZ() - l2.getBlockZ()) > 0.5) return false;
        return true;

    }

    public static int calR(int vstart, int vend, long start, long end) {
        int vd = vstart - vend;
        long period = end - start;
        long passed = System.currentTimeMillis() - start;

        int vreduced = Double.valueOf((double) vd * passed / period).intValue();

        return vstart - vreduced;
    }

    public static void random(List list, int amount) {
        int toRemove = list.size() - amount;
        for (int i = 0 ; i < toRemove ; i++) {
            list.remove(new Random().nextInt(list.size()));
        }
    }

    public static Location randomSafeLocation(Location center, int rmin, int rmax) {
        for (int i = 0 ; i < 5 ; i++) {
            var r = Utils.randomInt(rmin, rmax);
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

    public static void setBorder(World world, int centerX, int centerZ, int radius, double damage) {
        @NotNull WorldBorder wb = world.getWorldBorder();
        wb.setDamageAmount(damage);
        if (radius > 0) {
            wb.setCenter(centerX, centerZ);
            wb.setSize(radius * 8);
            wb.setWarningDistance(0);
        }
        else {
            wb.setCenter(centerX + 5000, centerZ);
            wb.setSize(1);
            wb.setDamageBuffer(4990);
        }
    }

    public static String format(int seconds) {
        int m = seconds / 60;
        int s = seconds % 60;

        var ms = m >= 10 ? m + "" : "0" + m;
        var ss = s >= 10 ? s + "" : "0" + s;

        return ms + ":" + ss;
    }

    public static void toSpawn(Player player) {
        if (player == null || !player.isOnline()) return;
        Bukkit.dispatchCommand(Bukkit.getConsoleSender(), "spawn " + player.getName());
    }

    public static Map<String, String> getPlaceholders(Player player) {
        Map<String, String> map = Maps.newHashMap();
        map.put("%player%", player.getName());
        return map;
    }

    public static List<ChatColor> getColors() {
        List<ChatColor> l = Lists.newArrayList(ChatColor.values());
        l.remove(ChatColor.BOLD);
        l.remove(ChatColor.BLACK);
        l.remove(ChatColor.DARK_BLUE);
        l.remove(ChatColor.DARK_RED);
        l.remove(ChatColor.DARK_PURPLE);
        l.remove(ChatColor.ITALIC);
        l.remove(ChatColor.MAGIC);
        l.remove(ChatColor.UNDERLINE);
        l.remove(ChatColor.STRIKETHROUGH);
        l.remove(ChatColor.RESET);

        return l;
    }

    public static int calScore(int top) {
        if (top <= 4) return (5 - top) * 3;
        else return (4 - top) * 3;
    }

    public static int getKillScore() {
        return 1;
    }

    public static String getTopColor(int top) {
        if (top == 1) return "§6";
        if (top == 2) return "§c";
        if (top == 3) return "§9";
        if (top == 4) return "§f";
        return "§7";
    }

    public static int getBossKillScore() {
        return 2;
    }
}
