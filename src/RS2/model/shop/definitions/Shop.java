package RS2.model.shop.definitions;

public class Shop {

    private String shopName;
    private int shopId;
    private ShopType shopType;
    private ShopItem[] shopItems;
    private ShopCurrency currency;

    public Shop(String shopName, int shopId, ShopType shopType, ShopItem... shopItems) {
        this.shopName = shopName;
        this.shopId = shopId;
        this.shopType = shopType;
        this.shopItems = shopItems;
        this.currency = ShopCurrency.COINS;
    }

    public String getShopName() {
        return shopName;
    }

    public int getShopId() {
        return shopId;
    }

    public boolean isShopType(ShopType type){
        for(ShopType t: ShopType.values()){
            if(t == type){
                return true;
            }
        }
        return false;
    }

    //O(shopItems.length)
    public ShopItem getShopItem(int itemId){
        for(ShopItem i: shopItems){
            if(i.getItemId() == itemId){
                return i;
            }
        }
        return null;
    }

    public ShopItem[] getShopItems() {
        return shopItems;
    }

    public ShopCurrency getCurrency() {
        return currency;
    }

    public void setCurrency(ShopCurrency currency) {
        this.currency = currency;
    }
}
