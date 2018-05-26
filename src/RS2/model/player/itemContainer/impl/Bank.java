package RS2.model.player.itemContainer.impl;

import RS2.Settings;
import RS2.model.player.itemContainer.ItemContainer;

public class Bank extends ItemContainer {
    public Bank() {
        super(Settings.BANK_SIZE);
    }
}