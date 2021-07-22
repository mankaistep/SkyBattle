package manaki.plugin.skybattle.game.type;

import manaki.plugin.skybattle.team.BattleTeam;

import java.util.List;

public enum GameType {

    V1(1, "Solo (1v1)"),
    V2(2, "Team (2v2)"),
    V3(3, "Team (3v3)");

    private final int playersInTeam;
    private final String name;

    GameType(int playersInTeam, String name) {
        this.playersInTeam = playersInTeam;
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public int getPlayersInTeam() {
        return playersInTeam;
    }

    public static GameType parse(int maxPlayersInTeam) {
        for (GameType gt : values()) {
            if (gt.getPlayersInTeam() == maxPlayersInTeam) return gt;
        }
        return null;
    }

    public static GameType parse(List<BattleTeam> list) {
        int max = 0;
        for (BattleTeam bm : list) {
            max = Math.max(bm.getPlayers().size(), max);
        }

        return parse(max);
    }

}
