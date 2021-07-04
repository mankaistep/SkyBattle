package manaki.plugin.skybattle.config.model;

public class BorderModel {

    private final String id;
    private final String center;
    private final int range;
    private final int time;

    public BorderModel(String id, String center, int range, int time) {
        this.id = id;
        this.center = center;
        this.range = range;
        this.time = time;
    }

    public String getId() {
        return id;
    }

    public String getCenter() {
        return center;
    }

    public int getRange() {
        return range;
    }

    public int getTime() {
        return time;
    }
}
