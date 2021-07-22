package manaki.plugin.skybattle.game;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import io.lumine.xikage.mythicmobs.MythicMobs;
import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.area.Areas;
import manaki.plugin.skybattle.config.model.BorderModel;
import manaki.plugin.skybattle.config.model.battle.BattleModel;
import manaki.plugin.skybattle.config.model.battle.ChestGroupItemModel;
import manaki.plugin.skybattle.config.model.battle.ChestItemModel;
import manaki.plugin.skybattle.config.model.map.ChestGroupModel;
import manaki.plugin.skybattle.config.model.map.MapModel;
import manaki.plugin.skybattle.connect.request.QuitRequest;
import manaki.plugin.skybattle.game.manager.GameManager;
import manaki.plugin.skybattle.game.state.BorderState;
import manaki.plugin.skybattle.game.state.GameState;
import manaki.plugin.skybattle.game.state.SupplyState;
import manaki.plugin.skybattle.team.BattleTeam;
import manaki.plugin.skybattle.util.Tasks;
import manaki.plugin.skybattle.util.Utils;
import manaki.plugin.skybattle.world.WorldState;
import me.manaki.plugin.shops.storage.ItemStorage;
import org.bukkit.*;
import org.bukkit.block.Chest;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.metadata.FixedMetadataValue;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.stream.Collectors;

public class Games {

    private static final List<GameManager> managers = Lists.newArrayList();

    private static int maxId = 0;

