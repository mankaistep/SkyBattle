package manaki.plugin.skybattle.game.state;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import manaki.plugin.skybattle.game.task.a.ATask;
import manaki.plugin.skybattle.game.task.border.BorderManagerTask;
import manaki.plugin.skybattle.game.task.boss.BossManagerTask;
import manaki.plugin.skybattle.game.task.game.GameManagerTask;
import manaki.plugin.skybattle.game.task.mob.MobManagerTask;
import manaki.plugin.skybattle.game.task.supply.SupplyManagerTask;
import manaki.plugin.skybattle.game.Games;
import manaki.plugin.skybattle.game.type.GameType;
import manaki.plugin.skybattle.team.BattleTeam;
import manaki.plugin.skybattle.util.Utils;
import manaki.plugin.skybattle.world.WorldState;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.boss.BossBar;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GameState {

    // Start information
    private final int id;
    private final String battleId;
    private final long startTime;
    private final List<BattleTeam> startBattleTeams;
    private final GameType type;

    // Task
    private List<ATask> tasks;

    // Current
    private final List<BattleTeam> currentBattleTeams;

    // Data
    private boolean isLoading;
    private final List<BossBar> bossbars;
    private LivingEntity boss;
    private final List<Location> blockPlaced;
    private final List<Location> supplySpawned;

    // Other state
    private BorderState borderState;
    private final WorldState worldState;
    private final List<SupplyState> supplyStates;
    private final Map<String, PlayerState> playerStates;

    // End
    private boolean isEnded;
    private boolean isRanked;

    public GameState(int id, String battleId, List<BattleTeam> battleTeams, WorldState ws, boolean isRanked) {
        this.id = id;
        this.battleId = battleId;
        this.startTime = System.currentTimeMillis();
        this.startBattleTeams = battleTeams.stream().map(BattleTeam::cloneTeam).collect(Collectors.toList());
        this.currentBattleTeams = Lists.newArrayList(battleTeams);
        this.worldState = ws;
        this.supplyStates = Lists.newArrayList();
        this.bossbars = Lists.newArrayList();
        this.blockPlaced = Lists.newArrayList();
        this.supplySpawned = Lists.newArrayList();
        this.isEnded = false;
        this.isLoading = false;
        this.type = GameType.parse(this.startBattleTeams);
        this.isRanked = isRanked;

        // Players
        playerStates = Maps.newConcurrentMap();
        for (Player p : this.getPlayers()) {
            var ps = new PlayerState(this, p, false);
            playerStates.put(p.getName(), ps);
        }


    }

    /*
     Methods
     */

    public void startTasks() {
        tasks = Lists.newArrayList();
        tasks.add(new BorderManagerTask(this).start());
        tasks.add(new BossManagerTask(this).start());
        tasks.add(new GameManagerTask(this).start());
        tasks.add(new MobManagerTask(this).start());
        tasks.add(new SupplyManagerTask(this).start());
    }

    public boolean isLoading() {
        return isLoading;
    }

    public void setLoading(boolean loading) {
        isLoading = loading;
    }

    public GameType getType() {
        return type;
    }

    public int getId() {
        return id;
    }

    public String getBattleId() {
        return battleId;
    }

    public long getStartTime() {
        return startTime;
    }

    public List<BattleTeam> getStartTeams() {
        return startBattleTeams;
    }

    public List<BattleTeam> getCurrentTeams() {
        return currentBattleTeams;
    }

    public List<Player> getPlayers() {
        List<Player> players = Lists.newArrayList();
        for (BattleTeam battleTeam : this.currentBattleTeams) {
            for (String pn : battleTeam.getPlayers()) {
                var p = Bukkit.getPlayer(pn);
                if (p == null) continue;
                players.add(p);
            }
        }
        return players;
    }

    public List<Player> getAlivePlayers() {
        List<Player> players = Lists.newArrayList();
        for (BattleTeam battleTeam : this.currentBattleTeams) {
            for (String pn : battleTeam.getPlayers()) {
                var p = Bukkit.getPlayer(pn);
                if (p == null) continue;
                if (getPlayerState(p.getName()).isDead()) continue;
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

    public List<Location> getBlockPlaced() {
        return blockPlaced;
    }

    public void addBlockPlaced(Block b) {
        this.blockPlaced.add(b.getLocation());
    }

    public void removeBlockPlaced(Block b) {
        this.blockPlaced.removeIf(l -> Utils.isSameBlock(l, b.getLocation()));
    }

    public boolean isBlockPlaced(Block b) {
        for (Location l : this.blockPlaced) {
            if (Utils.isSameBlock(l, b.getLocation())) return true;
        }
        return false;
    }

    public List<SupplyState> getSupplyStates() {
        return supplyStates;
    }

    public void addSupply(SupplyState supplyState) {
        this.supplyStates.add(supplyState);
        this.supplySpawned.add(supplyState.getLocation());
    }

    public void removeSupply(Location l) {
        this.supplyStates.removeIf(ss -> ss.getLocation().distanceSquared(l) < 0.5 * 0.5);
    }

    public boolean isSupplySpawned(Location l) {
        for (Location ls : this.supplySpawned) {
            if (Utils.isSameBlock(ls, l)) return true;
        }
        return false;
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
        return this.playerStates.getOrDefault(name, new PlayerState(name, this, true));
    }

    public boolean isRanked() {
        return isRanked;
    }

    public BattleTeam getTeam(Player player) {
        for (BattleTeam battleTeam : this.getCurrentTeams()) {
            if (battleTeam.getPlayers().contains(player.getName())) return battleTeam;
        }
        return null;
    }

    public BattleTeam getTeam(String player) {
        for (BattleTeam battleTeam : this.getCurrentTeams()) {
            if (battleTeam.getPlayers().contains(player)) return battleTeam;
        }
        return null;
    }

    public BattleTeam getStartTeam(String player) {
        for (BattleTeam t : this.getStartTeams()) {
            if (t.getPlayers().contains(player)) return t;
        }
        return null;
    }

    public int getTeamAlive() {
        int c = 0;
        for (BattleTeam battleTeam : this.getCurrentTeams()) {
            if (Games.isTeamAlive(this, battleTeam)) c++;
        }
        return c;
    }

    public boolean canFinish() {
        return getTeamAlive() == 1;
    }

    public BattleTeam getWinTeam() {
        for (BattleTeam battleTeam : this.getCurrentTeams()) {
            if (Games.isTeamAlive(this, battleTeam)) return battleTeam;
        }
        return null;
    }

    public List<BossBar> getBossbars() {
        return bossbars;
    }

    public void addBossbar(BossBar bb) {
        this.bossbars.add(bb);
    }

    public void removeBossbar(BossBar bb) {
        this.bossbars.remove(bb);
    }

    public void removeBossbar(String playerName) {
        var p = Bukkit.getPlayer(playerName);
        if (p == null) return;
        for (BossBar bb : this.bossbars) {
            bb.removePlayer(p);
        }
    }

    public boolean isEnded() {
        return isEnded;
    }

    public void setEnded(boolean ended) {
        isEnded = ended;
    }

    public List<BattleTeam> getAliveTeams() {
        return this.currentBattleTeams.stream().filter(bt -> Games.isTeamAlive(this, bt)).collect(Collectors.toList());
    }
}
