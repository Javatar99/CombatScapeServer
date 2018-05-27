package RS2.model.shop.definitions;

import RS2.model.player.Client;
import RS2.model.shop.definitions.currencys.CurrencyItem;

public interface ShopCurrency {

    ShopCurrency COINS = new CurrencyItem(995);

    void sellToNpc(Client c, ShopItem item, int amount);
    void sellToPlayer(Client c, ShopItem item, int amount);

    default double getSellToNpcModifier(){
        return 1;
    }

    default double getSellToPlayerModifier(){
        return 1;
    }
}
