package manaki.plugin.skybattle.game.state;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import manaki.plugin.skybattle.game.task.a.ATask;
import manaki.plugin.skybattle.game.task.border.BorderManagerTask;
import manaki.plugin.skybattle.game.task.boss.BossManagerTask;
import manaki.plugin.skybattle.game.task.game.GameManagerTask;
import manaki.plugin.skybattle.game.task.mob.MobManagerTask;
import manaki.plugin.skybattle.game.task.supply.SupplyManagerTask;
import manaki.plugin.skybattle.game.util.Games;
import manaki.plugin.skybattle.team.Team;
import manaki.plugin.skybattle.world.WorldState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;

public class GameState {

    // Start information
    private final int id;
    private final String battleId;
    private final long startTime;
    private final List<Team> startTeams;

    // Task
    private final List<ATask> tasks;

    // Current
    private final List<Team> currentTeams;

    // Data
    private LivingEntity boss;

    // Other state
    private BorderState borderState;
    private final WorldState worldState;
    private final List<SupplyState> supplyStates;
    private final Map<String, PlayerState> playerStates;

    public GameState(int id, String battleId, List<Team> teams, WorldState ws) {
        this.id = id;
        this.battleId = battleId;
        this.startTime = System.currentTimeMillis();
        this.startTeams = List.copyOf(teams);
        this.currentTeams = List.copyOf(teams);
        this.worldState = ws;
        this.supplyStates = Lists.newArrayList();

        // Players
        playerStates = Maps.newConcurrentMap();
        for (Player p : this.getPlayers()) {
            playerStates.put(p.getName(), new PlayerState(p, false));
        }

        // Tasks
        tasks = Lists.newArrayList();
        tasks.add(new BorderManagerTask(this).start());
        tasks.add(new BossManagerTask(this).start());
        tasks.add(new GameManagerTask(this).start());
        tasks.add(new MobManagerTask(this).start());
        tasks.add(new SupplyManagerTask(this).start());
    }

    /*
     Methods
     */

    public int getId() {
        return id;
    }

    public String getBattleId() {
        return battleId;
    }

    public long getStartTime() {
        return startTime;
    }

    public List<Team> getStartTeams() {
        return startTeams;
    }

    public List<Team> getCurrentTeams() {
        return currentTeams;
    }

    public List<Player> getPlayers() {
        List<Player> players = Lists.newArrayList();
        for (Team team : this.currentTeams) {
            for (String pn : team.getPlayers()) {
                var p = Bukkit.getPlayer(pn);
                if (p == null) continue;
                players.add(p);
            }
        }
        return players;
    }

    public LivingEntity getBoss() {
        return boss;
    }

    public void setBoss(LivingEntity boss) {
        this.boss = boss;
    }

    public WorldState getWorldState() {
        return worldState;
    }

    public List<ATask> getTasks() {
        return tasks;
    }

    public void addTask(ATask task) {
        this.tasks.add(task);
    }

    public BorderState getBorderState() {
        return borderState;
    }

    public void setBorderState(BorderState borderState) {
        this.borderState = borderState;
    }

    public void removeTask(ATask task) {
        this.tasks.remove(task);
    }

    public List<SupplyState> getSupplyStates() {
        return supplyStates;
    }

    public void addSupply(SupplyState supplyState) {
        this.supplyStates.add(supplyState);
    }

    public void removeSupply(Location l) {
        this.supplyStates.removeIf(ss -> ss.getLocation().distanceSquared(l) < 0.5 * 0.5);
    }

    public int getTime() {
        return Long.valueOf((System.currentTimeMillis() - this.startTime) / 1000).intValue();
    }

    public boolean isLastBorder() {
        return Games.getNextBorder(this) == null;
    }

    public Map<String, PlayerState> getPlayerStates() {
        return playerStates;
    }

    public PlayerState getPlayerState(String name) {
        return this.playerStates.getOrDefault(name, null);
    }

    public Team getTeam(Player player) {
        for (Team team : this.getCurrentTeams()) {
            if (team.getPlayers().contains(player.getName())) return team;
        }
        return null;
    }

    public int getTeamAlive() {
        int c = 0;
        for (Team team : this.getCurrentTeams()) {
            if (Games.isTeamAlive(this, team)) c++;
        }
        return c;
    }

    public boolean canFinish() {
        return getTeamAlive() == 1 && this.boss != null && this.boss.isDead();
    }

    public Team getWinTeam() {
        for (Team team : this.getCurrentTeams()) {
            if (Games.isTeamAlive(this, team)) return team;
        }
        return null;
    }

}
