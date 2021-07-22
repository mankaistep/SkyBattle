package manaki.plugin.skybattle.config.model.battle;

import manaki.plugin.skybattle.util.command.Command;

public class KillCmdModel {

    private final Command cmd;
    private final double rate;

    public KillCmdModel(Command cmd, double rate) {
        this.cmd = cmd;
        this.rate = rate;
    }

    public Command getCmd() {
        return cmd;
    }

    public double getRate() {
        return rate;
    }
}
