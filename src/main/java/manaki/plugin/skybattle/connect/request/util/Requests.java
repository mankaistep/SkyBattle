package manaki.plugin.skybattle.connect.request.util;

import com.google.gson.GsonBuilder;
import manaki.plugin.skybattle.connect.request.QuitRequest;
import manaki.plugin.skybattle.connect.request.StartRequest;


public class Requests {

    public static StartRequest parseStart(String s) {
        return new GsonBuilder().create().fromJson(s, StartRequest.class);
    }

    public static QuitRequest parseQuit(String s) {
        return new GsonBuilder().create().fromJson(s, QuitRequest.class);
    }

}
