package manaki.plugin.skybattle.game.manager;

import com.google.common.collect.Lists;
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

    public void playerQuit(Player player) {
        // Clear data
        ConnectListener.clearData(player);

        // Call quit
        playerQuit(player.getName());
    }

    public void playerQuit(String pname) {
        // Set state
        state.removeBossbar(pname);
        var ps = state.getPlayerState(pname);
        if (ps != null) {
            ps.setDead(true);
        }

        // Top broadcast
        for (Player p : state.getWorldState().toWorld().getPlayers()) {
            p.sendMessage("§6" + pname + "§f đạt §6§lTop #" + ps.getResult().getTop());
        }

        // If has anyone left
        var team = state.getTeam(pname);
        if (team != null) {
            // Kick if not alive
            if (!Games.isTeamAlive(state, team)) {
                // Set top
                int top = state.getTeamAlive() + 1;
                team.setTop(top);

                // Set score
                team.addScore(Utils.calScore(top));

                for (String pn : team.getPlayers()) {
                    if (pn.equals(pname)) continue;

                    // Send title
                    var p = Bukkit.getPlayer(pn);
                    if (p == null) continue;

                    p.sendTitle("§c§lTOP #" + top, "§fKết thúc", 10, 60, 10);

                    // Kick
                    Tasks.sync(() -> {
                        // Back to main server
                        Games.bypassInvalidCheck(p, 1000);
                        Games.backToMainServer(p);
                    }, 80);
                }


                // Team lose broadcast
                teamEndBroadcast(team, top);
            }
        }

        // Update all other players
        GameManagerTask.saveTop(state);
    }

    public void playerDead(Player player) {
        // Show tab
        player.setPlayerListName(player.getName());
        
        // Set state
        state.removeBossbar(player.getName());
        var ps = state.getPlayerState(player.getName());
        ps.setDead(true);

        Tasks.sync(() -> player.spigot().respawn());

        // Clear data
        Tasks.async(() -> ConnectListener.clearData(player));

        // Top broadcast
        for (Player p : player.getWorld().getPlayers()) {
            p.sendMessage("§6" + player.getName() + "§f đạt §6§lTop #" + ps.getResult().getTop());
        }

        // If has anyone left
        var team = state.getTeam(player);
        if (team != null) {
            boolean teamAlive = false;
            for (String pn : team.getPlayers()) {
                if (!state.getPlayerState(pn).isDead()) {
                    Games.bypassInvalidCheck(player, 1000);
                    Tasks.sync(() -> {
                        var teammate = Bukkit.getPlayer(pn);

                        // Spectator
                        Spectators.setSpectator(player, teammate);

                        // Message
                        player.sendTitle("§c§lBẠN ĐÃ CHẾT", "§fChỉ có thể theo dõi đồng đội", 10, 100, 10);
                        player.sendMessage("§cGhi §f/thoat §cđể thoát trận");
                    }, 5);
                    teamAlive = true;
                    break;
                }
            }

            // Kick if not alive
            if (!teamAlive) {
                int top = state.getTeamAlive() + 1;

                // Set score
                team.addScore(Utils.calScore(top));
                team.setTop(top);

                for (Player p : team.getOnlinePlayers()) {
                    // Title
                    Tasks.sync(() -> p.sendTitle("§c§lTOP #" + top, "§fKết thúc", 10, 60, 10), 5);
                    
                    // Kick
                    Tasks.sync(() -> {
                        // Back to spawn
                        Utils.toSpawn(p);
                    }, 80);
                }

                // Team lose broadcast
                teamEndBroadcast(team, top);
            }
        }

        // Update all other players
        GameManagerTask.saveTop(state);
    }

    public void finish(boolean instantly) {
        if (instantly) {
            for (var p : state.getPlayers()) {
                Utils.toSpawn(p);
            }
            this.clean(true);
            return;
        }

        state.setEnded(true);


        // Winner
        BattleTeam battleTeam = state.getWinTeam();
        battleTeam.addScore(Utils.calScore(1));

        // Broadcast
        endGameBroadcast(state);

        // Has winner
        if (battleTeam != null) {
            // Noti
            for (Player p : battleTeam.getOnlinePlayers()) {
                p.sendTitle("§e§lTOP #" + state.getTeamAlive(), "§fChiến thắng", 10, 60, 10);
                p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);

                var ps = state.getPlayerState(p.getName());
                ps.getResult().setWinner(true);
                SkyBattle.get().getExecutor().sendResult(ps.getResult());

                p.sendMessage("§c§lĐừng thoát vội, đợi tầm 15 giây rồi /thoat, thoát nhanh có nguy cơ mất điểm!");
            }
            var start = System.currentTimeMillis();
            new BukkitRunnable() {
                @Override
                public void run() {
                    try {
                        // Notification
                        var remain = WIN_WAITING_TIME - (System.currentTimeMillis() - start);
                        var seconds = remain / 1000;
                        for (Player p : battleTeam.getOnlinePlayers()) {
                            p.sendActionBar(new TextComponent("§a§lTự động rời sau §c§l" + seconds + " giây"));
                        }

                        // Check
                        if (remain <= 0) {
                            // Cancel
                            this.cancel();

                            // Back to main server
                            Tasks.sync(() -> {
                                for (Player p : battleTeam.getOnlinePlayers()) {
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
        else {
            // Do the finish
            Tasks.sync(() -> {
                this.clean(false);
            }, 100);
        }
    }

    // Method after game finished
    public void clean(boolean instantly) {
        // Cancel tasks
        for (ATask task : Lists.newArrayList(state.getTasks())) {
            task.selfDestroy();
        }

        // Cancel bossbars
        for (BossBar bb : state.getBossbars()) {
            bb.removeAll();
        }

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
            p.sendMessage("§fĐội của §6" + s + "§f đạt §6§lTop #" + top + " §fvới số điểm là §6§l" + team.getScore() + "đ");
        }
    }

    public void endGameBroadcast(GameState state) {
        for (Player p : state.getWorldState().toWorld().getPlayers()) {
            p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
            p.sendMessage("§7-----------------------------------------");
            p.sendMessage("        §e§lSKYBATTLE - KẾT THÚC");

            var result = sort(state);
            for (BattleTeam bt : result) {
                // Prefix
                var color = Utils.getTopColor(bt.getTop());
                var s = color + "§l" + bt.getTop() + ". §r" + color;

                // Players
                for (String name : bt.getPlayers()) {
                    s += name + ",";
                }
                if (s.length() > 0) s = s.substring(0, s.length() - 1);

                // Score
                s += " (" + bt.getScore() + "đ) ";
                p.sendMessage("   " + s);
            }

            p.sendMessage("§7-----------------------------------------");
        }
    }

    public static List<BattleTeam> sort(GameState state) {
        var list = state.getCurrentTeams();
        list.sort(new Comparator<BattleTeam>() {
            @Override
            public int compare(BattleTeam o1, BattleTeam o2) {
                return Integer.compare(o1.getTop(), o2.getTop());
            }
        });
        return list;
    }
}
