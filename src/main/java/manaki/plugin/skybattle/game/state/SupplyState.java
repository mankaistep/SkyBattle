package manaki.plugin.skybattle.game.state;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public class SupplyState {

    private final Location location;
    private final List<ItemStack> items;

    public SupplyState(Location location, List<ItemStack> items) {
        this.location = location;
        this.items = items;
    }

    public Location getLocation() {
        return location;
    }

    public List<ItemStack> getItems() {
        return items;
    }
}
