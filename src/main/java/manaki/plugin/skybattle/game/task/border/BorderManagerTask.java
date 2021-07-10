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
        var mm = Games.mapFromState(state);
        var currentBM = mm.getBorder(bs.getBorderId());
        var nextBorder = Games.getNextBorder(state);
        if (nextBorder == null) {
            this.selfDestroy();
            return;
        }
        var startR = currentBM.getRadius();
        var currentR = bs.getCurrentRadius();
        var targetR = nextBorder.getRadius();

        var bStartTime = state.getStartTime() + currentBM.getTime() * 1000L;
        var bEndTime = state.getStartTime() + nextBorder.getTime() * 1000L;

        var r = Utils.calR(startR, targetR, bStartTime, bEndTime);
        if (r != currentR) {
            bs.setCurrentRadius(r);
        }

        // Packet
        for (Player p : this.getState().getPlayers()) {
            if (r != currentR) p.sendMessage("§6Vòng bo đã thu hẹp lại " + (currentR - r) + " đơn vị (Hiện tại: " + r + ")");
            Utils.sendBorder(p, bs.getCenter().getBlockX(), bs.getCenter().getBlockZ(), bs.getCurrentRadius());
        }
    }

}
