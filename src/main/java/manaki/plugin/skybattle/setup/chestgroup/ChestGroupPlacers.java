package manaki.plugin.skybattle.setup.chestgroup;

import com.google.gson.GsonBuilder;
import manaki.plugin.skybattle.util.ItemStackManager;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;

public class ChestGroupPlacers {

    public static ChestGroupPlacer read(ItemStack is) {
        var ism = new ItemStackManager(is);
        if (!ism.hasTag("skybattle.cgp")) return null;

        var s = ism.getTag("skybattle.cgp");
        return new GsonBuilder().create().fromJson(s, ChestGroupPlacer.class);
    }

    public static ItemStack get(String gid, String mid, Material m) {
        var is = new ItemStack(m);
        var ism = new ItemStackManager(is);
        ism.setName("§a§lPlacer " + mid + " ~ " + gid);
        ism.setTag("skybattle.cgp", new GsonBuilder().create().toJson(new ChestGroupPlacer(mid, gid)));

        return is;
    }

}
