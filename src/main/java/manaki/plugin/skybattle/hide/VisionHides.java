package manaki.plugin.skybattle.hide;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import manaki.plugin.skybattle.SkyBattle;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;
import java.util.Set;

public class VisionHides extends BukkitRunnable {

    private static Map<String, Long> bypassHides = Maps.newConcurrentMap();
    private static Map<String, Set<String>> hiddens = Maps.newConcurrentMap();

    @Override
    public void run() {
        checkAll();
    }

    public static void checkAll() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            for (Player target : Bukkit.getOnlinePlayers()) check(p, target);
        }
    }

    public static void check(Player p, Player target) {
        if (p == target) return;

        // Check bypass
        if (isBypassed(target)) {
            show(p, target);
            return;
        }

        // Vision blocked
        if (!p.hasLineOfSight(target)) {
            // Check hidden
            if (isHidden(p, target)) return;

            // Hide
            p.hidePlayer(SkyBattle.get(), target);
            hide(p, target);
        }
        // Has vission
        else {
            // Not hidden
            if (!isHidden(p, target)) return;

            // Has invisible
            if (Invisibles.isHidden(target.getName())) return;

            show(p, target);
        }
    }

    public static Set<String> getHiddenList(Player p) {
        if (!hiddens.containsKey(p.getName())) hiddens.put(p.getName(), Sets.newHashSet());
        return hiddens.get(p.getName());
    }

    public static boolean isHidden(Player p, Player target) {
        return getHiddenList(p).contains(target.getName());
    }

    public static boolean hide(Player p, Player target) {
//        if (isHidden(p, target)) return false;
        p.hidePlayer(SkyBattle.get(), target);
        getHiddenList(p).add(target.getName());
        return true;
    }

    public static boolean show(Player p, Player target) {
//        if (!isHidden(p, target)) return false;
        p.showPlayer(SkyBattle.get(), target);
        getHiddenList(p).remove(target.getName());
        return true;
    }

    public static void show(Player target) {
        for (Player p : Bukkit.getOnlinePlayers()) {
            show(p, target);
        }
    }

    public static void showAll() {
        for (Player p : Bukkit.getOnlinePlayers()) {
            for (Player p2 : Bukkit.getOnlinePlayers()) {
                VisionHides.show(p, p2);
                p.showPlayer(SkyBattle.get(), p2);
            }
        }
    }

    public static void addBypass(Player target, long milis) {
        bypassHides.put(target.getName(), System.currentTimeMillis() + milis);
    }

    public static boolean isBypassed(Player target) {
        if (bypassHides.containsKey(target.getName()) && bypassHides.get(target.getName()) > System.currentTimeMillis()) {
            return true;
        }
        bypassHides.remove(target.getName());
        return false;
    }

}
