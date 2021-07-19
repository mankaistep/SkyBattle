package manaki.plugin.skybattle.connect.request;

import com.google.gson.GsonBuilder;

public class QuitRequest {

    private final String player;
    private final String data;

    public QuitRequest(String player, String data) {
        this.player = player;
        this.data = data;
    }

    public String getPlayer() {
        return player;
    }

    public String getData() {
        return data;
    }

    public String toString() {
        return new GsonBuilder().create().toJson(this);
    }
}
