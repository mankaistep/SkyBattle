package manaki.plugin.skybattle.game.state.result;

import com.google.gson.GsonBuilder;
import manaki.plugin.skybattle.game.state.statistic.PlayerStatistic;

public class PlayerResult {

    private final String name;
    private final int type;
    private final boolean isRanked;
    private int top;
    private final PlayerStatistic statistic;
    private boolean isWinner;

    public PlayerResult(String name, int type, boolean isRanked) {
        this.name = name;
        this.type = type;
        this.isRanked = isRanked;
        this.top = 8;
        this.statistic = new PlayerStatistic();
        this.isWinner = false;
    }

    public String getName() {
        return name;
    }

    public int getType() {
        return type;
    }

    public boolean isRanked() {
        return isRanked;
    }

    public int getTop() {
        return top;
    }

    public void setTop(int top) {
        this.top = top;
    }

    public void setWinner(boolean winner) {
        isWinner = winner;
    }

    public PlayerStatistic getStatistic() {
        return statistic;
    }

    public boolean isWinner() {
        return isWinner;
    }

    public String toString() {
        return new GsonBuilder().create().toJson(this);
    }


}
