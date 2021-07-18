package manaki.plugin.skybattle.util;

import com.google.common.collect.Maps;
import manaki.plugin.skybattle.SkyBattle;
import org.bukkit.Bukkit;
import org.bukkit.Particle;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Map;

public class Invisibles extends BukkitRunnable {

    public static final Map<String, Long> hiddenPlayers = Maps.newConcurrentMap();

    public static void hide(String name, long milis) {
        var player = Bukkit.getPlayer(name);
        if (player == null) return;

        hiddenPlayers.put(name, hiddenPlayers.getOrDefault(name, System.currentTimeMillis()) + milis);
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p != player) p.hidePlayer(SkyBattle.get(), player);
        }
    }

    public static boolean isHidden(String name) {
        return hiddenPlayers.containsKey(name);
    }

    @Override
    public void run() {
        // Check hidden
        for (Map.Entry<String, Long> e : hiddenPlayers.entrySet()) {
            var name = e.getKey();
            var expired = e.getValue();

            // Expired
            var player = Bukkit.getPlayer(name);
            if (System.currentTimeMillis() > expired) {
                // Remove from data
                hiddenPlayers.remove(name);

                // Remove if invalid
                if (player == null) continue;

                // Show
                for (Player p : Bukkit.getOnlinePlayers()) {
                    if (p != player) p.showPlayer(SkyBattle.get(), player);
                }
            }

            // Particle
            if (player == null) return;
            player.getWorld().spawnParticle(Particle.SPELL, player.getLocation().clone().add(0, 1, 0), 5, 1, 1, 1, 0.1);
        }
    }

}
