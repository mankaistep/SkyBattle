package manaki.plugin.skybattle.util;

import manaki.plugin.skybattle.SkyBattle;
import org.bukkit.Bukkit;
import org.bukkit.scheduler.BukkitRunnable;

public class Tasks {

    public static void sync(Runnable r) {
        Bukkit.getScheduler().runTask(SkyBattle.get(), r);
    }

    public static void sync(Runnable r, int later) {
        Bukkit.getScheduler().runTaskLater(SkyBattle.get(), r, later);
    }

    public static int sync(Runnable r, int later, int interval) {
        return Bukkit.getScheduler().runTaskTimer(SkyBattle.get(), r, later, interval).getTaskId();
    }

    public static void sync(Runnable r, int later, int interval, int times) {
        new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                i++;
                if (i > times) {
                    this.cancel();
                    return;
                }
                r.run();
            }
        }.runTaskTimer(SkyBattle.get(), later, interval);
    }

    public static void sync(Runnable r, int later, int interval, long period) {
        long start = System.currentTimeMillis();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - start >= period) {
                    this.cancel();
                    return;
                }
                r.run();
            }
        }.runTaskTimer(SkyBattle.get(), later, interval);
    }

    public static void async(Runnable r) {
        Bukkit.getScheduler().runTaskAsynchronously(SkyBattle.get(), r);
    }

    public static void async(Runnable r, int later) {
        Bukkit.getScheduler().runTaskLaterAsynchronously(SkyBattle.get(), r, later);
    }

    public static void async(Runnable r, int later, int interval) {
        Bukkit.getScheduler().runTaskTimerAsynchronously(SkyBattle.get(), r, later, interval);
    }

    public static void async(Runnable r, int later, int interval, int times) {
        new BukkitRunnable() {
            int i = 0;
            @Override
            public void run() {
                i++;
                if (i > times) {
                    this.cancel();
                    return;
                }
                r.run();
            }
        }.runTaskTimerAsynchronously(SkyBattle.get(), later, interval);
    }

    public static void async(Runnable r, int later, int interval, long period) {
        long start = System.currentTimeMillis();
        new BukkitRunnable() {
            @Override
            public void run() {
                if (System.currentTimeMillis() - start >= period) {
                    this.cancel();
                    return;
                }
                r.run();
            }
        }.runTaskTimerAsynchronously(SkyBattle.get(), later, interval);
    }

}
