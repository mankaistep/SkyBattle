package manaki.plugin.skybattle.connect.request;

import com.google.gson.GsonBuilder;
import manaki.plugin.skybattle.connect.team.Team;


import java.util.List;

public class StartRequest {

    private final String battleId;
    private final List<Team> teams;
    private final String data;

    public StartRequest(String battleId, List<Team> teams, String data) {
        this.battleId = battleId;
        this.teams = teams;
        this.data = data;
    }

    public String getBattleId() {
        return battleId;
    }

    public List<Team> getTeams() {
        return teams;
    }

    public String getData() {
        return data;
    }

    public String toString() {
        return new GsonBuilder().create().toJson(this);
    }

}