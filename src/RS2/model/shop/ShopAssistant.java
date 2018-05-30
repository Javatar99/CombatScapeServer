package RS2.model.shop;

import RS2.GameEngine;
import RS2.Settings;
import RS2.model.item.Item;
import RS2.model.player.Client;
import RS2.model.player.PlayerHandler;
import RS2.model.shop.definitions.Shop;
import RS2.model.shop.definitions.ShopItem;
import RS2.model.shop.definitions.ShopLoader;
import RS2.model.shop.definitions.ShopType;

@SuppressWarnings("all")
public class ShopAssistant {

    private Client c;

    public ShopAssistant(Client client) {
        this.c = client;
    }

    /**
     * Shops
     **/

    public void openShop(int ShopID) {
        if (ShopID < ShopLoader.shops.size()) {
            c.getItems().resetItems(3823);
            Shop shop = ShopLoader.shops.get(ShopID);
            resetShop(shop);
            c.isShopping = true;
            c.myShopId = ShopID;
            c.getPA().sendFrame248(3824, 3822);
            c.getPA().sendFrame126(shop.getShopName(), 3901);
        } else {
            c.sendMessage("Invalid Shop : " + ShopID);
            return;
        }
    }

    public void updateshop(int i) {
        resetShop(ShopLoader.shops.get(i));
    }

    public void resetShop(Shop shop) {
        synchronized (c) {
            int totalItems = 0;
            for (int i = 0; i < shop.getShopItems().length; i++) {
                if (shop.getShopItems()[i] != null) {
                    totalItems++;
                }
            }
            if (totalItems > ShopHandler.MaxShopItems) {
                totalItems = ShopHandler.MaxShopItems;
            }
            c.getOutStream().createFrameVarSizeWord(53);
            c.getOutStream().writeWord(3900);
            c.getOutStream().writeWord(totalItems);
            int totalCount = 0;
            for (int i = 0; i < shop.getShopItems().length; i++) {
                ShopItem[] items = shop.getShopItems();
                ShopItem item = items[i];
                int itemId = item.getItemId();
                if (item != null) {
                    if (item.getCurrentStock() > 254) {
                        c.getOutStream().writeByte(255);
                        c.getOutStream().writeDWord_v2(item.getCurrentStock());
                    } else {
                        c.getOutStream().writeByte(item.getCurrentStock());
                    }
                    if (itemId > Settings.ITEM_LIMIT || itemId < 0) {
                        itemId = Settings.ITEM_LIMIT;
                    }
                    c.getOutStream().writeWordBigEndianA((itemId + 1));
                    totalCount++;
                }
                if (totalCount > totalItems) {
                    break;
                }
            }
            c.getOutStream().endFrameVarSizeWord();
            c.flushOutStream();
        }
    }


    public double getItemShopValue(int ItemID, int Type, int fromSlot) {
        double ShopValue = 0;
        double Overstock = 0;
        double TotPrice = 0;
        final Shop shop = ShopLoader.shops.get(c.myShopId);
        ShopItem item = shop.getShopItem(ItemID);

        if (item != null) {
            ShopValue = item.getPrice();
            item.setPrice((int) ShopValue);
        }
        if (ShopValue <= 0) {
            ShopValue = getItemShopValue(ItemID);
        }
        TotPrice = ShopValue;
        if (shop.getCurrency().getSellToPlayerModifier() > 1) {
            TotPrice *= shop.getCurrency().getSellToPlayerModifier();
        }
        return TotPrice;
    }

    public int getItemShopValue(int itemId) {
        for (int i = 0; i < Settings.ITEM_LIMIT; i++) {
            if (GameEngine.itemHandler.ItemList[i] != null) {
                if (GameEngine.itemHandler.ItemList[i].itemId == itemId) {
                    return (int) GameEngine.itemHandler.ItemList[i].ShopValue;
                }
            }
        }
        return 0;
    }


    /**
     * buy item from shop (Shop Price)
     **/

    public void buyFromShopPrice(int removeId, int removeSlot) {
        int ShopValue = (int) Math.floor(getItemShopValue(removeId, 0, removeSlot));
        ShopValue *= 1.15;
        String ShopAdd = "";
        if (c.myShopId >= 17) {
            c.sendMessage(c.getItems().getItemName(removeId) + ": currently costs " + getSpecialItemValue(removeId) + " points.");
            return;
        }
        if (c.myShopId == 15) {
            c.sendMessage("This item current costs " + c.getItems().getUntradePrice(removeId) + " COINS.");
            return;
        }
        if (ShopValue >= 1000 && ShopValue < 1000000) {
            ShopAdd = " (" + (ShopValue / 1000) + "K)";
        } else if (ShopValue >= 1000000) {
            ShopAdd = " (" + (ShopValue / 1000000) + " million)";
        }
        c.sendMessage(c.getItems().getItemName(removeId) + ": currently costs " + ShopValue + " COINS" + ShopAdd);
    }

