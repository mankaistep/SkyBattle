package manaki.plugin.skybattle.config.model;

import java.util.List;

public class BorderModel {

    private final String id;
    private final List<String> centers;
    private final int radius;
    private final int time;

    public BorderModel(String id, List<String> center, int radius, int time) {
        this.id = id;
        this.centers = center;
        this.radius = radius;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public List<String> getCenters() {
        return centers;
    }

    public int getRadius() {
        return radius;
    }

    public int getTime() {
        return time;
    }
}
