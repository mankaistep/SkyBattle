package manaki.plugin.skybattle.config;

import com.google.common.collect.Maps;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import manaki.plugin.skybattle.config.a.AConfig;
import manaki.plugin.skybattle.config.model.battle.BattleModel;
import manaki.plugin.skybattle.config.model.map.ChestGroupModel;
import manaki.plugin.skybattle.config.model.map.LocationModel;
import manaki.plugin.skybattle.config.model.map.MapModel;
import manaki.plugin.skybattle.config.model.reader.Readers;
import manaki.plugin.skybattle.world.WorldTemplate;
import org.bukkit.World;
import org.bukkit.WorldType;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;
import java.util.Map;

public class MainConfig extends AConfig {

    private Map<String, BattleModel> battleModels;
    private Map<String, MapModel> mapModels;

    private Map<String, WorldTemplate> worldTemplates;

    public MainConfig(Plugin plugin, String path) {
        super(plugin, path);
    }

    @Override
    public void reload() {
        // World templates
        this.reloadWorldTemplates();

        // Map
        this.reloadMaps();

        // Battle
        this.reloadBattles();
    }

    private void reloadWorldTemplates() {
        var config = this.get();
        worldTemplates = Maps.newHashMap();
        for (String world : config.getConfigurationSection("world").getKeys(false)) {
            var path = "world." + world;
            var seed = config.getInt(path + ".seed", 0);
            var environment = World.Environment.valueOf(config.getString(path + ".environment", "NORMAL"));
            var type = config.contains(path + ".type") ? WorldType.valueOf(config.getString(path + ".type")) : null;
            var generator = config.getString(".generator");
            worldTemplates.put(world, new WorldTemplate(world, seed, environment, type, generator));
        }

    }

    private void reloadMaps() {
        var folder = new File(this.getPlugin().getDataFolder() + "//models//maps");
        if (!folder.exists()) {
            folder.mkdirs();
            var mtemplate = this.getPlugin().getResource("example-map.yml");
            var mdtemplate = this.getPlugin().getResource("example-map-data.yml");

            var mtfile = new File(this.getPlugin().getDataFolder() + "//models//maps//example-map.yml");
            var mtdfile = new File(this.getPlugin().getDataFolder() + "//models//maps//example-map-data.yml");

            try {
                FileUtils.copyInputStreamToFile(mtemplate, mtfile);
                FileUtils.copyInputStreamToFile(mdtemplate, mtdfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.mapModels = Maps.newHashMap();
        for (File file : folder.listFiles()) {
            if (file.getName().endsWith("data.yml")) continue;

            var id = file.getName().replace(".yml", "");
            var datafile = new File(this.getPlugin().getDataFolder() + "//models//maps//" + id + "-data.yml");
            if (!datafile.exists()) {
                try {
                    datafile.createNewFile();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            var locations = Readers.readMapData(datafile);
            var mm = Readers.readMapModel(file, locations);
            this.mapModels.put(mm.getId(), mm);
        }
    }

    public void saveMapData(String mapId) {
        var mm = getMapModel(mapId);
        var l = mm.getLocations();

        var datafile = new File(this.getPlugin().getDataFolder() + "//models//maps//" + mapId + "-data.yml");
        var config = YamlConfiguration.loadConfiguration(datafile);
        config.set("location", null);

        for (Map.Entry<String, LocationModel> e : l.entrySet()) {
            var lid = e.getKey();
            var lm = e.getValue();
            var s = lm.getRadius() + ";" + lm.getX() + ";" + lm.getY() + ";" + lm.getZ() + ";" + lm.getPitch() + ";" + lm.getYaw();
            config.set("location." + lid, s);
        }

        try {
            config.save(datafile);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    private void reloadBattles() {
        var folder = new File(this.getPlugin().getDataFolder() + "//models//battles");
        if (!folder.exists()) {
            folder.mkdirs();
            var btemplate = this.getPlugin().getResource("example-battle.yml");
            var btfile = new File(this.getPlugin().getDataFolder() + "//models//battles//example-battle.yml");

            try {
                FileUtils.copyInputStreamToFile(btemplate, btfile);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        this.battleModels = Maps.newHashMap();
        for (File file : folder.listFiles()) {
            var bm = Readers.readBattleModel(file);
            this.battleModels.put(bm.getId(), bm);
        }
    }

    public void saveChestGroup(String mapId) {
        var mm = getMapModel(mapId);
        var datafile = new File(this.getPlugin().getDataFolder() + "//models//maps//" + mm.getId() + ".yml");
        var config = YamlConfiguration.loadConfiguration(datafile);

        config.set("chest-group", null);
        for (Map.Entry<String, ChestGroupModel> e : mm.getChestGroups().entrySet()) {
            var id = e.getKey();
            var cgm = e.getValue();
            config.set("chest-group." + id + ".random", cgm.getRandom().toString());
            config.set("chest-group." + id + ".locations", cgm.getLocations());
        }

        try {
            config.save(datafile);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public Map<String, BattleModel> getBattleModels() {
        return battleModels;
    }

    public Map<String, MapModel> getMapModels() {
        return mapModels;
    }

    public BattleModel getBattleModel(String id) {
        return battleModels.getOrDefault(id, null);
    }

    public MapModel getMapModel(String id) {
        return mapModels.getOrDefault(id, null);
    }

    public WorldTemplate getWorldTemplate(String name) {
        return worldTemplates.getOrDefault(name, null);
    }

}
