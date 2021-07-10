package manaki.plugin.skybattle.config.model.map;

import manaki.plugin.skybattle.util.Utils;
import org.bukkit.Location;
import org.bukkit.World;

public class LocationModel {

    private final double r;
    private final double x;
    private final double y;
    private final double z;
    private final double pitch;
    private final double yaw;

    public LocationModel(double r, double x, double y, double z, double pitch, double yaw) {
        this.r = r;
        this.x = x;
        this.y = y;
        this.z = z;
        this.pitch = pitch;
        this.yaw = yaw;
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

    public double getPitch() {
        return pitch;
    }

    public double getYaw() {
        return yaw;
    }

    public Location toCenterLocation(World w) {
        return toBlockCenter(new Location(w, x, y, z).clone());
    }

    public Location toLocation(World w) {
        if (r == 0) return new Location(w, x, y, z);
        var cl = toBlockCenter(new Location(w, x, y, z).clone());
        cl.setX(cl.getX() + Utils.random(-1 * r, r));
        cl.setZ(cl.getZ() + Utils.random(-1 * r, r));
        return toBlockCenter(cl);
    }

    private Location toBlockCenter(Location l) {
        l.setX(l.getBlockX() + 0.5);
        l.setZ(l.getBlockZ() + 0.5);
        return l;
    }

}
