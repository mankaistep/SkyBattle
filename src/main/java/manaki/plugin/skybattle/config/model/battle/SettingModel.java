package manaki.plugin.skybattle.config.model.battle;

import java.util.Map;

public class SettingModel {

    public static final String ALLOW_BUILD = "build-allow";
    public static final String WATER_DPS = "water-dps";
    public static final String LAVA_DPS = "lava-dps";

    private Map<String, Object> values;

    public SettingModel(Map<String, Object> values) {
        this.values = values;
    }

    public Map<String, Object> getValues() {
        return values;
    }

    public <T> T get(String id, Class<T> cls) {
        return (T) values.get(id);
    }

}
