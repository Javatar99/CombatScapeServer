package RS2.model.player.itemContainer;

public abstract class ItemContainer {

    private int[] itemIds;
    private int[] itemAmounts;

    public ItemContainer(int size) {
        this.itemIds = new int[size];
        this.itemAmounts = new int[size];
    }

    public int[] getItemIds() {
        return itemIds;
    }

    public int[] getItemAmounts() {
        return itemAmounts;
    }
}
