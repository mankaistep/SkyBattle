package manaki.plugin.skybattle.util.command;

import manaki.plugin.skybattle.util.Utils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Command {
    
    private int tickDelay;
    private String cmd;
    private CommandType type;

    public Command(String s) {
        for (CommandType t : CommandType.values()) {
            if (s.contains("[" + t.name().toLowerCase() + "] ")) {
                this.type = t;
                this.cmd = s.replace("[" + t.name().toLowerCase() + "] ", "");
            }
        }
        if (s.equals("")) s = "*";

        // Delay
        String regex = "\\{(?<delay>\\d+)}\\s";
        Pattern p = Pattern.compile(regex);
        Matcher m = p.matcher(this.cmd);
        if (m.find()) this.tickDelay = Integer.parseInt(m.group("delay"));

        this.cmd = this.cmd.replace("{" + this.tickDelay + "} ", "");
    }

    public String getCommand() {
        return this.cmd;
    }

    public CommandType getType() {
        return this.type;
    }

    @Override
    public String toString() {
        return "[" + this.type.name().toLowerCase() + "] " + this.cmd;
    }

    public void execute(Plugin plugin, Player player, Map<String, String> placeholders) {
        var cmd = this.cmd;
        for (Map.Entry<String, String> e : placeholders.entrySet()) {
            cmd = cmd.replace(e.getKey(), e.getValue());
        }

        // Random placeholder
        if (cmd.contains("%random")) {
            var regex = "(?<placeholder>%random_(?<min>(\\d)+)_(?<max>(\\d)+)%)";
            var p = Pattern.compile(regex);
            var m = p.matcher(cmd);
            while (m.find()) {
                var plh = m.group("placeholder");
                int min = Integer.parseInt(m.group("min"));
                int max = Integer.parseInt(m.group("max"));

                int value = Utils.randomInt(min, max);
                cmd = cmd.replace(plh, value + "");
            }
        }

        execute(plugin, player, cmd);
    }

    public void execute(Plugin plugin, Player player, String cmd) {
        if (this.tickDelay > 0) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> {
                this.type.execute(cmd, player);
            }, this.tickDelay);
        }
        else this.type.execute(cmd, player);
    }

}
