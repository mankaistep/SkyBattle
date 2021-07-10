package manaki.plugin.skybattle.config.model.reader;

import com.google.common.collect.Maps;
import manaki.plugin.skybattle.area.AreaType;
import manaki.plugin.skybattle.config.model.BorderModel;
import manaki.plugin.skybattle.config.model.battle.*;
import manaki.plugin.skybattle.config.model.map.ChestGroupModel;
import manaki.plugin.skybattle.config.model.map.LocationModel;
import manaki.plugin.skybattle.config.model.map.MapModel;
import manaki.plugin.skybattle.util.MinMax;
import manaki.plugin.skybattle.util.command.Command;
import org.bukkit.Material;
import org.bukkit.configuration.file.YamlConfiguration;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class Readers {

    public static Map<String, LocationModel> readMapData(@NotNull File file) {
        var config = YamlConfiguration.loadConfiguration(file);
        Map<String, LocationModel> m = Maps.newHashMap();
        for (String id : config.getConfigurationSection("location").getKeys(false)) {
            String l = config.getString("location." + id);
            var a = l.split(";");
            var r = Double.parseDouble(a[0]);
            var x = Double.parseDouble(a[1]);
            var y = Double.parseDouble(a[2]);
            var z = Double.parseDouble(a[3]);
            double pitch = 0;
            double yaw = 0;
            if (a.length > 4) {
                pitch = Double.parseDouble(a[4]);
                yaw = Double.parseDouble(a[5]);
            }
            var lmodel = new LocationModel(r, x, y, z, pitch, yaw);
            m.put(id, lmodel);
        }
        return m;
    }

    public static MapModel readMapModel(File file, Map<String, LocationModel> locations) {
        var config = YamlConfiguration.loadConfiguration(file);

        var id = file.getName().replace(".yml", "");
        var w = config.getString("world-name");
        var center = config.getString("map.center");
        Map<AreaType, Double> ar = Maps.newHashMap();
        for (AreaType at : AreaType.values()) {
            double r = config.getDouble("map.radius." + at.name());
            ar.put(at, r);
        }

        LinkedHashMap<Integer, BorderModel> borders = Maps.newLinkedHashMap();
        int i = 1;
        while (config.contains("border." + i)) {
            var centers = config.getStringList("border." + i + ".center");
            var range = config.getInt("border." + i + ".range");
            var start = config.getInt("border." + i + ".start-time");
            var bm = new BorderModel(i, centers, range, start);
            borders.put(i, bm);

            i++;
        }

        var supplys = config.getStringList("supply-locations");
        var spawns = config.getStringList("spawn-locations");

        Map<String, ChestGroupModel> chestGroups = Maps.newHashMap();
        for (String cid : config.getConfigurationSection("chest-group").getKeys(false)) {
            var l = config.getStringList("chest-group." + cid + ".location");
            var random = MinMax.parse(config.getString("chest-group." + cid + ".random"));
            var cgm = new ChestGroupModel(cid, l, random);
            chestGroups.put(cid, cgm);
        }

        return new MapModel(id, w, center, ar, borders, supplys, spawns, chestGroups, locations);
    }

    public static BattleModel readBattleModel(File file) {
        var config = YamlConfiguration.loadConfiguration(file);

        var id = file.getName().replace(".yml", "");
        var mapId = config.getString("map");
        var time = config.getInt("time");
        var boss = config.getString("boss");
        var sm = new SettingModel(config.getConfigurationSection("setting").getValues(false));

        Map<AreaType, List<String>> mobTypes = Maps.newHashMap();
        for (String tn : config.getConfigurationSection("mob.mob-type").getKeys(false)) {
            var type = AreaType.valueOf(tn.toUpperCase());
            var types = config.getStringList("mob.mob-type." + tn);
            mobTypes.put(type, types);
        }
        int limitPerPlayer = config.getInt("mob.check.limit-per-player");
        double rate = config.getDouble("mob.check.rate");
        var mm = new MobModel(mobTypes, limitPerPlayer, Double.valueOf(rate).floatValue());

        Map<AreaType, ChestModel> chest = Maps.newHashMap();
        for (String tn : config.getConfigurationSection("chest").getKeys(false)) {
            var type = AreaType.valueOf(tn.toUpperCase());
            var random = MinMax.parse(config.getString("chest." + tn + ".random"));
            var items = config.getStringList("chest." + tn + ".items").stream().map(ChestItemModel::parse).collect(Collectors.toList());
            chest.put(type, new ChestModel(random, items));
        }

        var sat = config.getStringList("supply-chest.appear-time").stream().map(Integer::parseInt).collect(Collectors.toList());
        var sr = MinMax.parse(config.getString("supply-chest.random"));
        var items = config.getStringList("supply-chest.items").stream().map(ChestItemModel::parse).collect(Collectors.toList());
        var supplyModel = new SupplyModel(sat, sr, items);

        Map<Material, List<Command>> blockCommands = Maps.newHashMap();
        for (String mn : config.getConfigurationSection("command.block").getKeys(false)) {
            var m = Material.valueOf(mn.toUpperCase());
            List<Command> cmds = config.getStringList("command.block." + mn).stream().map(Command::new).collect(Collectors.toList());
            blockCommands.put(m, cmds);
        }
        List<Command> winCommands = config.getStringList("command.win").stream().map(Command::new).collect(Collectors.toList());

        return new BattleModel(id, mapId, boss, time, sm, mm, chest, supplyModel, blockCommands, winCommands);
    }

}
