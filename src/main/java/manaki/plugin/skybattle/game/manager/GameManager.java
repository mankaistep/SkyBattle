package manaki.plugin.skybattle.game.manager;

import com.google.common.collect.Lists;
import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.connect.listener.ConnectListener;
import manaki.plugin.skybattle.game.state.GameState;
import manaki.plugin.skybattle.game.task.a.ATask;
import manaki.plugin.skybattle.game.Games;
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

public class GameManager {

    private final int WIN_WAITING_TIME = 30000;

    private GameState state;

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
        if (ps != null) ps.setDead(true);

        // If has anyone left
        var team = state.getTeam(pname);
        if (team != null) {
            // Kick if not alive
            if (!Games.isTeamAlive(state, team)) {
                for (String pn : team.getPlayers()) {
                    if (pn.equals(pname)) continue;
                    var p = Bukkit.getPlayer(pn);
                    if (p == null) continue;

                    p.sendTitle("§c§lTOP #" + state.getTeamAlive(), "§fKết thúc", 10, 60, 10);

                    // Kick
                    Tasks.sync(() -> {
                        // Back to main server
                        Games.bypassInvalidCheck(p, 1000);
                        Games.backToMainServer(p);
                    }, 80);
                }
            }
        }
    }

    public void playerDead(Player player) {
        // Show tab
        player.setPlayerListName(player.getName());
        
        // Set state
        state.removeBossbar(player.getName());
        var ps = state.getPlayerState(player.getName());
        ps.setDead(true);

        Tasks.sync(() -> {
            player.spigot().respawn();
        });

        // Clear data
        Tasks.async(() -> {
            ConnectListener.clearData(player);
        });

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
                for (String pn : team.getPlayers()) {
                    var p = Bukkit.getPlayer(pn);

                    // Title
                    Tasks.sync(() -> {
                        p.sendTitle("§c§lTOP #" + (state.getTeamAlive() + 1), "§fKết thúc", 10, 60, 10);
                    }, 5);

                    // Kick
                    Tasks.sync(() -> {
                        // Back to spawn
                        Utils.toSpawn(p);
                    }, 80);
                }
            }
        }
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

        // Has winner
        if (battleTeam != null) {
            for (String pn : battleTeam.getPlayers()) {
                var p = Bukkit.getPlayer(pn);
                p.sendTitle("§e§lTOP #" + state.getTeamAlive(), "§fChiến thắng", 10, 60, 10);
                p.playSound(p.getLocation(), Sound.ENTITY_FIREWORK_ROCKET_LAUNCH, 1, 1);
            }
            var start = System.currentTimeMillis();
            new BukkitRunnable() {
                @Override
                public void run() {
                    // Notification
                    var remain = WIN_WAITING_TIME - (System.currentTimeMillis() - start);
                    var seconds = remain / 1000;
                    for (String pn : battleTeam.getPlayers()) {
                        var p = Bukkit.getPlayer(pn);
                        p.sendActionBar(new TextComponent("§a§lTự động rời sau §c§l" + seconds + " giây"));
                    }

                    // Check
                    if (remain <= 0) {
                        this.cancel();

                        // Back to main server
                        Tasks.sync(() -> {
                            for (String pn : battleTeam.getPlayers()) {
                                var p = Bukkit.getPlayer(pn);
                                Games.backToMainServer(p);
                            }

                        });

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

}
