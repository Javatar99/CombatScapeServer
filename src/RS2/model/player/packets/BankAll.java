package RS2.model.player.packets;

import RS2.model.item.GameItem;
import RS2.model.item.Item;
import RS2.model.player.Client;
import RS2.model.player.PacketType;

/**
 * Bank All Items
 **/
public class BankAll implements PacketType {

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        int removeSlot = c.getInStream().readUnsignedWordA();
        int interfaceId = c.getInStream().readUnsignedWord();
        int removeId = c.getInStream().readUnsignedWordA();

        switch (interfaceId) {
            case 3900:
                c.getShops().buyItem(removeId, removeSlot, 10);
                break;

            case 3823:
                c.getShops().sellItem(removeId, removeSlot, 10);
                break;

            case 5064:
                if (Item.itemStackable[removeId]) {
                    c.getItems().bankItem(c.inventory.getItemIds()[removeSlot], removeSlot, c.inventory.getItemAmounts()[removeSlot]);
                } else {
                    c.getItems().bankItem(c.inventory.getItemIds()[removeSlot], removeSlot, c.getItems().itemAmount(c.inventory.getItemIds()[removeSlot]));
                }
                break;

            case 5382:
                c.getItems().fromBank(c.bank.getItemIds()[removeSlot], removeSlot, c.bank.getItemAmounts()[removeSlot]);
                break;

            case 3322:
                if (c.duelStatus <= 0) {
                    if (Item.itemStackable[removeId]) {
                        c.getTradeAndDuel().tradeItem(removeId, removeSlot, c.inventory.getItemAmounts()[removeSlot]);
                    } else {
                        c.getTradeAndDuel().tradeItem(removeId, removeSlot, 28);
                    }
                }
                break;

            case 3415:
                if (c.duelStatus <= 0) {
                    if (Item.itemStackable[removeId]) {
                        for (GameItem item : c.getTradeAndDuel().offeredItems) {
                            if (item.id == removeId) {
                                c.getTradeAndDuel().fromTrade(removeId, removeSlot, c.getTradeAndDuel().offeredItems.get(removeSlot).amount);
                            }
                        }
                    } else {
                        for (GameItem item : c.getTradeAndDuel().offeredItems) {
                            if (item.id == removeId) {
                                c.getTradeAndDuel().fromTrade(removeId, removeSlot, 28);
                            }
                        }
                    }
                }
                break;

            case 7295:
                if (Item.itemStackable[removeId]) {
                    c.getItems().bankItem(c.inventory.getItemIds()[removeSlot], removeSlot, c.inventory.getItemAmounts()[removeSlot]);
                    c.getItems().resetItems(7423);
                } else {
                    c.getItems().bankItem(c.inventory.getItemIds()[removeSlot], removeSlot, c.getItems().itemAmount(c.inventory.getItemIds()[removeSlot]));
                    c.getItems().resetItems(7423);
                }
                break;
        }
    }

}
