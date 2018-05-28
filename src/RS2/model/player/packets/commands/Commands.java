package RS2.model.player.packets.commands;

import RS2.GameEngine;
import RS2.Settings;
import RS2.model.player.Client;
import RS2.model.player.PacketType;
import RS2.model.player.packets.commands.impl.DeveloperCommands;
import RS2.util.Misc;

/**
 * Commands
 **/
public class Commands implements PacketType, CommandImplementation {

    private CommandImplementation implementation = this;

    @Override
    public void processPacket(Client c, int packetType, int packetSize) {
        String playerCommand = c.getInStream().readString().trim();
        Misc.println(c.playerName + " playerCommand: " + playerCommand);
        if (Settings.SERVER_DEBUG) {
            if (playerCommand.startsWith("/") && playerCommand.length() > 1) {
                if (c.clanId >= 0) {
                    System.out.println(playerCommand);
                    playerCommand = playerCommand.substring(1);
                    GameEngine.clanChat.playerMessageToClan(c.playerId,
                            playerCommand, c.clanId);
                } else {
                    if (c.clanId != -1)
                        c.clanId = -1;
                    c.sendMessage("You are not in a clan.");
                }
            }
        }

        switch (c.playerRights) {
            case 3:
                implementation = new DeveloperCommands();
                break;
            default:
                implementation = this;
        }
        try {
            if (!implementation.commandDefinitions(c, new CommandParser(" ", playerCommand))) {
                c.sendMessage("No Such Command.");
            }
        } catch (NoPrefixException | NotEnoughDataException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean commandDefinitions(Client c, CommandParser parser) {
        try {
            return adminCommands(c, parser) || normalCommands(c, parser);
        } catch (NoPrefixException e) {
            System.out.println(e.getMessage());
        } catch (NotEnoughDataException notEnoughData) {
            notEnoughData.printStackTrace();
        }
        return false;
    }
}
