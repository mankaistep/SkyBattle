package manaki.plugin.skybattle.util;

import java.util.Random;

public class MinMax {

    private final int min;
    private final int max;

    public MinMax(int min, int max) {
        this.min = min;
        this.max = max;
    }

    public int getMin() {
        return min;
    }

    public int getMax() {
        return max;
    }

    public int random() {
        return new Random().nextInt(max - min) + min;
    }

}
