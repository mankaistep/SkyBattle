package manaki.plugin.skybattle;

import manaki.plugin.skybattle.command.AdminCommand;
import manaki.plugin.skybattle.config.MainConfig;
import manaki.plugin.skybattle.listener.PlayerListener;
import manaki.plugin.skybattle.world.WorldLoader;
import manaki.plugin.skybattle.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class SkyBattle extends JavaPlugin {

    private MainConfig mainConfig;
    private WorldLoader worldLoader;
    private WorldManager worldManager;

    @Override
    public void onEnable() {
        this.mainConfig = new MainConfig(this, "config.yml");
        this.worldLoader = new WorldLoader(this);
        this.worldManager = new WorldManager(this);


    }

    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
    }

    public void registerCommands() {
        this.getCommand("skybattle").setExecutor(new AdminCommand());
    }

    public MainConfig getMainConfig() {
        return mainConfig;
    }

    public WorldLoader getWorldLoader() {
        return worldLoader;
    }

    public WorldManager getWorldManager() {
        return worldManager;
    }

    public static SkyBattle get() {
        return JavaPlugin.getPlugin(SkyBattle.class);
    }

}
