package manaki.plugin.skybattle.game.type;

public enum GameType {

    SOLO(1, 8),
    DUO(2, 8),
    SQUAD(4, 6);

    private final int playersInTeam;
    private final int maxTeam;

    GameType(int playersInTeam, int maxTeam) {
        this.playersInTeam = playersInTeam;
        this.maxTeam = maxTeam;
    }

    public int getPlayersInTeam() {
        return playersInTeam;
    }

    public int getMaxTeam() {
        return maxTeam;
    }
}
