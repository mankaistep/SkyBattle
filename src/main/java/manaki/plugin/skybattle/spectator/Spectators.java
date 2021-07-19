package manaki.plugin.skybattle.spectator;

import com.google.common.collect.Lists;
import manaki.plugin.skybattle.game.util.Games;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.World;
import org.bukkit.entity.Player;

import java.util.List;

public class Spectators {

    public static void setSpectator(Player p, Player target) {
        var state = Games.getCurrentGame(p);
        if (state == null) return;

        p.spigot().respawn();
        var mm = Games.mapFromState(state);

        if (target != null) {
            p.setGameMode(GameMode.SPECTATOR);
            p.setSpectatorTarget(target);
        }
        else {
            p.teleport(mm.getLocation(mm.getCenterLocation()).toLocation(state.getWorldState().toWorld()));
            p.setGameMode(GameMode.SPECTATOR);
        }
    }

    public static boolean isInSpectator(Player p) {
        return true;
    }

    public static List<Player> getSpectatorsInWorld(World w) {
        List<Player> list = Lists.newArrayList();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.getWorld() != w) continue;
            if (p.getGameMode() == GameMode.SPECTATOR || p.hasPermission("skybattle.admin")) list.add(p);
        }
        return list;
    }

}
