package RS2.model.player.packets;

import RS2.model.player.Client;
import RS2.model.player.PacketType;

/**
 * Clicking an item, bury bone, eat food etc
 **/
@SuppressWarnings("all")
public class ClickItem implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        int itemSlot = c.getInStream().readUnsignedByte();
        final int itemid = c.inventory.getItemIds()[itemSlot] - 1;
        final int amount = c.inventory.getItemAmounts()[itemSlot];
        if (c.playerRights == 3) {
            c.sendMessage("First Clicked Item: " + itemid + " In Slot: " + itemSlot);
        }

        switch (itemid) {

            case 3827:
                c.getItems().deleteItemAmount(itemid, 1);
                if(c.getItems().freeSlots() >= 13){
                    for (int i = 554; i <= 559; i++) {
                        c.getItems().addItem(i, 500);
                    }
                    c.getItems().addItem(562, 500);
                    c.getItems().addItem(563, 500);
                    c.getItems().addItem(565, 500);
                    c.getItems().addItem(561, 500);
                    c.getItems().addItem(560, 500);
                    c.getItems().addItem(566, 500);
                } else {
                    c.sendMessage("You need 13 free inventory spaces.");
                }
                break;

        }

    }
}
