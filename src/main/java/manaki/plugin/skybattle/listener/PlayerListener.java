package manaki.plugin.skybattle.listener;

import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.game.state.SupplyState;
import manaki.plugin.skybattle.game.util.Games;
import manaki.plugin.skybattle.util.Tasks;
import manaki.plugin.skybattle.util.Utils;
import manaki.plugin.skybattle.util.command.Command;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockBreakEvent;
import org.bukkit.event.block.BlockExpEvent;
import org.bukkit.event.block.BlockExplodeEvent;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryOpenEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.List;
import java.util.Map;

public class PlayerListener implements Listener {

    // Player death
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        var p = e.getEntity();
        var state = Games.getCurrentGame(p);
        if (state == null) return;

        var gm = Games.managerFromState(state);
        gm.playerDead(p);
    }

    // Quit game
    @EventHandler
    public void onQuit(PlayerQuitEvent e) {
        var p = e.getPlayer();
        var state = Games.getCurrentGame(p);
        if (state == null) return;

        // Back to main server
        if (Games.isByPassedInvalid(p)) return;

        var gm = Games.managerFromState(state);
        gm.playerQuit(p.getName());
    }

    // Open supply
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

    // PvP
    @EventHandler
    public void onPvP(EntityDamageByEntityEvent e) {
        if (!(e.getEntity() instanceof Player) || !(e.getDamager() instanceof Player)) return;
        var target = (Player) e.getEntity();
        var damager = (Player) e.getDamager();

        var state = Games.getCurrentGame(target);
        if (state == null) return;
        if (state != Games.getCurrentGame(damager)) return;

        if (state.getTeam(target) == state.getTeam(damager)) {
            e.setCancelled(true);
            damager.sendMessage("§cKhông được tấn công đồng đội!");
        }
    }

    // Build (Place)
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockPlaceEvent e) {
        var p = e.getPlayer();
        var state = Games.getCurrentGame(p);
        if (state == null) return;

        var b = e.getBlock();
        state.addBlockPlaced(b);
    }

    // Build (Break)
    @EventHandler(priority = EventPriority.HIGHEST, ignoreCancelled = true)
    public void onBlockPlace(BlockBreakEvent e) {
        var p = e.getPlayer();
        var state = Games.getCurrentGame(p);
        if (state == null) return;

        var b = e.getBlock();
        if (!state.isBlockPlaced(b)) {
            e.setCancelled(true);
            p.sendMessage("§cBạn chỉ được đặt những block đã đặt ra!");
            return;
        }

        state.removeBlockPlaced(b);
    }

    // Block click
    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent e) {
        var b = e.getClickedBlock();
        if (b == null || b.getType() == Material.AIR) return;

        var p = e.getPlayer();
        var state = Games.getCurrentGame(p);
        if (state == null) return;

        var bm = Games.battleFromState(state);
        for (Map.Entry<Material, List<Command>> entry : bm.getBlockCommands().entrySet()) {
            var m = entry.getKey();
            if (b.getType() == m) {
                e.setCancelled(true);
                for (Command cmd : entry.getValue()) {
                    cmd.execute(SkyBattle.get(), p, Map.of("%player%", p.getName()));
                }
                break;
            }
        }

    }

}
