package manaki.plugin.skybattle.config.model;

import java.util.List;

public class BorderModel {

    private final int id;
    private final List<String> centers;
    private final int radius;
    private final int time;

    public BorderModel(int id, List<String> center, int radius, int time) {
        this.id = id;
        this.centers = center;
        this.radius = radius;
        this.time = time;
    }

    public int getId() {
        return id;
    }

    public List<String> getCenters() {
        return centers;
    }

    public int getRadius() {
        return Math.max(radius, 0);
    }

    public int getTime() {
        return time;
    }
}
