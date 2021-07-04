package manaki.plugin.skybattle.config.model.battle;

public class ChestItemModel {


    private final String id;
    private final int amount;

    public ChestItemModel(String id, int amount) {
        this.id = id;
        this.amount = amount;
    }

    public String getId() {
        return id;
    }

    public int getAmount() {
        return amount;
    }

}
