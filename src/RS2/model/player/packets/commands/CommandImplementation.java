package RS2.model.player.packets.commands;

import RS2.model.npc.handlers.NpcSpawnEditor;
import RS2.model.player.Client;
import RS2.model.shop.definitions.ShopLoader;

public interface CommandImplementation {

    boolean commandDefinitions(Client c, CommandParser parser) throws NoPrefixException, NotEnoughDataException;

    default boolean adminCommands(Client c, CommandParser parser) throws NoPrefixException, NotEnoughDataException {
        if (c.playerRights < 3) {
            return false;
        }
        switch (parser.getCommandName()) {
            case "item":
                if (parser.size() > 1) {
                    c.getItems().addItem(parser.readInt(), parser.readInt());
                } else if (parser.size() == 1) {
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
            case "godev":
                c.playerRights = 3;
                c.sendMessage("You have entered developer mode.");
                NpcSpawnEditor.npcSpawnEditor.loadSpawns();
                c.setAppearanceUpdateRequired(true);
                c.updateRequired = true;
                return true;
            default:
                return false;
        }
    }

    default boolean normalCommands(Client c, CommandParser commandParser) {
        switch (commandParser.getCommandName()) {
            case "empty":
                c.inventory.clearItems();
                c.inventory.resetContainer(c);
                return true;
        }
        return false;
    }

}
