package manaki.plugin.skybattle.listener;

import manaki.plugin.skybattle.game.state.SupplyState;
import manaki.plugin.skybattle.game.util.Games;
import manaki.plugin.skybattle.util.Utils;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
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

    @EventHandler
    public void onInvOpen(PlayerInteractEvent e) {
        var p = e.getPlayer();
        var state = Games.getCurrentGame(p);
        if (state == null) return;

        var block = e.getClickedBlock();
        if (block == null || block.getType() == Material.AIR) return;

        for (SupplyState ss : state.getSupplyStates()) {
            if (Utils.isSameBlock(ss.getLocation(), block.getLocation())) ss.setOpened(true);
        }
    }

}
