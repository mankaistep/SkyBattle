package manaki.plugin.skybattle.connect.team;

import manaki.plugin.skybattle.connect.team.player.TeamPlayer;

import java.util.List;

public class Team {

    private final List<TeamPlayer> players;

    public Team(List<TeamPlayer> players) {
        this.players = players;
    }

    public List<TeamPlayer> getPlayers() {
        return players;
    }
}
