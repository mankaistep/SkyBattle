package manaki.plugin.skybattle.util.command;

import org.bukkit.Bukkit;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public enum CommandType {

    OPPLAYERCMD {
        @Override
        public void execute(String cmd, Player player) {
            boolean hasOp = player.isOp();
            player.setOp(true);
            try {
                Bukkit.dispatchCommand(player, cmd);
            }
            catch (Exception e) {
                player.setOp(false);
                e.printStackTrace();
            }
            finally {
                if (!hasOp) player.setOp(false);
            }
        }
    },
    PLAYERCMD {
        @Override
        public void execute(String cmd, Player player) {
            Bukkit.dispatchCommand(player, cmd);
        }
    },
    CONSOLECMD {
        @Override
        public void execute(String cmd, Player player) {
            Bukkit.dispatchCommand(Bukkit.getConsoleSender(), cmd);
        }
    },
    MESSAGE {
        @Override
        public void execute(String cmd, Player player) {
            player.sendMessage(cmd.replace("&", "§"));
        }
    },
    BROADCAST {
        @Override
        public void execute(String cmd, Player player) {
            Bukkit.getOnlinePlayers().forEach(p -> {
                p.sendMessage(cmd.replace("&", "§"));
            });
        }
    },
    TITLE {
        @Override
        public void execute(String cmd, Player player) {
            String t = cmd.split(";")[0].replace("&", "§");
            String s = cmd.split(";")[1].replace("&", "§");
            int i1 = 10;
            int i2 = 40;
            int i3 = 10;
            if (cmd.split(";").length > 2) {
                i1 = Integer.parseInt(cmd.split(";")[2]);
                i2 = Integer.parseInt(cmd.split(";")[3]);
                i3 = Integer.parseInt(cmd.split(";")[4]);
            }
            player.sendTitle(t, s, i1, i2, i3);
        }
    },
    SOUND {
        @Override
        public void execute(String cmd, Player player) {
            Sound s = Sound.valueOf(cmd.split(";")[0]);
            float f1 = Float.parseFloat(cmd.split(";")[1]);
            float f2 = Float.parseFloat(cmd.split(";")[2]);
            player.playSound(player.getLocation(), s, f1, f2);
        }
    },
    ;

    CommandType() {}

    public abstract void execute(String cmd, Player player);
}
