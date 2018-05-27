package RS2.model.player.packets;

import RS2.model.player.Client;
import RS2.model.player.PacketType;
import RS2.model.shop.definitions.ShopItem;
import RS2.model.shop.definitions.ShopLoader;

/**
 * Bank X Items
 **/
public class BankX1 implements PacketType {

	public static final int PART1 = 135;
	public static final int	PART2 = 208;
	public int XremoveSlot, XinterfaceID, XremoveID, Xamount;
	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		if (packetType == 135) {
			c.xRemoveSlot = c.getInStream().readSignedWordBigEndian();
			c.xInterfaceId = c.getInStream().readUnsignedWordA();
			c.xRemoveId = c.getInStream().readSignedWordBigEndian();
		}
		if (c.xInterfaceId == 3900) {
			ShopItem item = ShopLoader.shops.get(c.myShopId).getShopItem(c.xRemoveId);
			if(item != null){
				c.getShops().buyItem(c.xRemoveId, c.xRemoveSlot, item.getOriginalStock());//Buy Max
				c.xRemoveSlot = 0;
				c.xInterfaceId = 0;
				c.xRemoveId = 0;
			}
			return;
		}

		if(packetType == PART1) {
			synchronized(c) {
				c.getOutStream().createFrame(27);
			}			
		}
	
	}
}
