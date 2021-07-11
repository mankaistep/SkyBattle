package manaki.plugin.skybattle.setup.chestgroup;

public class ChestGroupPlacer {

    private final String mid;
    private final String gid;

    private String lid;

    public ChestGroupPlacer(String mid, String id) {
        this.mid = mid;
        this.gid = id;
        this.lid = null;
    }

    public String getGroupId() {
        return gid;
    }

    public String getMapId() {
        return mid;
    }

    public String getLocationId() {
        return lid;
    }

    public void setLocation(String lid) {
        this.lid = lid;
    }
}
