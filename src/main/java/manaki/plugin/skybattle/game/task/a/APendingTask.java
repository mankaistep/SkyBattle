package manaki.plugin.skybattle.game.task.a;

import com.google.common.collect.Sets;
import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.game.state.GameState;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BarColor;
import org.bukkit.boss.BarStyle;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Set;

public abstract class APendingTask extends ATask {

    private final String name;
    private final long start;
    private final long period;
    private final String message;

    public APendingTask(GameState state, String name, long period, String message) {
        super(state);
        this.name = name;
        this.start = System.currentTimeMillis();
        this.period = period;
        this.message = message;
    }

    public String getName() {
        return name;
    }

    public long getStart() {
        return start;
    }

    public long getPeriod() {
        return period;
    }

    public String getMessage() {
        return message;
    }

    public abstract Runnable getStarter();


    /*
    Run
     */
    private BossBar bossbar = null;
    private Set<Long> checkedSeconds = null;

    @Override
    public void run() {
        try {
            // Create boss bar
            if (this.bossbar == null) {
                this.checkedSeconds = Sets.newHashSet();
                this.bossbar = Bukkit.createBossBar("", BarColor.GREEN, BarStyle.SOLID);
                for (Player p : this.getState().getPlayers()) this.bossbar.addPlayer(p);
            }

            // Set bossbar
            long remain = period - (System.currentTimeMillis() - start);
            long secondRemain = remain / 1000;
            bossbar.setTitle(this.message + ": §e§l" + secondRemain + "s");
            bossbar.setProgress(Math.max(0, Math.min(1, (System.currentTimeMillis() - start) / period)));

            // Sound
            if (!checkedSeconds.contains(secondRemain)) {
                checkedSeconds.add(secondRemain);
                if (secondRemain <= 3) {
                    for (Player p : this.getState().getPlayers()) p.playSound(p.getLocation(), Sound.UI_BUTTON_CLICK, 1, 1);
                }
            }

            // Check time
            if (remain <= 0) {
                bossbar.removeAll();
                this.getStarter().run();
                this.selfDestroy();
            }
        }
        catch (Exception e) {
            for (Player p : this.getState().getPlayers()) p.sendMessage("§c Có lỗi, vui lòng báo lại với admin (cụ thể thời điểm)");
            SkyBattle.get().getLogger().severe("Catch an exception in a pending task: " + this.getName());
            this.selfDestroy();
            e.printStackTrace();
        }

    }

}
