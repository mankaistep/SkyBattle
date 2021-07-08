package manaki.plugin.skybattle.area;

import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.game.util.Games;
import org.bukkit.Location;

import java.util.Map;

public class Areas {

    public static AreaType check(Location l) {
        var state = Games.fromWorld(l.getWorld());
        if (state == null) return null;

        var bm = SkyBattle.get().getMainConfig().getBattleModel(state.getBattleId());
        var mm = SkyBattle.get().getMainConfig().getMapModel(bm.getMapId());

        int mprt = 0;
        AreaType area = AreaType.EDGE;
        for (Map.Entry<AreaType, Double> e : mm.getAreaRadius().entrySet()) {
            var at = e.getKey();
            var rm = e.getValue();
            if (at.getPriority() > mprt) {
                var center = mm.getLocation(mm.getCenterLocation());
                var r = l.distance(center.toLocation(l.getWorld()));
                if (r <= rm) {
                    mprt = at.getPriority();
                    area = at;
                }
            }
        }

        return area;
    }

}
