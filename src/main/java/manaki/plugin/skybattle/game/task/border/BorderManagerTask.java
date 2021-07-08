package manaki.plugin.skybattle.game.task.border;

import manaki.plugin.skybattle.game.state.GameState;
import manaki.plugin.skybattle.game.task.a.ATask;
import manaki.plugin.skybattle.game.util.Games;
import manaki.plugin.skybattle.util.Utils;
import org.bukkit.entity.Player;

public class BorderManagerTask extends ATask {

    public BorderManagerTask(GameState state) {
        super(state);
    }

    @Override
    public void run() {
        borderCheck();
    }

    public void borderCheck() {
        var state = this.getState();

        // Random border
        var bs = Games.randomizeBorder(state);
        this.getState().setBorderState(bs);

        // Check radius timing
        int oldR = bs.getCurrentRadius();
        var nextbdm = Games.getNextBorder(state);
        if (nextbdm != null) {
            var cbdm = Games.mapFromState(state).getBorders().get(bs.getBorderId());
            int dR = cbdm.getRadius() - nextbdm.getRadius();

            long timePassed = System.currentTimeMillis() - state.getStartTime();
            long period = (cbdm.getTime() - nextbdm.getTime()) * 1000L;

            int newR = Long.valueOf(cbdm.getRadius() - dR * timePassed / period).intValue();
            if (newR != oldR) {
                int sub = newR - oldR;
                Games.broadcast(state, "§6§l>> Bo đã được thu lại thêm " + sub + " đơn vị (" + newR + ")");
                bs.setCurrentRadius(newR);
            }
        }

        // Packet
        for (Player p : this.getState().getPlayers()) {
            Utils.sendBorder(p, bs.getCenter().getBlockX(), bs.getCenter().getBlockZ(), bs.getCurrentRadius());
        }
    }

}
