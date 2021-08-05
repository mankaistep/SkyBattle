package manaki.plugin.skybattle.game.manager;

import com.google.common.collect.Lists;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.connect.listener.ConnectListener;
import manaki.plugin.skybattle.game.state.GameState;
import manaki.plugin.skybattle.game.task.a.ATask;
import manaki.plugin.skybattle.game.Games;
import manaki.plugin.skybattle.game.task.game.GameManagerTask;
import manaki.plugin.skybattle.spectator.Spectators;
import manaki.plugin.skybattle.team.BattleTeam;
import manaki.plugin.skybattle.util.Tasks;
import manaki.plugin.skybattle.util.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class GameManager {

    private final int WIN_WAITING_TIME = 30000;

    private final GameState state;

    public GameManager(GameState state) {
        this.state = state;
    }

    public GameState getState() {
        return state;
    }

    public void doPlayerOut(Player player, boolean canSpec) {
        // Clear data
        ConnectListener.clearData(player);

        // Set state
        state.removeBossbar(player.getName());
        var ps = state.getPlayerState(player.getName());
        ps.setDead(true);

        // Check player's team
        var team = state.getTeam(player.getName());

        // Team alive
        if (Games.isTeamAlive(state, team)) {
            if (canSpec) {
                // Spec
                Tasks.sync(() -> {
                    var teammate = Games.getTeammate(state, player);
                    Spectators.setSpectator(player, teammate);
                    Tasks.async(() -> {
                        player.sendTitle("§c§lBẠN ĐÃ CHẾT", "§fChỉ có thể theo dõi đồng đội", 10, 100, 10);
                        player.sendMessage("§cGhi §f/thoat §cđể thoát trận");
                    }, 25);
                }, 5);
            }
        }

        // Team not alive
        else {
            int top = team.getTop();

            // Kick
            sendTeamResult(team, true);

            // Broadcast
            this.teamEndBroadcast(team, top);

            // Change other teams top
            for (BattleTeam bt : state.getAliveTeams()) {
                int teamtop = state.getTeamAlive();
                bt.setTop(teamtop);
            }

            // One team left (winner)
            if (state.getTeamAlive() == 1) {
                var winTeam = state.getWinTeam();
                sendTeamResult(winTeam, false);

                // Noti
                for (Player p : winTeam.getOnlinePlayers()) {
                    p.sendTitle("§e§lTOP #" + state.getTeamAlive(), "§fChiến thắng", 10, 100, 10);
                    p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
                    p.sendMessage("§f§lĐợi §c§l5 giây§f§l rồi thoát!");
                }
            }

        }

        // Check end
        if (state.canFinish()) finish(false);
    }

    public void finish(boolean instantly) {
        // Istant stop
        if (instantly) {
            for (var p : state.getPlayers()) {
                Utils.toSpawn(p);
            }
            this.clean(true);
            return;
        }

        // Set end
        state.setEnded(true);

        // Check top 1 & send
        BattleTeam team = state.getWinTeam();

        // Broadcast
        this.endGameBroadcast();

        // Cancel tasks
        for (ATask task : Lists.newArrayList(state.getTasks())) {
            task.selfDestroy();
        }

        // Cancel bossbars
        for (BossBar bb : state.getBossbars()) {
            bb.removeAll();
        }

        // Quit task
        var start = System.currentTimeMillis();
        new BukkitRunnable() {
            @Override
            public void run() {
                try {
                    // Notification
                    var remain = WIN_WAITING_TIME - (System.currentTimeMillis() - start);
                    var seconds = remain / 1000;

                    if (team != null) {
                        for (Player p : team.getOnlinePlayers()) {
                            p.sendActionBar(new TextComponent("§a§lTự động rời sau §c§l" + seconds + " giây"));
                        }
                    }

                    // Check
                    if (remain <= 0) {
                        // Cancel
                        this.cancel();

                        // Back to main server
                        Tasks.sync(() -> {
                            if (team == null) return;
                            for (Player p : team.getOnlinePlayers()) {
                                Games.backToMainServer(p);
                            }
                        });

                        // Do the finish
                        Tasks.sync(() -> {
                            clean(false);
                        }, 200);
                    }
                }
                catch (Exception e) {
                    e.printStackTrace();
                    this.cancel();

                    // Do the finish
                    Tasks.sync(() -> {
                        clean(false);
                    }, 200);
                }
            }
        }.runTaskTimerAsynchronously(SkyBattle.get(), 0, 5);
    }

    // Method after game finished
    public void clean(boolean instantly) {
        // Remove manager
        Games.removeManager(this);

        if (!instantly) Tasks.async(() -> {
            // Unload world
            SkyBattle.get().getWorldLoader().unload(state.getWorldState().toWorldName(), true);
        }, 20);
        else SkyBattle.get().getWorldLoader().unload(state.getWorldState().toWorldName(), false);
    }

    public void teamEndBroadcast(BattleTeam team, int top) {
        for (Player p : state.getWorldState().toWorld().getPlayers()) {
            var s = "";
            for (String name : team.getPlayers()) {
                s += name + ",";
            }
            if (s.length() > 0) s = s.substring(0, s.length() - 1);
            p.sendMessage("§fĐội của §6" + s + "§f đạt §6§lTop #" + top + " §fvới số điểm là §6§l" + team.calScore() + "đ");
        }
    }

    public void endGameBroadcast() {
        for (Player p : state.getWorldState().toWorld().getPlayers()) {
            p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
            p.sendMessage("§7-----------------------------------------");
            p.sendMessage("         §e§lSKYBATTLE - KẾT THÚC");

            var result = sort(state);
            for (BattleTeam bt : result) {
                // Prefix
                var color = Utils.getTopColor(bt.getTop());
                var s = color + "§l#" + bt.getTop() + ". §r" + color;

                // Players
                for (String name : bt.getPlayers()) {
                    s += name + ",";
                }
                if (s.length() > 0) s = s.substring(0, s.length() - 1);

                // Score
                s += " (" + bt.calScore() + "đ) ";
                p.sendMessage("   " + s);
            }

            p.sendMessage("§7-----------------------------------------");
        }
    }

    public List<BattleTeam> sort(GameState state) {
        var list = state.getCurrentTeams();
        list.sort(new Comparator<BattleTeam>() {
            @Override
            public int compare(BattleTeam o1, BattleTeam o2) {
                return Integer.compare(o1.getTop(), o2.getTop());
            }
        });
        return list;
    }

    public void sendTeamResult(BattleTeam team, boolean noti) {
        int top = team.getTop();
        for (String pname : team.getPlayers()) {
            var p = Bukkit.getPlayer(pname);
            if (p != null && noti) {
                Tasks.sync(() -> p.sendTitle("§c§lTOP #" + top, "§fKết thúc", 10, 60, 10), 5);
                Tasks.sync(() -> {
                    Utils.toSpawn(p);
                }, 80);
            }

            // Send result
            var pr = state.getPlayerState(pname).getResult();
            pr.setTop(top);
            SkyBattle.get().getExecutor().sendResult(pr);
        }
    }
}
