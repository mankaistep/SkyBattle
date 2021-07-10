package manaki.plugin.skybattle.command;

import com.google.common.collect.Lists;
import manaki.plugin.skybattle.SkyBattle;
import manaki.plugin.skybattle.config.model.map.LocationModel;
import manaki.plugin.skybattle.game.util.Games;
import manaki.plugin.skybattle.team.Team;
import org.bukkit.Color;
import org.bukkit.Particle;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;

public class AdminCommand implements @Nullable CommandExecutor {

    @Override
    public boolean onCommand(@NotNull CommandSender sender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {

        try {


            if (args[0].equalsIgnoreCase("reload")) {
                SkyBattle.get().getMainConfig().reload();
                sender.sendMessage("§aConfig has been reloaded!");
                return false;
            }

            else if (args[0].equalsIgnoreCase("start")) {
                var bid = args[1];

                List<Team> teams = Lists.newArrayList();
                for (String pn : args[2].split(";")) {
                    teams.add(new Team(Color.RED, List.of(pn)));
                }

                Games.start(bid, teams);
            }

            else if (args[0].equalsIgnoreCase("setlocation")) {
                var p = (Player) sender;
                var mid = args[1];
                double radius = Double.parseDouble(args[2]);
                var id = args[3];
                var l = p.getLocation();
                var lm = new LocationModel(radius, l.getX(), l.getY(), l.getZ(), l.getPitch(), l.getYaw());
                var mm = SkyBattle.get().getMainConfig().getMapModel(mid);
                mm.setLocation(id, lm);

                SkyBattle.get().getMainConfig().saveMapData(mid);

                sender.sendMessage("§aDone!");

                l.getWorld().spawnParticle(Particle.FLAME, l.getBlock().getLocation().clone().add(0.5, 0.5, 0.5), 1, 0, 0, 0, 0);
            }

            else if (args[0].equalsIgnoreCase("removelocation")) {
                var mid = args[1];
                var id = args[2];
                var mm = SkyBattle.get().getMainConfig().getMapModel(mid);
                mm.removeLocation(id);

                SkyBattle.get().getMainConfig().saveMapData(mid);

                sender.sendMessage("§aDone!");
            }

        }
        catch (ArrayIndexOutOfBoundsException e) {
            sendHelp(sender);
        }

        return false;
    }

    public void sendHelp(CommandSender sender) {
        sender.sendMessage("/skybattle reload");
        sender.sendMessage("/skybattle start <id> <player1>;<player2>");
        sender.sendMessage("");
        sender.sendMessage("/skybattle setlocation <mapId> <radius> <id>");
        sender.sendMessage("/skybattle removelocation <mapId> <id>");

    }
}
