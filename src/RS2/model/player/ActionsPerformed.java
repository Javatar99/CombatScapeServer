package RS2.model.player;

public class ActionsPerformed {

	private Client c;

	public ActionsPerformed(Client Client) {
		this.c = Client;
	}

	public void firstClickObject(int objectType, int obX, int obY) {
		c.clickObjectType = 0;
		c.actionTimer = 4;
		if (c.actionTimer > 0) {
			return;
		}
		c.actionTimer = 4;
		switch (objectType) {
		}
	}

	public void secondClickObject(int objectType, int obX, int obY) {
		c.clickObjectType = 0;
		switch (objectType) {
		}
	}

	public void thirdClickObject(int objectType, int obX, int obY) {
		c.clickObjectType = 0;
		c.sendMessage("Object type: " + objectType);
		switch (objectType) {
		}
	}

	public void firstClickNpc(int npcType) {
		c.clickNpcType = 0;
		c.npcClickIndex = 0;
		switch (npcType) {

			case 1784:
				c.getShops().openShop(2);
				break;
			case 225:
				c.getShops().openShop(5);
				break;

		}
	}

	public void secondClickNpc(int npcType) {
		c.clickNpcType = 0;
		c.npcClickIndex = 0;
		switch (npcType) {
			case 1784:// test shop
				c.getShops().openShop(0);
				break;
			case 461: //Magic store owner
				c.getShops().openShop(1);
				break;
			case 225:
				c.getShops().openShop(6);
				break;
		}
	}

	public void thirdClickNpc(int npcType) {
		c.clickNpcType = 0;
		c.npcClickIndex = 0;
		switch (npcType) {
			case 1784:
				c.getShops().openShop(3);
				break;
			case 225:
				c.getShops().openShop(7);
				break;
		}
	}

	public void fourthClickNpc(int npcType){
		c.clickNpcType = 0;
		c.npcClickIndex = 0;
		switch (npcType) {
			case 1784:
				c.getShops().openShop(4);
				break;
		}
	}
}