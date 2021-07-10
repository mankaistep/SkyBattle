package manaki.plugin.skybattle.game.state;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SupplyState {

    private final Location location;
    private final List<ItemStack> items;
    private boolean isOpened;

    public SupplyState(Location location, List<ItemStack> items) {
        this.location = location;
        this.items = items;
        this.isOpened = false;
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
