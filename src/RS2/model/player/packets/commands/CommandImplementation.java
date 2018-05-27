package RS2.model.player.packets.commands;

import RS2.model.player.Client;
import RS2.model.shop.definitions.ShopLoader;

public interface CommandImplementation {

    default boolean adminCommands(Client c, CommandParser parser) throws NoPrefixException {
        if(c.playerRights != 2 && c.playerRights != 3){
            return false;
        }
        switch (parser.getCommandName()){
            case "item":
                if(parser.size() > 2){
                    c.getItems().addItem(parser.readInt(), parser.readInt());
                } else if(parser.size() == 1) {
                    c.getItems().addItem(parser.readInt(), 1);
                }
                return true;
            case "bank":
                c.getPA().openUpBank();
                return true;
            case "reloadshops":
                ShopLoader.loadShop();
                c.sendMessage("Shops reloaded : " + ShopLoader.shops.size());
                return true;
        }

        return false;
    }

    default void normalCommands(Client c, CommandParser commandParser){
        switch (commandParser.getCommandName()){

            case "empty":
                c.inventory.clearItems();
                c.inventory.resetContainer(c);
                break;
            default:
                c.sendMessage("No such command : " + commandParser.getCommandName());
                break;
        }
    }

}
