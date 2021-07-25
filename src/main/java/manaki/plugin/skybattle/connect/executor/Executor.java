package manaki.plugin.skybattle.connect.executor;

import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.connect.request.QuitRequest;
import manaki.plugin.skybattle.game.state.result.PlayerResult;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;

public class Executor {

    private final Plugin plugin;

    public Executor(Plugin plugin) {
        this.plugin = plugin;
    }

    public void sendQuit(QuitRequest qr) {
        // Send request
        var rs = qr.toString();
        var stream = new ByteArrayOutputStream();
        var out = new DataOutputStream(stream);
        try {
            out.writeUTF("skybattle-quit");
            out.writeUTF(rs);
        } catch (IOException e) {
            SkyBattle.get().getLogger().severe("An I/O error occurred!");
        }
        ((Player) Bukkit.getOnlinePlayers().toArray()[0]).sendPluginMessage(SkyBattle.get(), SkyBattle.CHANNEL, stream.toByteArray());
    }

    public void sendResult(PlayerResult pr) {
        // Send request
        var rs = pr.toString();
        var stream = new ByteArrayOutputStream();
        var out = new DataOutputStream(stream);
        try {
            out.writeUTF("skybattle-player-result");
            out.writeUTF(rs);
        } catch (IOException e) {
            SkyBattle.get().getLogger().severe("An I/O error occurred!");
        }
        ((Player) Bukkit.getOnlinePlayers().toArray()[0]).sendPluginMessage(SkyBattle.get(), SkyBattle.CHANNEL, stream.toByteArray());
    }

}