    public int getSpecialItemValue(int id) {
        switch (id) {
            case 6889:
            case 6914:
                return 200;
            case 6916:
            case 6918:
            case 6920:
            case 6922:
            case 6924:
                return 50;
            case 11663:
            case 11664:
            case 11665:
            case 8842:
                return 30;
            case 8839:
            case 8840:
                return 75;
            case 10499:
                return 20;
            case 8845:
                return 5;
            case 8846:
                return 10;
            case 8847:
                return 15;
            case 8848:
                return 20;
            case 8849:
            case 8850:
                return 25;
            case 7462:
                return 40;
            case 10551:
                return 100;
        }
        return 0;
    }


    /**
     * Sell item to shop (Shop Price)
     **/
    public void sellToShopPrice(int removeId, int removeSlot) {
        for (int i : Settings.ITEM_SELLABLE) {
            if (i == removeId) {
                c.sendMessage("You can't sell " + c.getItems().getItemName(removeId).toLowerCase() + ".");
                return;
            }
        }
        Shop shop = ShopLoader.shops.get(c.myShopId);
        if (isInShop(removeId, shop)) {
            c.sendMessage("You can't sell " + c.getItems().getItemName(removeId).toLowerCase() + " to this store.");
        } else {
            int ShopValue = (int) Math.floor(getItemShopValue(removeId, 1, removeSlot));
            String ShopAdd = "";
            if (ShopValue >= 1000 && ShopValue < 1000000) {
                ShopAdd = " (" + (ShopValue / 1000) + "K)";
            } else if (ShopValue >= 1000000) {
                ShopAdd = " (" + (ShopValue / 1000000) + " million)";
            }
            c.sendMessage(c.getItems().getItemName(removeId) + ": shop will buy for " + ShopValue + " COINS" + ShopAdd);
        }
    }

    private boolean isInShop(int removeId, Shop shop) {
        if (shop.isShopType(ShopType.CAN_DO_BOTH) || shop.isShopType(ShopType.CAN_SELL_TO_NPC)) {
            ShopItem item = shop.getShopItem(removeId);
            if (item != null) {
                return true;
            }
        }
        return false;
    }


    public boolean sellItem(int itemID, int fromSlot, int amount) {
        if (c.myShopId == 14)
            return false;
        for (int i : Settings.ITEM_SELLABLE) {
            if (i == itemID) {
                c.sendMessage("You can't sell " + c.getItems().getItemName(itemID).toLowerCase() + ".");
                return false;
            }
        }
        if (c.playerRights == 2 && !Settings.ADMIN_CAN_SELL_ITEMS) {
            c.sendMessage("Selling items as an admin has been disabled.");
            return false;
        }

        if (amount > 0 && itemID == (c.inventory.getItemIds()[fromSlot] - 1)) {
            Shop shop = ShopLoader.shops.get(c.myShopId);
            if (!isInShop(itemID, shop)) {
                c.sendMessage("You can't sell " + c.getItems().getItemName(itemID).toLowerCase() + " to this store.");
                return false;
            }

            if (amount > c.inventory.getItemAmounts()[fromSlot] && (Item.itemIsNote[(c.inventory.getItemIds()[fromSlot] - 1)] == true || Item.itemStackable[(c.inventory.getItemIds()[fromSlot] - 1)] == true)) {
                amount = c.inventory.getItemAmounts()[fromSlot];
            } else if (amount > c.getItems().getItemAmount(itemID) && Item.itemIsNote[(c.inventory.getItemIds()[fromSlot] - 1)] == false && Item.itemStackable[(c.inventory.getItemIds()[fromSlot] - 1)] == false) {
                amount = c.getItems().getItemAmount(itemID);
            }
            shop.getCurrency().sellToNpc(c, shop.getShopItem(itemID), amount);
            c.getItems().resetItems(3823);
            resetShop(shop);
            return true;
        }
        return true;
    }

    public boolean addShopItem(int itemID, int amount) {
        boolean Added = false;
        if (amount <= 0) {
            return false;
        }
        if (Item.itemIsNote[itemID] == true) {
            itemID = c.getItems().getUnnotedItem(itemID);
        }
        Shop shop = ShopLoader.shops.get(c.myShopId);
        ShopItem item = shop.getShopItem(itemID);
        if(item != null){
            item.setCurrentStock(item.getCurrentStock() + amount);
            Added = true;
        }
        
        if(Added == false){
            for (int i = 0; i < shop.getShopItems().length; i++) {
                if(shop.getShopItems()[i] == null){
                    shop.getShopItems()[i]
                            = new ShopItem(itemID, amount, amount, getItemShopValue(itemID));
                    break;
                }
            }
        }
        return true;
    }

