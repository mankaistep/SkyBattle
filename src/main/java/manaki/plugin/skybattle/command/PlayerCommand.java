package manaki.plugin.skybattle.command;

import com.google.common.collect.ForwardingMapEntry;
import manaki.plugin.skybattle.game.util.Games;
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


        return false;
    }

}
