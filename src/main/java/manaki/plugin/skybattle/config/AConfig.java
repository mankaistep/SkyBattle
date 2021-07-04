package manaki.plugin.skybattle.config;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.Plugin;

import java.io.File;
import java.io.IOException;

public abstract class AConfig {

    private final Plugin plugin;
    private final String path;

    private FileConfiguration config;

    public AConfig(Plugin plugin, String path) {
        this.plugin = plugin;
        this.path = path.replace(".yml", "");

        // Load
        var file = new File(path + ".yml");
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);
    }

    public FileConfiguration get() {
        return this.config;
    }

    public void save() {
        var file = new File(path + ".yml");
        try {
            this.config.save(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
