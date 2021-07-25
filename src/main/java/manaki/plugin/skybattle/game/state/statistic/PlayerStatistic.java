package manaki.plugin.skybattle.game.state.statistic;

public class PlayerStatistic {

    private int kill;
    private int assist;

    public PlayerStatistic() {
        this.kill = 0;
        this.assist = 0;
    }
    public int getKill() {
        return kill;
    }

    public int getAssist() {
        return assist;
    }

    public void addKill(int value) {
        this.kill += value;
    }

    public void addAssist(int value) {
        this.assist += value;
    }

}
