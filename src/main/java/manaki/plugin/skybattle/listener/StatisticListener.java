package manaki.plugin.skybattle.listener;

import manaki.plugin.skybattle.game.Games;
import manaki.plugin.skybattle.game.state.PlayerState;
import manaki.plugin.skybattle.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.bukkit.event.entity.PlayerDeathEvent;

import java.util.Map;

public class StatisticListener implements Listener {

    /*
    Add damaged players
     */
    @EventHandler
    public void onDamage(EntityDamageByEntityEvent e) {
        if (!(e.getDamager() instanceof Player) || !(e.getEntity() instanceof Player)) return;

        var damager = (Player) e.getDamager();
        var target = (Player) e.getEntity();

        var state = Games.getCurrentGame(damager);
        if (state == null) return;

        if (Games.isTeammate(damager, target)) return;

        var ps = state.getPlayerState(damager.getName());
        ps.addDamaged(target.getName());
    }
    /*
    Add kill and assist
     */
    @EventHandler
    public void onKill(PlayerDeathEvent e) {
        var p = e.getEntity();
        var state = Games.getCurrentGame(p);
        if (state == null) return;

        var killer = p.getKiller();

        // Check killer
        if (killer != null) {
            var ps = state.getPlayerState(killer.getName());
            ps.addKill(1);
            killer.sendTitle("", "§c§l⚔ Hạ gục ⚔", 0, 50, 0);
            killer.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_HURT, 1, 1);

            // Team score
            var team = state.getTeam(killer);
            team.addScore(Utils.getKillScore());
        }

        // Check assist
        for (Map.Entry<String, PlayerState> entry : state.getPlayerStates().entrySet()) {
            var name = entry.getKey();
            var ps = entry.getValue();

            var assistant = Bukkit.getPlayer(name);
            if (assistant == null) return;
            if (assistant == killer) return;
            if (ps.getDamaged().contains(p.getName())) {
                ps.addAssist(1);
                assistant.sendTitle("", "§6§l⚒ Hỗ trợ ⚒", 0, 50, 0);
                assistant.playSound(p.getLocation(), Sound.ENTITY_CHICKEN_EGG, 1, 1);
            }

        }
    }

}
