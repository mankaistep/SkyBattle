package manaki.plugin.skybattle.listener;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.config.model.map.ChestGroupModel;
import manaki.plugin.skybattle.config.model.map.LocationModel;
import manaki.plugin.skybattle.setup.chestgroup.ChestGroupPlacer;
import manaki.plugin.skybattle.setup.chestgroup.ChestGroupPlacers;
import manaki.plugin.skybattle.util.MinMax;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockPlaceEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.Map;
import java.util.Set;

public class PlacerListener implements Listener {

    private final Map<Player, String> rChecks;

    public PlacerListener() {
        this.rChecks = Maps.newHashMap();
    }

    @EventHandler
    public void onPlace(BlockPlaceEvent e) {
        var p = e.getPlayer();
        var is = e.getItemInHand();

        var gplacer = ChestGroupPlacers.read(is);
        if (gplacer == null) return;

        if (rChecks.containsKey(p)) {
            p.sendMessage("§cEnter radius first!");
            e.setCancelled(true);
            return;
        }

        var b = e.getBlock();
        var l = b.getLocation();

        var mm = SkyBattle.get().getMainConfig().getMapModel(gplacer.getMapId());
        var groups = mm.getChestGroups();
        if (!groups.containsKey(gplacer.getGroupId())) {
            var cgm = new ChestGroupModel(gplacer.getGroupId(), Lists.newArrayList(), new MinMax(1, 2));
            groups.put(gplacer.getGroupId(), cgm);
        }

        var cgm = groups.get(gplacer.getGroupId());
        var lid = gplacer.getMapId() + "-" + gplacer.getGroupId() + "-" + (cgm.getLocations().size() + 1);
        var lm = new LocationModel(0, l.getX(), l.getY(), l.getZ(), p.getLocation().getPitch(), p.getLocation().getYaw());

        // Add to data
        mm.setLocation(lid, lm);
        cgm.getLocations().add(lid);
        gplacer.setLocation(lid);

        // Done
        p.sendMessage("§aEnter the radius below V");
        rChecks.put(p, new GsonBuilder().create().toJson(gplacer));
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        var p = e.getPlayer();
        if (rChecks.containsKey(p)) {
            try {
                var r = Double.parseDouble(e.getMessage());

                var gplacer = new GsonBuilder().create().fromJson(rChecks.get(p), ChestGroupPlacer.class);
                var mid = gplacer.getMapId();

                var mm = SkyBattle.get().getMainConfig().getMapModel(mid);
                var lm = mm.getLocation(gplacer.getLocationId());
                lm.setRadius(r);

                // Remove check
                rChecks.remove(p);

                // Save
                SkyBattle.get().getMainConfig().saveMapData(mm.getId());
                SkyBattle.get().getMainConfig().saveChestGroup(mm.getId());

                // Done
                p.sendMessage("§aOk done! Map id: " + mm.getId() + ", Group id: " + gplacer.getGroupId() + ", Location id: " + gplacer.getLocationId() + ", Radius: " + lm.getRadius());
                e.setCancelled(true);
            }
            catch (NumberFormatException ex) {
                p.sendMessage("§cWrong typed, re-enter please!");
            }
        }
    }

}
