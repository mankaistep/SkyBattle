package manaki.plugin.skybattle.connect.team.player;

import java.util.List;

public class TeamPlayer {

    private final String name;
    private final List<String> items;

    public TeamPlayer(String name, List<String> items) {
        this.name = name;
        this.items = items;
    }

    public String getName() {
        return name;
    }

    public List<String> getItems() {
        return items;
    }
}
