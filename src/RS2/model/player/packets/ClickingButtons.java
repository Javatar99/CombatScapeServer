package RS2.model.player.packets;

import RS2.model.item.GameItem;
import RS2.model.player.Client;
import RS2.model.player.PacketType;
import RS2.model.player.PlayerHandler;
import RS2.model.player.packets.tabs.MagicTab;

/**
 * Clicking most buttons
 **/
public class ClickingButtons implements PacketType, MagicTab {

    @Override
    public void processPacket(final Client c, int packetType, int packetSize) {
        int parentID = c.getInStream().readUnsignedWord();
        int childID = c.getInStream().readUnsignedWord();
        if (c.isDead)
            return;
        if (c.playerRights >= 3) {
            c.sendMessage("Parent ID: " + parentID + " Child ID: " + childID);
        }

        switch (parentID) {

            case MAGIC_TAB:
                teleportMagicTab(c, childID);
                break;

            case 5292:
                switch (childID) {
                    case 102:
                        c.getItems().bankInventory();
                        break;
                    case 92:
                        c.takeAsNote = true;
                        break;
                    case 93:
                        c.takeAsNote = false;
                        break;
                }
                break;
            case 2449:
                if (childID == 8) {
                    c.logout();
                }
                break;

            case 2492:
                if (c.currentOptionDialogue != null) {
                    if (c.currentOptionDialogue.doAction(childID)) {
                        c.getPA().closeAllWindows();
                    }
                }
                break;

            case 147:
                switch (childID) {
                    case 5:
                        c.isRunning2 = true;
                        break;
                    case 4:
                        c.isRunning2 = false;
                        break;
                }
                break;
            case 3323:
                if (childID == 94) {
                    Client ot = (Client) PlayerHandler.players[c.tradeWith];
                    if (ot == null) {
                        c.getTradeAndDuel().declineTrade();
                        c.sendMessage("Trade declined as the other player has disconnected.");
                        break;
                    }
                    c.getPA().sendFrame126("Waiting for other player...", 3431);
                    ot.getPA().sendFrame126("Other player has accepted", 3431);
                    c.goodTrade = true;
                    ot.goodTrade = true;

                    for (GameItem item : c.getTradeAndDuel().offeredItems) {
                        if (item.id > 0) {
                            if (ot.getItems().freeSlots() < c.getTradeAndDuel().offeredItems.size()) {
                                c.sendMessage(ot.playerName + " only has " + ot.getItems().freeSlots() + " free slots, please remove " + (c.getTradeAndDuel().offeredItems.size() - ot.getItems().freeSlots()) + " items.");
                                ot.sendMessage(c.playerName + " has to remove " + (c.getTradeAndDuel().offeredItems.size() - ot.getItems().freeSlots()) + " items or you could offer them " + (c.getTradeAndDuel().offeredItems.size() - ot.getItems().freeSlots()) + " items.");
                                c.goodTrade = false;
                                ot.goodTrade = false;
                                c.getPA().sendFrame126("Not enough inventory space...", 3431);
                                ot.getPA().sendFrame126("Not enough inventory space...", 3431);
                                break;
                            } else {
                                c.getPA().sendFrame126("Waiting for other player...", 3431);
                                ot.getPA().sendFrame126("Other player has accepted", 3431);
                                c.goodTrade = true;
                                ot.goodTrade = true;
                            }
                        }
                    }
                    if (c.inTrade && !c.tradeConfirmed && ot.goodTrade && c.goodTrade) {
                        c.tradeConfirmed = true;
                        if (ot.tradeConfirmed) {
                            c.getTradeAndDuel().confirmScreen();
                            ot.getTradeAndDuel().confirmScreen();
                            break;
                        }

                    }
                }
                break;

            case 3443:
                if (childID == 102) {
                    c.tradeAccepted = true;
                    Client ot1 = (Client) PlayerHandler.players[c.tradeWith];
                    if (ot1 == null) {
                        c.getTradeAndDuel().declineTrade();
                        c.sendMessage("Trade declined as the other player has disconnected.");
                        break;
                    }

                    if (c.inTrade && c.tradeConfirmed && ot1.tradeConfirmed && !c.tradeConfirmed2) {
                        c.tradeConfirmed2 = true;
                        if (ot1.tradeConfirmed2) {
                            c.acceptedTrade = true;
                            ot1.acceptedTrade = true;
                            c.getTradeAndDuel().giveItems();
                            ot1.getTradeAndDuel().giveItems();
                            break;
                        }
                        ot1.getPA().sendFrame126("Other player has accepted.", 3535);
                        c.getPA().sendFrame126("Waiting for other player...", 3535);
                    }
                }
                break;
            /* Player Options */
            case 74176:
                if (!c.mouseButton) {
                    c.mouseButton = true;
                    c.getPA().sendFrame36(500, 1);
                    c.getPA().sendFrame36(170, 1);
                } else if (c.mouseButton) {
                    c.mouseButton = false;
                    c.getPA().sendFrame36(500, 0);
                    c.getPA().sendFrame36(170, 0);
                }
                break;
            case 74184:
                if (!c.splitChat) {
                    c.splitChat = true;
                    c.getPA().sendFrame36(502, 1);
                    c.getPA().sendFrame36(287, 1);
                } else {
                    c.splitChat = false;
                    c.getPA().sendFrame36(502, 0);
                    c.getPA().sendFrame36(287, 0);
                }
                break;
            case 74180:
                if (!c.chatEffects) {
                    c.chatEffects = true;
                    c.getPA().sendFrame36(501, 1);
                    c.getPA().sendFrame36(171, 0);
                } else {
                    c.chatEffects = false;
                    c.getPA().sendFrame36(501, 0);
                    c.getPA().sendFrame36(171, 1);
                }
                break;
            case 74188:
                if (!c.acceptAid) {
                    c.acceptAid = true;
                    c.getPA().sendFrame36(503, 1);
                    c.getPA().sendFrame36(427, 1);
                } else {
                    c.acceptAid = false;
                    c.getPA().sendFrame36(503, 0);
                    c.getPA().sendFrame36(427, 0);
                }
                break;
            case 74192:
                if (!c.isRunning2) {
                    c.isRunning2 = true;
                    c.getPA().sendFrame36(504, 1);
                    c.getPA().sendFrame36(173, 1);
                } else {
                    c.isRunning2 = false;
                    c.getPA().sendFrame36(504, 0);
                    c.getPA().sendFrame36(173, 0);
                }
                break;
            case 74201://brightness1
                c.getPA().sendFrame36(505, 1);
                c.getPA().sendFrame36(506, 0);
                c.getPA().sendFrame36(507, 0);
                c.getPA().sendFrame36(508, 0);
                c.getPA().sendFrame36(166, 1);
                break;
            case 74203://brightness2
                c.getPA().sendFrame36(505, 0);
                c.getPA().sendFrame36(506, 1);
                c.getPA().sendFrame36(507, 0);
                c.getPA().sendFrame36(508, 0);
                c.getPA().sendFrame36(166, 2);
                break;

            case 74204://brightness3
                c.getPA().sendFrame36(505, 0);
                c.getPA().sendFrame36(506, 0);
                c.getPA().sendFrame36(507, 1);
                c.getPA().sendFrame36(508, 0);
                c.getPA().sendFrame36(166, 3);
                break;

            case 74205://brightness4
                c.getPA().sendFrame36(505, 0);
                c.getPA().sendFrame36(506, 0);
                c.getPA().sendFrame36(507, 0);
                c.getPA().sendFrame36(508, 1);
                c.getPA().sendFrame36(166, 4);
                break;
            case 74206://area1
                c.getPA().sendFrame36(509, 1);
                c.getPA().sendFrame36(510, 0);
                c.getPA().sendFrame36(511, 0);
                c.getPA().sendFrame36(512, 0);
                break;
            case 74207://area2
                c.getPA().sendFrame36(509, 0);
                c.getPA().sendFrame36(510, 1);
                c.getPA().sendFrame36(511, 0);
                c.getPA().sendFrame36(512, 0);
                break;
            case 74208://area3
                c.getPA().sendFrame36(509, 0);
                c.getPA().sendFrame36(510, 0);
                c.getPA().sendFrame36(511, 1);
                c.getPA().sendFrame36(512, 0);
                break;
            case 74209://area4
                c.getPA().sendFrame36(509, 0);
                c.getPA().sendFrame36(510, 0);
                c.getPA().sendFrame36(511, 0);
                c.getPA().sendFrame36(512, 1);
                break;
        }
        if (c.isAutoButton(parentID))
            c.assignAutocast(parentID);
    }
}