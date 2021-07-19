package manaki.plugin.skybattle.listener;

import io.papermc.paper.event.player.AsyncChatEvent;
import manaki.plugin.skybattle.spectator.Spectators;
import org.bukkit.GameMode;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

public class SpectatorListener implements Listener {

    // Spectator chat
    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {
        var p = e.getPlayer();
        if (p.getGameMode() != GameMode.SPECTATOR) return;

        var message = e.getMessage();
        var w = p.getWorld();
        var recipients = e.getRecipients();

        recipients.clear();
        recipients.addAll(Spectators.getSpectatorsInWorld(w));
        e.setFormat("§b[@spectator] " + p.getName() + ": §7"  + message.replace("@all", ""));
    }



}
