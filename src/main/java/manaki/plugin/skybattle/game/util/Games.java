package manaki.plugin.skybattle.game.util;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.area.Areas;
import manaki.plugin.skybattle.config.model.BorderModel;
import manaki.plugin.skybattle.config.model.battle.BattleModel;
import manaki.plugin.skybattle.config.model.map.ChestGroupModel;
import manaki.plugin.skybattle.config.model.map.MapModel;
import manaki.plugin.skybattle.game.manager.GameManager;
import manaki.plugin.skybattle.game.state.BorderState;
import manaki.plugin.skybattle.game.state.GameState;
import manaki.plugin.skybattle.game.state.SupplyState;
import manaki.plugin.skybattle.team.Team;
import manaki.plugin.skybattle.util.Utils;
import manaki.plugin.skybattle.world.WorldState;
import me.manaki.plugin.shops.storage.ItemStorage;
import net.minecraft.server.v1_16_R3.is;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Chest;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.*;
import java.util.stream.Collectors;

public class Games {

    private static final List<GameManager> managers = Lists.newArrayList();

    private static int maxId = 0;

    public static void start(String battleId, List<Team> teams) {
        // Load template world
        var plugin = SkyBattle.get();
        var config = plugin.getMainConfig();
        var bm = config.getBattleModel(battleId);
        var mm = config.getMapModel(bm.getMapId());
        var worldSource = mm.getWorldName();
        var worldTemplate = config.getWorldTemplate(worldSource);
        var loader = plugin.getWorldLoader();
        WorldState worldState = null;
        try {
            worldState = loader.load(worldTemplate, true, true);
        }
        catch (Exception e) {
            plugin.getLogger().warning("Exception appeared when world is being loaded");
            e.printStackTrace();
            return;
        }
        plugin.getWorldManager().addActiveWorld(worldState);

        // Wait to do
        WorldState finalWorldState = worldState;
        new BukkitRunnable() {
            @Override
            public void run() {
                // Wait till load done
                if (loader.isLoading(finalWorldState.toWorldName())) return;

                // Cancel task
                this.cancel();

                // Create state object
                maxId++;
                var state = new GameState(maxId, battleId, teams, finalWorldState);

                // Create game manager
                managers.add(new GameManager(state));

                // Generate chests
                spawnChests(state);

                // Teleport
                var teamLocations = randomLocations(teams, mm.getTeamLocations());
                for (Map.Entry<Team, String> e : teamLocations.entrySet()) {
                    var team = e.getKey();
                    var lid = e.getValue();
                    for (String pn : team.getPlayers()) {
                        var p = Bukkit.getPlayer(pn);
                        var l = mm.getLocation(lid).toLocation(finalWorldState.toWorld());
                        p.teleport(l);
                        p.sendTitle("§a§lBắt đầu!", "§fHãy trở thành team sống sót cuối cùng", 10, 60, 10);
                    }
                }
            }
        }.runTaskTimer(plugin, 0, 10);

    }

    public static GameManager managerFromState(GameState state) {
        for (GameManager manager : managers) {
            if (manager.getState() == state) return manager;
        }
        return null;
    }

    public static void removeManager(GameManager manager) {
        managers.remove(manager);
    }

    public static BattleModel battleFromState(GameState state) {
        return SkyBattle.get().getMainConfig().getBattleModel(state.getBattleId());
    }

    public static MapModel mapFromState(GameState state) {
        var bm = battleFromState(state);
        return SkyBattle.get().getMainConfig().getMapModel(bm.getMapId());
    }

    public static GameState fromWorld(World w) {
        for (GameManager gm : managers) {
            if (gm.getState().getWorldState().toWorld().equals(w)) return gm.getState();
        }
        return null;
    }

    public static void spawnChests(GameState state) {
        int count = 0;

        var mm = mapFromState(state);
        var bm = battleFromState(state);

        for (Map.Entry<String, ChestGroupModel> e : mm.getChestGroups().entrySet()) {
            var id = e.getKey();
            var group = e.getValue();

            // Get location models
            List<String> locations = List.copyOf(group.getLocations());
            Utils.random(group.getLocations(), group.getRandom().random());

            // Spawn
            for (String lid : locations) {
                var l = mm.getLocation(lid).toLocation(state.getWorldState().toWorld());

                // Create block
                var b = l.getBlock();
                b.setType(Material.CHEST);
                Chest chest = (Chest) b.getState();
                var inv = chest.getInventory();

                // Random items
                var at = Areas.check(l);
                var cm = bm.getChests().get(at);
                var items = List.copyOf(cm.getItems());
                Utils.random(items, cm.getRandom().random());

                // Put into chest
                List<Integer> slots = Lists.newArrayList();
                int amount = items.size();
                for (int i = 0 ; i < inv.getSize() ; i++) slots.add(i);
                Utils.random(slots, amount);
                for (int i = 0; i < slots.size(); i++) {
                    var cim = items.get(i);
                    var is = ItemStorage.get(cim.getId());
                    is.setAmount(cim.getAmount().random());
                    inv.setItem(slots.get(i), is);
                }

                // Count
                count++;
            }
        }

        SkyBattle.get().getLogger().info("Spawned " + count + " chest for battle " + bm.getId());
    }

