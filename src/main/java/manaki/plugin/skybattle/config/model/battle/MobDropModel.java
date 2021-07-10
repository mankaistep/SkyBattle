package manaki.plugin.skybattle.config.model.battle;

import manaki.plugin.skybattle.util.MinMax;

public class MobDropModel {

    private final String itemId;
    private final MinMax amount;
    private final double rate;

    public MobDropModel(String itemId, MinMax amount, double rate) {
        this.itemId = itemId;
        this.amount = amount;
        this.rate = rate;
    }

    public String getItemId() {
        return itemId;
    }

    public MinMax getAmount() {
        return amount;
    }

    public double getRate() {
        return rate;
    }
}
