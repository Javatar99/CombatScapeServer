package RS2.model.player.itemContainer.impl;

import RS2.model.player.Client;
import RS2.model.player.itemContainer.ItemContainer;

public class Inventory extends ItemContainer {

    public Inventory() {
        super(28);
    }

    @Override
    public void resetContainer(Client c) {
        c.getItems().resetItems(3214);
    }
}