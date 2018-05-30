package RS2.model.player.itemContainer;

import RS2.model.player.Client;

/**
 * @author david (Javatar)
 */

public abstract class ItemContainer {

    private int[] itemIds;
    private int[] itemAmounts;

    public ItemContainer(int size) {
        this.itemIds = new int[size];
        this.itemAmounts = new int[size];
    }

    public void clearItems(){
        for (int i = 0; i < this.itemIds.length; i++) {
            this.itemIds[i] = 0;
            this.itemAmounts[i] = 0;
        }
    }

    public int[] getItemIds() {
        return itemIds;
    }

    public int[] getItemAmounts() {
        return itemAmounts;
    }

    public abstract void resetContainer(Client c);
}
