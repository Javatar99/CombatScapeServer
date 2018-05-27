package RS2.model.shop.definitions.currencys;

import RS2.model.player.Client;
import RS2.model.shop.definitions.ShopCurrency;
import RS2.model.shop.definitions.ShopItem;

public class CurrencyItem implements ShopCurrency {

    private final int currencyItemId;

    public CurrencyItem(int currencyItemId) {
        this.currencyItemId = currencyItemId;
    }

    @Override
    public void sellToNpc(Client c, ShopItem item, int amount) {
        final int sellPrice = (int) ((item.getPrice() * 0.25) + item.getPrice()) * amount;
        if(item.getCurrentStock() + amount >= item.getOriginalStock()){
            c.sendMessage("The store has no more room for that " + c.getItems().getItemName(item.getItemId()));
            return;
        }
        if(c.getItems().addItem(this.currencyItemId, sellPrice)){
            c.getItems().deleteItemAmount(item.getItemId(), amount);
        }
    }

    @Override
    public void sellToPlayer(Client c, ShopItem item, int amount) {
        if(c.getItems().playerHasItem(this.currencyItemId, item.getPrice() * amount) && c.getItems().addItem(item.getItemId(), amount)){
            c.getItems().deleteItemAmount(this.currencyItemId, item.getPrice() * amount);
        } else {
            c.sendMessage("You need " + (item.getPrice() * amount) + " " + c.getItems().getItemName(this.currencyItemId)
            + " to buy " + c.getItems().getItemName(item.getItemId()));
        }
    }
}
