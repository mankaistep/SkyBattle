package manaki.plugin.skybattle.listener;

import manaki.plugin.skybattle.game.util.Games;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class PlayerListener implements Listener {

    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        var p = e.getEntity();
        var state = Games.getCurrentGame(p);
        if (state == null) return;

        var gm = Games.managerFromState(state);
        gm.playerDead(p);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        var p = e.getPlayer();
        var state = Games.getCurrentGame(p);
        if (state == null) return;

        var gm = Games.managerFromState(state);
        gm.playerQuit(p);
    }

}
