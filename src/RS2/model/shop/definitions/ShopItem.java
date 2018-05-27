package RS2.model.shop.definitions;

public class ShopItem {

    private int itemId;
    private int originalStock;
    private int currentStock;
    private int price;
    private transient int respawnDelay;

    public ShopItem(int itemId, int originalStock, int currentStock, int price) {
        this.itemId = itemId;
        this.originalStock = originalStock;
        this.currentStock = currentStock;
        this.price = price;
        this.respawnDelay = 0;
    }

    public int getItemId() {
        return itemId;
    }

    public int getOriginalStock() {
        return originalStock;
    }

    public int getCurrentStock() {
        return currentStock;
    }

    public void setCurrentStock(int currentStock) {
        this.currentStock = currentStock;
    }

    public void setPrice(int price) {
        this.price = price;
    }

    public int getPrice() {
        return price;
    }

    public int getRespawnDelay() {
        return respawnDelay;
    }

    public void setRespawnDelay(int respawnDelay) {
        this.respawnDelay = respawnDelay;
    }
}
