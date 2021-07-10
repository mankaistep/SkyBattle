package manaki.plugin.skybattle.game.state;

import org.bukkit.Location;

public class BorderState {

    private int borderId;
    private Location center;
    private int currentRadius;

    public BorderState(int borderId, Location center, int currentRadius) {
        this.borderId = borderId;
        this.center = center;
        this.currentRadius = currentRadius;
    }

    public int getBorderId() {
        return borderId;
    }

    public Location getCenter() {
        return center;
    }

    public int getCurrentRadius() {
        return currentRadius;
    }

    public void setCurrentRadius(int currentRadius) {
        this.currentRadius = currentRadius;
    }

    public void setBorderId(int borderId) {
        this.borderId = borderId;
    }

    public void setCenter(Location center) {
        this.center = center;
    }
}
