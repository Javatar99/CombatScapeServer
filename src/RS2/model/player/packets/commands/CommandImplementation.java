package RS2.model.player.packets.commands;

import RS2.GameEngine;
import RS2.model.item.definitions.ItemAnimationDefinition;
import RS2.model.item.definitions.ItemDefinition;
import RS2.model.npc.handlers.NPCHandler;
import RS2.model.npc.handlers.NpcSpawnEditor;
import RS2.model.player.Client;
import RS2.model.player.dialogues.OptionDialogue;
import RS2.model.shop.definitions.ShopLoader;

import java.util.Arrays;

/**
 * @author david (Javatar)
 */

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
            case "reloadnpcs":
                GameEngine.npcHandler = new NPCHandler();
                c.sendMessage("Reloaded Npcs.");
                return true;

            case "tele":
                if(parser.size() > 2){
                    c.getPA().startTeleport2(parser.readInt(), parser.readInt(), parser.readInt());
                } else if(parser.size() == 2){
                    c.getPA().startTeleport2(parser.readInt(), parser.readInt(), c.heightLevel);
                } else {
                    c.sendMessage("tele X Y H");
                    c.sendMessage("tele X Y");
                }
                return true;
            case "dialogue":
                OptionDialogue dialogue = new OptionDialogue(c);
                dialogue.setOption("Test1", 0, p -> {
                    p.sendMessage("Test 1");
                    return true;
                });
                dialogue.setOption("Test2", 1, p -> {
                    p.sendMessage("Test 2");
                    return true;
                });
                dialogue.setOption("Test3", 2, p -> {
                    p.sendMessage("Test 3");
                    return true;
                });
                dialogue.setOption("Test4", 3, p -> {
                    p.sendMessage("Test 4");
                    return true;
                });
                dialogue.setOption("Next", 4, p -> {
                    dialogue.setOption("Test 5", 0, t -> true);
                    dialogue.setOption("Test 6", 1, t -> true);
                    dialogue.setOption("Test 7", 2, t -> true);
                    dialogue.setOption("Test 8", 3, t -> true);
                    dialogue.setOption("Test 9", 4, t -> true);
                    return false;
                });
                dialogue.show();
                return true;

            case "godev":
                c.playerRights = 3;
                c.sendMessage("You have entered developer mode.");
                NpcSpawnEditor.npcSpawnEditor.loadSpawns();
                c.setAppearanceUpdateRequired(true);
                c.updateRequired = true;
                return true;
            case "master":
                Arrays.fill(c.playerSkills1.getPlayerLevel(), 99);
                Arrays.fill(c.playerSkills1.getPlayerXP(), 200000000);
                for (int i = 0; i < c.playerSkills1.getPlayerLevel().length; i++) {
                    c.getPA().refreshSkill(i);
                }
                return true;
            case "unmaster":
                Arrays.fill(c.playerSkills1.getPlayerLevel(), 1);
                Arrays.fill(c.playerSkills1.getPlayerXP(), 0);
                for (int i = 0; i < c.playerSkills1.getPlayerLevel().length; i++) {
                    c.getPA().refreshSkill(i);
                }
                return true;
            case "setlvl":
                int id = parser.readInt();
                int level = parser.readInt();
                c.playerSkills1.getPlayerLevel()[id] = level;
                c.playerSkills1.getPlayerXP()[id] = c.getPA().getXPForLevel(level) + 1;
                c.getPA().refreshSkill(id);
                return true;
            case "anim":
                c.startAnimation(parser.readInt());
                return true;
            case "dumpolditems":
                ItemDefinition.convertOldToNewDefinitions(c);
                return true;
            case "dumpoldanim":
                ItemAnimationDefinition.saveOldSystem(c);
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
