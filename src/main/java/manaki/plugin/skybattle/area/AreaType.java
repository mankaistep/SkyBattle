package manaki.plugin.skybattle.area;

public enum AreaType {

    CENTER(2),

    NEAR_CENTER(1),

    EDGE(0);

    private final int priority;

    AreaType(int priority) {
        this.priority = priority;
    }

    public int getPriority() {
        return priority;
    }
}