    public boolean buyItem(int itemID, int fromSlot, int amount) {
        if (c.myShopId == 14) {
            skillBuy(itemID);
            return false;
        }
        if (amount > 0) {
            Shop shop = ShopLoader.shops.get(c.myShopId);
            ShopItem shopItem = shop.getShopItems()[fromSlot];
            if(shopItem != null){
                if (amount > shopItem.getCurrentStock()) {
                    amount = shopItem.getCurrentStock();
                }
            }
            shop.getCurrency().sellToPlayer(c, shopItem, amount);
            c.getItems().resetItems(3823);
            resetShop(ShopLoader.shops.get(c.myShopId));
            return true;
        }
        return false;
    }

    public void openSkillCape() {
        int capes = get99Count();
        if (capes > 1)
            capes = 1;
        else
            capes = 0;
        c.myShopId = 14;
        setupSkillCapes(capes, get99Count());
    }


    /*public int[][] skillCapes = {{0,9747,4319,2679},{1,2683,4329,2685},{2,2680,4359,2682},{3,2701,4341,2703},{4,2686,4351,2688},{5,2689,4347,2691},{6,2692,4343,2691},
                                {7,2737,4325,2733},{8,2734,4353,2736},{9,2716,4337,2718},{10,2728,4335,2730},{11,2695,4321,2697},{12,2713,4327,2715},{13,2725,4357,2727},
                                {14,2722,4345,2724},{15,2707,4339,2709},{16,2704,4317,2706},{17,2710,4361,2712},{18,2719,4355,2721},{19,2737,4331,2739},{20,2698,4333,2700}};*/
    public int[] skillCapes = {9747, 9753, 9750, 9768, 9756, 9759, 9762, 9801, 9807, 9783, 9798, 9804, 9780, 9795, 9792, 9774, 9771, 9777, 9786, 9810, 9765};

    public int get99Count() {
        int count = 0;
        for (int j = 0; j < c.playerLevel.length; j++) {
            if (c.getLevelForXP(c.playerXP[j]) >= 99) {
                count++;
            }
        }
        return count;
    }

    public void setupSkillCapes(int capes, int capes2) {
        synchronized (c) {
            c.getItems().resetItems(3823);
            c.isShopping = true;
            c.myShopId = 14;
            c.getPA().sendFrame248(3824, 3822);
            c.getPA().sendFrame126("Skillcape Shop", 3901);

            int TotalItems = 0;
            TotalItems = capes2;
            if (TotalItems > ShopHandler.MaxShopItems) {
                TotalItems = ShopHandler.MaxShopItems;
            }
            c.getOutStream().createFrameVarSizeWord(53);
            c.getOutStream().writeWord(3900);
            c.getOutStream().writeWord(TotalItems);
            int TotalCount = 0;
            for (int i = 0; i < 21; i++) {
                if (c.getLevelForXP(c.playerXP[i]) < 99)
                    continue;
                c.getOutStream().writeByte(1);
                c.getOutStream().writeWordBigEndianA(skillCapes[i] + 2);
                TotalCount++;
            }
            c.getOutStream().endFrameVarSizeWord();
            c.flushOutStream();
        }
    }

    public void skillBuy(int item) {
        int nn = get99Count();
        if (nn > 1)
            nn = 1;
        else
            nn = 0;
        for (int j = 0; j < skillCapes.length; j++) {
            if (skillCapes[j] == item || skillCapes[j] + 1 == item) {
                if (c.getItems().freeSlots() > 1) {
                    if (c.getItems().playerHasItem(995, 99000)) {
                        if (c.getLevelForXP(c.playerXP[j]) >= 99) {
                            c.getItems().deleteItem(995, c.getItems().getItemSlot(995), 99000);
                            c.getItems().addItem(skillCapes[j] + nn, 1);
                            c.getItems().addItem(skillCapes[j] + 2, 1);
                        } else {
                            c.sendMessage("You must have 99 in the skill of the cape you're trying to buy.");
                        }
                    } else {
                        c.sendMessage("You need 99k to buy this item.");
                    }
                } else {
                    c.sendMessage("You must have at least 1 inventory spaces to buy this item.");
                }
            }
        }
        c.getItems().resetItems(3823);
    }
}

