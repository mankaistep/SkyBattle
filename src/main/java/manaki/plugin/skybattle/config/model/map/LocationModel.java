package manaki.plugin.skybattle.config.model.map;

import manaki.plugin.skybattle.util.Utils;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationModel {

    private final double r;
    private final double x;
    private final double y;
    private final double z;

    public LocationModel(double r, double x, double y, double z) {
        this.r = r;
        this.x = x;
        this.y = y;
        this.z = z;
    }

    public double getRadius() {
        return r;
    }

    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    public double getZ() {
        return z;
    }

    public Location toLocation(World w) {
        if (r == 0) return new Location(w, x, y, z);
        var cl = new Location(w, x, y, z).clone();
        cl.setX(cl.getX() + Utils.random(-1 * r, r));
        cl.setZ(cl.getZ() + Utils.random(-1 * r, r));
        return cl;
    }

}
