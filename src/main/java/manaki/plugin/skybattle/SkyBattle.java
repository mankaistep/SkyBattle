package manaki.plugin.skybattle;

import manaki.plugin.skybattle.command.AdminCommand;
import manaki.plugin.skybattle.command.PlayerCommand;
import manaki.plugin.skybattle.config.MainConfig;
import manaki.plugin.skybattle.connect.executor.Executor;
import manaki.plugin.skybattle.connect.listener.ConnectListener;
import manaki.plugin.skybattle.game.manager.GameManager;
import manaki.plugin.skybattle.game.Games;
import manaki.plugin.skybattle.listener.*;
import manaki.plugin.skybattle.util.Invisibles;
import manaki.plugin.skybattle.world.WorldListener;
import manaki.plugin.skybattle.world.WorldLoader;
import manaki.plugin.skybattle.world.WorldManager;
import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

public class SkyBattle extends JavaPlugin {

    public static final String CHANNEL = "game:skybattle";

    private MainConfig mainConfig;
    private WorldLoader worldLoader;
    private WorldManager worldManager;

    private Executor executor;

    @Override
    public void onEnable() {
        this.mainConfig = new MainConfig(this, this.getDataFolder() + "//config.yml");
        this.worldLoader = new WorldLoader(this);
        this.worldManager = new WorldManager(this);
        this.executor = new Executor(this);

        this.registerCommands();
        this.registerListeners();
        this.registerTasks();
        this.registerChannel();
    }

    @Override
    public void onDisable() {
        try {
            this.cancelAllGames();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (worldLoader != null) this.worldLoader.unloadAllTemporaryWorlds(false);
        }
    }

    public void registerListeners() {
        Bukkit.getPluginManager().registerEvents(new WorldListener(this), this);
        Bukkit.getPluginManager().registerEvents(new PlayerListener(), this);
        Bukkit.getPluginManager().registerEvents(new MobListener(), this);
        Bukkit.getPluginManager().registerEvents(new PlacerListener(), this);
        Bukkit.getPluginManager().registerEvents(new CustomItemListener(), this);
        Bukkit.getPluginManager().registerEvents(new SpectatorListener(), this);
    }

    private void registerTasks() {
        new Invisibles().runTaskTimer(this, 0, 10);
    }


    public void registerCommands() {
        this.getCommand("skybattle").setExecutor(new AdminCommand());
        this.getCommand("quit").setExecutor(new PlayerCommand());
        this.getCommand("spectator").setExecutor(new PlayerCommand());
    }

    public void registerChannel() {
        this.getServer().getMessenger().registerOutgoingPluginChannel(this, CHANNEL);
        this.getServer().getMessenger().registerIncomingPluginChannel(this, CHANNEL, new ConnectListener());
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

    public Executor getExecutor() {
        return executor;
    }

    public void cancelAllGames() {
        List<GameManager> list = Games.getManagers();
        for (int i = 0 ; i < list.size() ; i++) {
            Games.getManagers().get(i).finish(true);
        }
    }

    public static SkyBattle get() {
        return JavaPlugin.getPlugin(SkyBattle.class);
    }

}
