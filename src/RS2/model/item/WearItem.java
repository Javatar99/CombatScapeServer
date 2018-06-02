package RS2.model.item;

import RS2.model.player.Client;
import RS2.model.player.PacketType;


/**
 * Wear Item
 **/
@SuppressWarnings("all")
public class WearItem implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		int inventorySlot = c.getInStream().readUnsignedByte();
		if(inventorySlot < 0 || inventorySlot >= c.inventory.getItemIds().length){
			c.sendMessage("Invalid slot: " + inventorySlot);
			return;
		}
		if (c.playerIndex > 0 || c.npcIndex > 0) {
			c.getCombat().resetPlayerAttack();
		}
		c.getItems().wearItem(inventorySlot);
	}
}