package manaki.plugin.skybattle.command;

import manaki.plugin.skybattle.SkyBattle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public class AdminCommand implements @Nullable CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        if (args[0].equalsIgnoreCase("reload")) {
            SkyBattle.get().getMainConfig().reload();
            sender.sendMessage("§aConfig has been reloaded!");
            return false;
        }

        else if (args[0].equalsIgnoreCase("start")) {

        }

        return false;
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage("/skybattle reload");
        sender.sendMessage("/skybattle start ...");
    }
}
