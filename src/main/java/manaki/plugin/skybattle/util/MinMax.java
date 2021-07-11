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
        if (min == max) return min;
        return new Random().nextInt(max - min) + min;
    }

    @Override
    public String toString() {
        return this.min + "-" + max;
    }

    public static MinMax parse(String s) {
        var a = s.split("-");
        int min = Integer.parseInt(a[0]);
        int max = min;
        if (a.length > 1) max = Integer.parseInt(a[1]);
        return new MinMax(min, max);
    }

}
