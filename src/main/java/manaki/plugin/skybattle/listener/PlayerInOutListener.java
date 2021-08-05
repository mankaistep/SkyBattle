package manaki.plugin.skybattle.listener;

import manaki.plugin.skybattle.game.Games;
import manaki.plugin.skybattle.hide.VisionHides;
import manaki.plugin.skybattle.util.Tasks;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerTeleportEvent;

public class PlayerInOutListener implements Listener {

    // Hidden players join
    @EventHandler
    public void onHiddenPlayerJoin(PlayerJoinEvent e) {
        Player p = e.getPlayer();
        VisionHides.show(p);
    }

    // Hidden players quit
    @EventHandler
    public void onHiddenPlayerQuit(PlayerQuitEvent e) {
        Player p = e.getPlayer();
        VisionHides.show(p);
    }

    // Player death
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        var p = e.getEntity();
        var state = Games.getCurrentGame(p);
        if (state == null) return;

        var ps = state.getPlayerState(p.getName());
        if (ps.isDead()) return;

        var gm = Games.managerFromState(state);
        Tasks.sync(() -> {
            p.spigot().respawn();
            gm.doPlayerOut(p, true);
        });
    }

    // Quit game
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        var p = e.getPlayer();

        var state = Games.getCurrentGame(p);
        if (state == null) return;

        var ps = state.getPlayerState(p.getName());
        if (ps.isDead()) return;

        var gm = Games.managerFromState(state);
        gm.doPlayerOut(p, false);
    }

    // To other world
    @EventHandler
    public void onTeleport(PlayerTeleportEvent e) {
        var p = e.getPlayer();

        var state = Games.getCurrentGame(p);
        if (state == null) return;

        var ps = state.getPlayerState(p.getName());

        Tasks.sync(() -> {
            if (ps.isDead()) return;

            // Check world
            if (p.getWorld() != state.getWorldState().toWorld()) {
                var gm = Games.managerFromState(state);
                gm.doPlayerOut(p, false);
            }
        }, 20);
    }

}