    public static void start(String battleId, List<BattleTeam> battleTeams, boolean isAsync) {
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
            worldState = loader.load(worldTemplate, true, isAsync);
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

                // Fixed time
                var world = finalWorldState.toWorld();
                world.setGameRule(GameRule.DO_DAYLIGHT_CYCLE, false);
                world.setTime(5000L);

                // No archivement
                world.setGameRule(GameRule.ANNOUNCE_ADVANCEMENTS, false);

                // Clear entities
                clearEntities(finalWorldState.toWorld());

                // Create state object
                maxId++;
                var state = new GameState(maxId, battleId, battleTeams, finalWorldState);

                // Create game manager
                managers.add(new GameManager(state));

                // Remove placers
                removeChestPlacers(state);

                // Generate chests
                spawnChests(state);

                // Teleport (wait for chest spawning)
                Tasks.sync(() -> {
                    var teamLocations = randomLocations(battleTeams, mm.getTeamLocations());
                    for (Map.Entry<BattleTeam, String> e : teamLocations.entrySet()) {
                        var team = e.getKey();
                        var lid = e.getValue();
                        for (String pn : team.getPlayers()) {
                            var p = Bukkit.getPlayer(pn);
                            if (p == null) continue;
                            var l = mm.getLocation(lid).toLocation(finalWorldState.toWorld());
                            p.teleport(l);

                            p.sendTitle("§a§lBắt đầu!", "§fTrên đảo luôn có 3 rương", 10, 80, 10);
                            p.playSound(p.getLocation(), Sound.ENTITY_ENDER_DRAGON_GROWL, 1, 1);

                            // Hidden in tab
                            p.setPlayerListName("Người chơi X");
                        }
                    }

                    // Start tasks
                    state.startTasks();
                }, 40);
            }
        }.runTaskTimer(plugin, 0, 10);

    }

    public static void backToMainServer(Player player) {
        var qr = new QuitRequest(player.getName(), "");
        SkyBattle.get().getExecutor().sendQuit(qr);
    }

    public static GameManager managerFromWorld(World world) {
        for (GameManager manager : managers) {
            if (manager.getState().getWorldState().toWorld() == world) return manager;
        }
        return null;
    }

    public static GameManager managerFromState(GameState state) {
        for (GameManager manager : managers) {
            if (manager.getState() == state) return manager;
        }
        return null;
    }

    public static List<GameManager> getManagers() {
        return managers;
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

        // Random
        for (Map.Entry<String, ChestGroupModel> e : mm.getChestGroups().entrySet()) {
            var grid = e.getKey();
            var group = e.getValue();

            // Get location models
            List<String> locations = Lists.newArrayList(group.getLocations());
            int ranAmount = group.getRandom().random();
            Utils.random(locations, ranAmount);

            // Spawn
            for (String lid : locations) {
                var l = mm.getLocation(lid).toLocation(state.getWorldState().toWorld());

                // Create block
                var b = l.getBlock();
                b.setType(Material.CHEST);
                Chest chest = (Chest) b.getState();
                var inv = chest.getInventory();

                Tasks.async(() -> {
                    // Random items
                    var at = Areas.check(l);
                    var cm = bm.getChests().get(at);
                    var items = randomItems(state, grid);

                    // Put into chest
                    List<Integer> slots = Lists.newArrayList();
                    int amount = items.size();
                    for (int i = 0 ; i < inv.getSize() ; i++) slots.add(i);
                    Utils.random(slots, amount);
                    for (int i = 0; i < slots.size(); i++) {
                        var cim = items.get(i);
                        int slot = slots.get(i);

                        // Santory core sync item load
                        if (cim.getId().contains("santory_")) {
                            Tasks.sync(() -> {
                                var is = ItemStorage.get(cim.getId());
                                if (is == null) {
                                    throw new NullPointerException("Can't find item id: " + cim.getId());
                                }
                                is.setAmount(cim.getAmount().random());
                                inv.setItem(slot, is);
                            });
                        }

                        // Not santory item
                        else {
                            var is = ItemStorage.get(cim.getId());
                            if (is == null) {
                                throw new NullPointerException("Can't find item id: " + cim.getId());
                            }
                            is.setAmount(cim.getAmount().random());
                            inv.setItem(slot, is);
                        }
                    }
                });

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
            if (r <= bs.getCurrentRadius()) lr.add(l);
        }
        return lr;
    }

    public static SupplyState randomizeSupply(GameState state) {
        var bm = SkyBattle.get().getMainConfig().getBattleModel(state.getBattleId());
        var mm = SkyBattle.get().getMainConfig().getMapModel(bm.getMapId());

        // Available supply locations that in border
        var avaiL = Games.filterInBorder(state, mm.getSupplyLocations().stream().map(lm -> mm.getLocation(lm).toLocation(state.getWorldState().toWorld())).collect(Collectors.toList()));
        avaiL.removeIf(state::isSupplySpawned);
        if (avaiL.size() <= 0) return null;

        var l = avaiL.get(new Random().nextInt(avaiL.size()));

        // Randomize items
        List<ItemStack> lr = Lists.newArrayList();
        var list = bm.getSupplyModel().getItemList();
        for (ChestGroupItemModel cgim : list) {
            // Randomize
            var a = cgim.getRandom().random();
            var ri = Lists.newArrayList(cgim.getItems());
            Utils.random(ri, a);

            // Get items
            for (ChestItemModel cim : ri) {
                var is = ItemStorage.get(cim.getId());
                is.setAmount(cim.getAmount().random());
                lr.add(is);
            }
        }

        // Set
        return new SupplyState(l, lr);
    }

    public static BorderState randomizeBorder(GameState state) {
        // Get next border
        var nextBorder = 1;
        var bs = state.getBorderState();
        if (bs != null) nextBorder = bs.getBorderId() + 1;

        // Check available
        var mm = mapFromState(state);
        if (!mm.getBorders().containsKey(nextBorder)) return bs;

        // Check time
        var bdm = mm.getBorders().get(nextBorder);
        if (bdm.getTime() > state.getTime()) return bs;

        // Generate state
        var center = mm.getLocation(bdm.getCenters().get(new Random().nextInt(bdm.getCenters().size())));
        if (bs == null) bs = new BorderState(bdm.getId(), center.toLocation(state.getWorldState().toWorld()), bdm.getRadius());
        else {
            bs.setBorderId(bdm.getId());
            bs.setCenter(center.toLocation(state.getWorldState().toWorld()));
            bs.setCurrentRadius(bdm.getRadius());
        }
        return bs;
    }

    public static BorderModel getNextBorder(GameState state) {
        var nextBorder = 1;
        var bs = state.getBorderState();
        if (bs != null) nextBorder = bs.getBorderId() + 1;

        // Check available
        var mm = mapFromState(state);
        if (!mm.getBorders().containsKey(nextBorder)) return null;
        return mm.getBorders().get(nextBorder);
    }

    public static boolean isTeamAlive(GameState state, BattleTeam battleTeam) {
        for (String pn : battleTeam.getPlayers()) {
            var ps = state.getPlayerState(pn);
            if (ps == null) return true;
            if (!ps.isDead()) return true;
        }
        return false;
    }

    public static GameState getCurrentGame(Player player) {
        for (GameManager gm : managers) {
            if (gm.getState().getPlayers().contains(player)) return gm.getState();
        }
        return null;
    }

    public static Map<BattleTeam, String> randomLocations(List<BattleTeam> battleTeams, List<String> locations) {
        List<String> lr = Lists.newArrayList();
        List<String> clone = Lists.newArrayList(locations);
        int size = battleTeams.size();
        for (int i = 0 ; i < size ; i++) {
            int ri = new Random().nextInt(clone.size());
            lr.add(clone.get(ri));
            clone.remove(ri);
        }

        Map<BattleTeam, String> m = Maps.newHashMap();
        for (int i = 0 ; i < size ; i++) {
            m.put(battleTeams.get(i), lr.get(i));
        }

        return m;
    }
    
    public static void clearEntities(World world) {
        var list = world.getLivingEntities();
        for (org.bukkit.entity.LivingEntity e : list) {
            if (!MythicMobs.inst().getMobManager().isActiveMob(e.getUniqueId())) e.remove();
        }
    }

    public static BorderModel getLastBorder(GameState state) {
        var mm = mapFromState(state);
        return mm.getBorder(mm.getBorders().size());
    }

    public static boolean isSpecialEntity(Entity e) {
        return MythicMobs.inst().getMobManager().isActiveMob(e.getUniqueId())
                || e.hasMetadata("skybattle.entity") || e.hasMetadata("settings.bypass");
    }

    public static void setSpecialEntity(Entity e) {
        e.setMetadata("skybattle.entity", new FixedMetadataValue(SkyBattle.get(), ""));
    }

    public static void bypassInvalidCheck(Player p, long milis) {
        p.setMetadata("skybattle.invalidbypass", new FixedMetadataValue(SkyBattle.get(), ""));
        Tasks.sync(() -> {
            p.removeMetadata("skybattle.invalidbypass", SkyBattle.get());
        }, Long.valueOf(milis / 50).intValue());
    }

    public static boolean isByPassedInvalid(Player p) {
        return p.hasMetadata("skybattle.invalidbypass");
    }

    public static void removeChestPlacers(GameState state) {
        var mm = mapFromState(state);

        // Chest groups
        for (ChestGroupModel cgm : mm.getChestGroups().values()) {
            for (String lid : cgm.getLocations()) {
                var lm = mm.getLocation(lid);
                if (lm == null) {
                    throw new NullPointerException("Can't find location: " + lid);
                }
                var l = lm.toCenterLocation(state.getWorldState().toWorld());
                l.getBlock().setType(Material.AIR);
            }
        }

        // Supply
        for (String lid : mm.getSupplyLocations()) {
            var lm = mm.getLocation(lid);
            if (lm == null) {
                throw new NullPointerException("Can't find location: " + lid);
            }
            var l = lm.toCenterLocation(state.getWorldState().toWorld());
            l.getBlock().setType(Material.AIR);
        }
    }

    public static List<ChestItemModel> randomItems(GameState state, String chestGr) {
        List<ChestItemModel> items = Lists.newArrayList();
        var bm = battleFromState(state);
        if (!bm.getChests().containsKey(chestGr)) return items;

        var list = bm.getChests().get(chestGr);
        for (ChestGroupItemModel cgim : list) {
            var a = cgim.getRandom().random();
            var ri = Lists.newArrayList(cgim.getItems());
            Utils.random(ri, a);
            items.addAll(ri);
        }

        return items;
    }

    public static List<Player> getWorldChatRecipients(Player player) {
        List<Player> list = Lists.newArrayList();
        for (Player p : Bukkit.getOnlinePlayers()) {
            if (p.hasPermission("skybattle.admin")) list.add(p);
            else if (p.getWorld() == player.getWorld()) list.add(p);
        }
        return list;
    }

    public static boolean isTeammate(Player p1, Player p2) {
        var state = getCurrentGame(p1);
        if (state == null) return false;
        if (getCurrentGame(p2) != state) return false;

        return state.getTeam(p1) == state.getTeam(p2);
    }

}
