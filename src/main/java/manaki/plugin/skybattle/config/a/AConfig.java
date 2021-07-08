package manaki.plugin.skybattle.config.a;

import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.craftbukkit.libs.org.apache.commons.io.FileUtils;
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
                var is = this.getPlugin().getResource(file.getName());
                if (is != null) {
                    try {
                        FileUtils.copyInputStreamToFile(is, file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                else file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        this.config = YamlConfiguration.loadConfiguration(file);

        // Reload
        this.reload();
    }

    public abstract void reload();

    public FileConfiguration get() {
        return this.config;
    }

    public Plugin getPlugin() {
        return plugin;
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
