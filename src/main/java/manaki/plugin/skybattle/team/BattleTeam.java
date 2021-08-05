package manaki.plugin.skybattle.team;

import com.google.common.collect.Lists;
import manaki.plugin.skybattle.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

import java.util.List;

public class BattleTeam {

    private ChatColor color;
    private final List<String> players;

    private int top;
    private int kill;
    private boolean killBoss;

    public BattleTeam(ChatColor color, List<String> list) {
        this.color = color;
        this.players = list;
        this.kill = 0;
        this.top = 8;
        this.killBoss = false;
    }

    public BattleTeam(List<String> list) {
        this.color = null;
        this.players = list;
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public List<String> getPlayers() {
        return players;
    }

    public List<Player> getOnlinePlayers() {
        List<Player> list = Lists.newArrayList();
        for (String pn : this.players) {
            var p = Bukkit.getPlayer(pn);
            if (p != null) list.add(p);
        }
        return list;
    }

    public void add(String player) {
        players.add(player);
    }

    public void remove(String player) {
        players.remove(player);
    }

    public BattleTeam cloneTeam() {
        return new BattleTeam(this.color, Lists.newArrayList(this.players));
    }

    public String getColorChat() {
        return ChatColor.valueOf(this.color.name()).toString();
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public int getKill() {
        return kill;
    }

    public void setKill(int kill) {
        this.kill = kill;
    }

    public void addKill(int kill) {
        this.kill += kill;
    }

    public int calScore() {
        return Utils.calScore(this.top) + Utils.getKillScore() * this.kill + (this.killBoss ? Utils.getBossKillScore() : 0);
    }

    public boolean isKillBoss() {
        return killBoss;
    }

    public void setKillBoss(boolean killBoss) {
        this.killBoss = killBoss;
    }
}
