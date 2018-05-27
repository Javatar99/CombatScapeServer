package RS2.model.shop.definitions;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class ShopLoader {

    public static List<Shop> shops = new ArrayList<>();

    public static void loadShop(){
        try {
            shops = new Gson().fromJson(new FileReader("Data/cfg/shops.json"),
                    new TypeToken<List<Shop>>(){}.getType());

            for(Shop shop: shops){
                switch (shop.getShopId()){

                    default:
                        shop.setCurrency(ShopCurrency.COINS);
                }
            }

        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
