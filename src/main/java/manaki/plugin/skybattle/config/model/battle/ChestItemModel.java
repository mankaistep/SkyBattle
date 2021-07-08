package manaki.plugin.skybattle.config.model.battle;

import manaki.plugin.skybattle.util.MinMax;

public class ChestItemModel {

    private final String id;
    private final MinMax amount;

    public ChestItemModel(String id, MinMax amount) {
        this.id = id;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public MinMax getAmount() {
        return amount;
    }

    public static ChestItemModel parse(String s) {
        var a = s.split(" ");
        var id = a[0];
        var minmax = new MinMax(1, 1);
        if (a.length > 1) {
            minmax = MinMax.parse(a[1]);
        }
        return new ChestItemModel(id, minmax);
    }

}
