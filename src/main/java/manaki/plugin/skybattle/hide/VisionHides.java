package manaki.plugin.skybattle.hide;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.game.Games;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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

        // Check spectator
        if (p.getGameMode() == GameMode.SPECTATOR) {
            // If same spec
            if (target.getGameMode() == GameMode.SPECTATOR) {
                if (isHidden(p, target)) show(p, target);
                return;
            }

            // Not spec
            var team = Games.getTeamIn(p.getName());
            if (team != null) {
                boolean has = false;
                for (Player mate : team.getOnlinePlayers()) {
                    if (mate == p) continue;
                    if (mate.hasLineOfSight(target)) {
                        has = true;
                        // Not hidden
                        if (isHidden(p, target)) show(p, target);
                        break;
                    }
                }
                if (!has && !isHidden(p, target)) hide(p, target);
                return;
            }
        }

        // Vision blocked
        if (!p.hasLineOfSight(target)) {
            // Check hidden
            if (isHidden(p, target)) return;

            // Check teammate
            if (Games.isTeammate(p, target)) return;

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
