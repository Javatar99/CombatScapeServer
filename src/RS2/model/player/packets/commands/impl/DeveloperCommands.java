package RS2.model.player.packets.commands.impl;

import RS2.model.npc.handlers.NpcSpawnDefinition;
import RS2.model.npc.handlers.NpcSpawnEditor;
import RS2.model.player.Client;
import RS2.model.player.packets.commands.CommandImplementation;
import RS2.model.player.packets.commands.CommandParser;
import RS2.model.player.packets.commands.NoPrefixException;
import RS2.model.player.packets.commands.NotEnoughDataException;

public class DeveloperCommands implements CommandImplementation {
    @Override
    public boolean commandDefinitions(Client c, CommandParser parser) throws NoPrefixException, NotEnoughDataException {
        switch (parser.getCommandName()){
            case "exit":
            case "ex":
            case "exti":
                c.playerRights = 4;
                c.setAppearanceUpdateRequired(true);
                c.updateRequired = true;
                c.sendMessage("All Editors reset.");
                NpcSpawnEditor.npcSpawnEditor.restartEditor();
                return true;
            case "addnpc":
                final int npcId = parser.readInt();
                final int walkingType = parser.readInt();
                final int hp = parser.readInt();
                final int attack = parser.readInt();
                final int defence = parser.readInt();
                NpcSpawnEditor.npcSpawnEditor.addSpawn(
                        new NpcSpawnDefinition(npcId, c.absX, c.absY, c.heightLevel, walkingType, hp, hp,
                                attack, defence)
                );
                return true;
            case "undospawn":
                NpcSpawnEditor.npcSpawnEditor.undoSpawn();
                return true;
            case "finish":
                NpcSpawnEditor.npcSpawnEditor.finish();
                return true;
            case "save":
                NpcSpawnEditor.npcSpawnEditor.saveAllNewSpawns();
                return true;
            case "show":
                NpcSpawnEditor.npcSpawnEditor.showNPCS();
                return true;
            case "despawn":
                NpcSpawnEditor.npcSpawnEditor.deleteSpawnedNpcs();
                return true;
            case "debug":
                NpcSpawnEditor.npcSpawnEditor.checkEditor();
                return true;
            case "help":
                c.sendMessage("Command: addnpc npcId walkType hp attack defence");
                return true;
        }
        return false;
    }
}
