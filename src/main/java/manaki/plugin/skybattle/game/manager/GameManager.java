package manaki.plugin.skybattle.game.manager;

import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.game.state.GameState;
import manaki.plugin.skybattle.game.task.a.ATask;
import manaki.plugin.skybattle.game.util.Games;
import manaki.plugin.skybattle.team.Team;
import manaki.plugin.skybattle.util.Tasks;
import manaki.plugin.skybattle.util.Utils;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.GameMode;
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
        // Set state
        var ps = state.getPlayerState(player.getName());
        ps.setDead(true);

        // If has anyone left
        var team = state.getTeam(player);
        if (team != null) {
            // Kick if not alive
            if (!Games.isTeamAlive(state, team)) {
                for (String pn : team.getPlayers()) {
                    var p = Bukkit.getPlayer(pn);
                    p.sendTitle("§c§lTOP #" + state.getTeamAlive(), "§fKết thúc", 10, 60, 10);

                    // Kick
                    Tasks.sync(() -> {
                        // Back to main server
                        Utils.toSpawn(p);
                    }, 80);
                }
            }
        }
    }

    public void playerDead(Player player) {
        // Set state
        var ps = state.getPlayerState(player.getName());
        ps.setDead(true);

        // If has anyone left
        var team = state.getTeam(player);
        if (team != null) {
            boolean teamAlive = false;
            for (String pn : team.getPlayers()) {
                if (!state.getPlayerState(pn).isDead()) {
                    Tasks.sync(() -> {
                        player.spigot().respawn();
                        player.setGameMode(GameMode.SPECTATOR);
                        player.setSpectatorTarget(Bukkit.getPlayer(pn));
                        player.sendTitle("§c§lBẠN ĐÃ CHẾT", "§fChỉ có thể theo dõi đồng đội", 10, 100, 10);
                        player.sendMessage("§cGhi §f/thoat &cđể thoát trận");
                    }, 5);
                    teamAlive = true;
                    break;
                }
            }

            // Kick if not alive
            if (!teamAlive) {
                for (String pn : team.getPlayers()) {
                    var p = Bukkit.getPlayer(pn);
                    p.sendTitle("§c§lTOP #" + (state.getTeamAlive() + 1), "§fKết thúc", 10, 60, 10);

                    // Kick
                    Tasks.sync(() -> {
                        // Back to main server
                        Utils.toSpawn(p);
                    }, 80);
                }
            }
        }
    }

    public void finish(boolean instantly) {
        if (instantly) {
            this.clean();
            return;
        }

        // Winner
        Team team = state.getWinTeam();

        // Has winner
        if (team != null) {
            for (String pn : team.getPlayers()) {
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
                    for (String pn : team.getPlayers()) {
                        var p = Bukkit.getPlayer(pn);
                        p.sendActionBar(new TextComponent("§a§lTự động rời sau §c§l" + seconds + " giây"));
                    }

                    // Check
                    if (remain <= 0) {
                        this.cancel();

                        // Back to main server
                        Tasks.sync(() -> {
                            for (String pn : team.getPlayers()) {
                                var p = Bukkit.getPlayer(pn);
                                Utils.toSpawn(p);
                            }

                        });

                        // Do the finish
                        Tasks.sync(() -> {
                            clean();
                        }, 200);
                    }
                }
            }.runTaskTimerAsynchronously(SkyBattle.get(), 0, 5);
        }
        else {
            // Do the finish
            Tasks.sync(this::clean, 200);
        }
    }

    // Method after game finished
    public void clean() {
        // Cancel tasks
        for (ATask task : state.getTasks()) {
            if (Bukkit.getScheduler().isCurrentlyRunning(task.getTaskId())) task.cancel();
        }

        // Cancel bossbars
        for (BossBar bb : state.getBossbars()) {
            bb.removeAll();
        }

        // Unload world
        SkyBattle.get().getWorldLoader().unload(state.getWorldState().toWorldName(), false);

        // Remove manager
        Games.removeManager(this);
    }

}
