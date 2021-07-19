package manaki.plugin.skybattle.command;

import manaki.plugin.skybattle.game.util.Games;
import manaki.plugin.skybattle.spectator.SpectatorGUI;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;

public class PlayerCommand implements CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command cmd, @NotNull String s, @NotNull String[] args) {

        if (cmd.getName().equals("quit")) {
            var p = (Player) sender;
            Games.backToMainServer(p);
        }

        else if (cmd.getName().equals("spectator")) {
            var p = (Player) sender;
            var state = Games.getCurrentGame(p);
            if (state != null) {
                p.sendMessage("§cBạn đang trong một game, không thể dùng tính năng này!");
                return false;
            }
            SpectatorGUI.open(p);
        }


        return false;
    }

}