    public static void broadcast(GameState state, String message) {
        for (Player p : state.getPlayers()) {
            p.sendMessage(message);
        }
    }

    public static List<Location> filterInBorder(GameState state, List<Location> list) {
        List<Location> lr = Lists.newArrayList();
        var bs = state.getBorderState();
        for (Location l : list) {
            var r = l.distance(bs.getCenter());
            if (r <= bs.getRadius()) lr.add(l);
        }
        return lr;
    }

    public static SupplyState randomizeSupply(GameState state) {
        var bm = SkyBattle.get().getMainConfig().getBattleModel(state.getBattleId());
        var mm = SkyBattle.get().getMainConfig().getMapModel(bm.getMapId());

        // Available supply locations that in border
        var avaiL = Games.filterInBorder(state, mm.getSupplyLocations().stream().map(lm -> mm.getLocation(lm).toLocation(state.getWorldState().toWorld())).collect(Collectors.toList()));
        var l = avaiL.get(new Random().nextInt(avaiL.size()));

        // Randomize items
        var sm = bm.getSupplyModel();
        int amount = sm.getRandom().random();
        var list = List.copyOf(sm.getItems());
        List<ItemStack> lr = Lists.newArrayList();
        for (int i = 0 ; i < amount ; i++ ){
            var ri = new Random().nextInt(list.size());
            var cim = list.get(ri);

            var is = ItemStorage.get(cim.getId());
            is.setAmount(cim.getAmount().random());

            lr.add(is);
            list.remove(ri);
        }

        // Set
        return new SupplyState(l, lr);
    }

    public static BorderState randomizeBorder(GameState state) {
        // Get next border
        var nextBorder = "1";
        var bs = state.getBorderState();
        if (bs != null) nextBorder = (Integer.parseInt(bs.getBorderId()) + 1) + "";

        // Check available
        var mm = mapFromState(state);
        if (!mm.getBorders().containsKey(nextBorder)) return bs;

        // Check time
        var bdm = mm.getBorders().get(nextBorder);
        if (bdm.getTime() >= state.getTime()) return bs;

        // Generate state
        var center = mm.getLocation(bdm.getCenters().get(new Random().nextInt(bdm.getCenters().size())));
        if (bs == null) bs = new BorderState(bdm.getId(), center.toLocation(state.getWorldState().toWorld()), bdm.getRadius(), bdm.getRadius());
        else {
            bs.setBorderId(bdm.getId());
            bs.setCenter(center.toLocation(state.getWorldState().toWorld()));
            bs.setRadius(bdm.getRadius());
        }
        return bs;
    }

    public static BorderModel getNextBorder(GameState state) {
        var nextBorder = "1";
        var bs = state.getBorderState();
        if (bs != null) nextBorder = (Integer.parseInt(bs.getBorderId()) + 1) + "";

        // Check available
        var mm = mapFromState(state);
        if (!mm.getBorders().containsKey(nextBorder)) return null;
        return mm.getBorders().get(nextBorder);
    }

    public static boolean isTeamAlive(GameState state, Team team) {
        for (String pn : team.getPlayers()) {
            if (!state.getPlayerState(pn).isDead()) return true;
        }
        return false;
    }

    public static GameState getCurrentGame(Player player) {
        for (GameManager gm : managers) {
            if (gm.getState().getPlayers().contains(player)) return gm.getState();
        }
        return null;
    }

    public static Map<Team, String> randomLocations(List<Team> teams, List<String> locations) {
        List<String> lr = Lists.newArrayList();
        List<String> clone = Lists.newArrayList(locations);
        int size = teams.size();
        for (int i = 0 ; i < size ; i++) {
            int ri = new Random().nextInt(clone.size());
            lr.add(clone.get(ri));
            clone.remove(ri);
        }

        Map<Team, String> m = Maps.newHashMap();
        for (int i = 0 ; i < size ; i++) {
            m.put(teams.get(i), lr.get(i));
        }

        return m;
    }

}
