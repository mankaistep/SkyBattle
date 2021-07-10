package manaki.plugin.skybattle.game.task.border;

import manaki.plugin.skybattle.game.state.GameState;
import manaki.plugin.skybattle.game.task.a.ATask;
import manaki.plugin.skybattle.game.util.Games;
import manaki.plugin.skybattle.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;

public class BorderManagerTask extends ATask {

    private BossBar bossbar = null;

    public BorderManagerTask(GameState state) {
        super(state);
    }

    @Override
    public void run() {
        // Border check
        borderCheck();

        // Bossbar
        bossbarCheck();
    }

    public void bossbarCheck() {
        if (this.bossbar == null) {
            this.bossbar = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID);
            for (Player p : this.getState().getPlayers()) this.bossbar.addPlayer(p);
            this.getState().addBossbar(bossbar);
        }

        // Get
        var bs = this.getState().getBorderState();
        var r = bs.getCurrentRadius();
        int last = Games.getLastBorder(this.getState()).getTime();
        int time = this.getState().getTime();

        float progress = (1 - (float) time / last);
        if (progress <= 0) {
            bossbar.removeAll();
            this.getState().removeBossbar(bossbar);
            return;
        }

        this.bossbar.setProgress(progress);
        this.bossbar.setTitle("§a§lVòng bo thu hẹp: §c§l" + r);
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
        var targetR = nextBorder.getRadius();

        var bStartTime = state.getStartTime() + currentBM.getTime() * 1000L;
        var bEndTime = state.getStartTime() + nextBorder.getTime() * 1000L;

        var r = Utils.calR(startR, targetR, bStartTime, bEndTime);
        bs.setCurrentRadius(r);

        // Packet
        var battle = Games.battleFromState(state);
        Utils.setBorder(state.getWorldState().toWorld(), bs.getCenter().getBlockX(), bs.getCenter().getBlockZ(), bs.getCurrentRadius(), battle.getSetting().get("border-damage", Integer.class));
    }

}
