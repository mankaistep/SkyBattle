package manaki.plugin.skybattle.game.state;

import org.bukkit.Location;
import org.bukkit.entity.Shulker;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SupplyState {

    private Shulker shulker = null;
    private final Location location;
    private final List<ItemStack> items;
    private boolean isOpened;

    public SupplyState(Location location, List<ItemStack> items) {
        this.location = location;
        this.items = items;
        this.isOpened = false;
    }

    public Shulker getShulker() {
        return shulker;
    }

    public void setShulker(Shulker shulker) {
        this.shulker = shulker;
    }

    public Location getLocation() {
        return location;
    }

    public List<ItemStack> getItems() {
        return items;
    }

    public void setOpened(boolean opened) {
        isOpened = opened;
    }

    public boolean isOpened() {
        return isOpened;
    }
}
