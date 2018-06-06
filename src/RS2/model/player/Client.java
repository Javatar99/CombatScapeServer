package RS2.model.player;

import RS2.GameEngine;
import RS2.Settings;
import RS2.model.item.ItemAssistant;
import RS2.model.shop.ShopAssistant;
import RS2.model.skilling.skills.Skill;
import RS2.net.Packet;
import RS2.net.Packet.Type;
import RS2.util.Misc;
import RS2.util.Stream;
import org.jboss.netty.buffer.ChannelBuffers;
import org.jboss.netty.channel.Channel;

import java.util.PriorityQueue;
import java.util.Queue;

public class Client extends Player {

    public byte buffer[];
    public Stream inStream, outStream;
    private Channel session;
    private ItemAssistant itemAssistant = new ItemAssistant(this);
    private ShopAssistant shopAssistant = new ShopAssistant(this);
    private TradeAndDuel tradeAndDuel = new TradeAndDuel(this);
    private PlayerAssistant playerAssistant = new PlayerAssistant(this);
    private CombatAssistant combatAssistant = new CombatAssistant(this);
    private ActionsPerformed actionHandler = new ActionsPerformed(this);
    private PlayerKilling playerKilling = new PlayerKilling(this);
    private DialogueHandler dialogueHandler = new DialogueHandler(this);
    private final Queue<Packet> queuedPackets = new PriorityQueue<>(
            (o1, o2) -> {
                if (o1.getOpcode() == 41 || o1.getOpcode() == 122)
                    return -1;
                else if (o1.getOpcode() == o2.getOpcode())
                    return 0;
                return 1;
            }
    );

    public Client(Channel s, int _playerId) {
        super(_playerId);
        this.session = s;
        synchronized (this) {
            outStream = new Stream(new byte[Settings.BUFFER_SIZE]);
            outStream.currentOffset = 0;
            inStream = new Stream(new byte[Settings.BUFFER_SIZE]);
            inStream.currentOffset = 0;
            buffer = new byte[Settings.BUFFER_SIZE];
        }
    }

    public void flushOutStream() {
        if (!session.isConnected() || disconnected
                || outStream.currentOffset == 0)
            return;
        byte[] temp = new byte[outStream.currentOffset];
        System.arraycopy(outStream.buffer, 0, temp, 0, temp.length);
        Packet packet = new Packet(-1, Type.FIXED,
                ChannelBuffers.wrappedBuffer(temp));
        session.write(packet);
        outStream.currentOffset = 0;
    }

    public void sendClan(String name, String message, String clan, int rights) {
        outStream.createFrameVarSizeWord(217);
        outStream.writeString(name);
        outStream.writeString(message);
        outStream.writeString(clan);
        outStream.writeWord(rights);
        outStream.endFrameVarSize();
    }

    public static final int PACKET_SIZES[] = {0, 0, 0, 1, -1, 0, 0, 0, 0, 0, // 0
            0, 0, 0, 0, 8, 0, 6, 2, 2, 0, // 10
            0, 2, 0, 6, 0, 12, 0, 0, 0, 0, // 20
            0, 0, 0, 0, 0, 8, 4, 0, 0, 2, // 30
            2, 6, 0, 6, 0, -1, 0, 0, 0, 0, // 40
            0, 0, 0, 12, 0, 0, 0, 8, 8, 12, // 50
            8, 8, 0, 0, 0, 0, 0, 0, 0, 0, // 60
            6, 0, 2, 2, 8, 6, 0, -1, 0, 6, // 70
            0, 0, 0, 0, 0, 1, 4, 6, 0, 0, // 80
            0, 0, 0, 0, 0, 3, 0, 0, -1, 0, // 90
            0, 13, 0, -1, 0, 0, 0, 0, 0, 0,// 100
            0, 0, 0, 0, 0, 0, 0, 6, 0, 0, // 110
            1, 0, 6, 0, 0, 0, -1, 0, 2, 6, // 120
            0, 4, 6, 8, 0, 6, 0, 0, 0, 2, // 130
            0, 0, 0, 0, 0, 6, 0, 0, 0, 0, // 140
            0, 0, 1, 2, 0, 2, 6, 0, 0, 0, // 150
            0, 0, 0, 0, -1, -1, 0, 0, 0, 0,// 160
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, // 170
            0, 8, 0, 3, 0, 4, 0, 0, 8, 1, // 180
            0, 0, 12, 0, 0, 0, 0, 0, 0, 0, // 190
            2, 0, 0, 0, 0, 0, 0, 0, 4, 0, // 200
            4, 0, 0, 0, 7, 8, 0, 0, 10, 0, // 210
            0, 0, 0, 0, 0, 0, -1, 0, 6, 0, // 220
            1, 0, 0, 0, 6, 0, 6, 8, 1, 0, // 230
            0, 4, 0, 0, 0, 0, -1, 0, -1, 4,// 240
            0, 0, 6, 6, 0, 0, 0 // 250
    };

    static {
        PACKET_SIZES[122] = 1;
        PACKET_SIZES[41] = 1;
    }

    @Override
    public void destruct() {
        if (session == null)
            return;
        if (clanId >= 0)
            GameEngine.clanChat.leaveClan(playerId, clanId);
        Misc.println("[DEREGISTERED]: " + playerName + "");
        disconnected = true;
        session.close();
        session = null;
        inStream = null;
        outStream = null;
        isActive = false;
        buffer = null;
        super.destruct();
    }

    public void sendMessage(String s) {
        // synchronized (this) {
        if (getOutStream() != null) {
            outStream.createFrameVarSize(253);
            outStream.writeString(s);
            outStream.endFrameVarSize();
        }

    }

    public void setSidebarInterface(int menuId, int form) {
        // synchronized (this) {
        if (getOutStream() != null) {
            outStream.createFrame(71);
            outStream.writeWord(form);
            outStream.writeByteA(menuId);
        }

    }

    public void initialize() {
        //synchronized (this) {
        outStream.createFrame(249);
        outStream.writeByteA(1);        // 1 for members, zero for free
        outStream.writeWordBigEndianA(playerId);
        for (int j = 0; j < PlayerHandler.players.length; j++) {
            if (j == playerId)
                continue;
            if (PlayerHandler.players[j] != null) {
                if (PlayerHandler.players[j].playerName.equalsIgnoreCase(playerName))
                    disconnected = true;
            }
        }
        this.skills.refreshAll();
        for (int p = 0; p < PRAYER.length; p++) { // reset prayer glows
            prayerActive[p] = false;
            getPA().sendFrame36(PRAYER_GLOW[p], 0);
        }
        //if (playerName.equalsIgnoreCase("Sanity")) {
        //getPA().sendCrashFrame();
        //}
        getPA().handleWeaponStyle();
        accountFlagged = getPA().checkForFlags();
        //getPA().sendFrame36(43, fightMode-1);
        getPA().sendFrame36(108, 0);//resets autocast button
        getPA().sendFrame36(172, 1);
        getPA().sendFrame107(); // reset screen
        getPA().setChatOptions(0, 0, 0); // reset private messaging options
        setSidebarInterface(1, 3917);
        setSidebarInterface(2, 638);
        setSidebarInterface(3, 3213);
        setSidebarInterface(4, 1644);
        setSidebarInterface(5, 5608);
        if (playerMagicBook == 0) {
            setSidebarInterface(6, 1151); //modern
        } else {
            setSidebarInterface(6, 12855); // ancient
        }
        setSidebarInterface(7, 18128);
        setSidebarInterface(8, 5065);
        setSidebarInterface(9, 5715);
        setSidebarInterface(10, 2449);
        //setSidebarInterface(11, 4445); // wrench tab
        setSidebarInterface(11, 904); // wrench tab
        setSidebarInterface(12, 147); // run tab
        setSidebarInterface(13, -1);
        setSidebarInterface(0, 2423);
        sendMessage("Welcome to " + Settings.SERVER_NAME);
        getPA().showOption(5, 0, "Follow", 4);
        getPA().showOption(4, 0, "Trade With", 3);
        getItems().resetItems(3214);
        getItems().sendWeapon(equipment.getItemIds()[playerWeapon], getItems().getItemName(equipment.getItemIds()[playerWeapon]));
        getItems().resetBonus();
        getItems().getBonus();
        getItems().writeBonus();
        getItems().setEquipment(equipment.getItemIds()[playerHat], 1, playerHat);
        getItems().setEquipment(equipment.getItemIds()[playerCape], 1, playerCape);
        getItems().setEquipment(equipment.getItemIds()[playerAmulet], 1, playerAmulet);
        getItems().setEquipment(equipment.getItemIds()[playerArrows], equipment.getItemAmounts()[playerArrows], playerArrows);
        getItems().setEquipment(equipment.getItemIds()[playerChest], 1, playerChest);
        getItems().setEquipment(equipment.getItemIds()[playerShield], 1, playerShield);
        getItems().setEquipment(equipment.getItemIds()[playerLegs], 1, playerLegs);
        getItems().setEquipment(equipment.getItemIds()[playerHands], 1, playerHands);
        getItems().setEquipment(equipment.getItemIds()[playerFeet], 1, playerFeet);
        getItems().setEquipment(equipment.getItemIds()[playerRing], 1, playerRing);
        getItems().setEquipment(equipment.getItemIds()[playerWeapon], equipment.getItemAmounts()[playerWeapon], playerWeapon);
        getCombat().getPlayerAnimIndex();
        getPA().logIntoPM();
        getItems().addSpecialBar(equipment.getItemIds()[playerWeapon]);
        saveTimer = Settings.SAVE_TIMER;
        saveCharacter = true;
        Misc.println("[REGISTERED]: " + playerName + "");
        handler.updatePlayer(this, outStream);
        handler.updateNPC(this, outStream);
        flushOutStream();
        getPA().clearClanChat();
        getPA().resetFollow();
        if (addStarter)
            getPA().addStarter();
        if (autoRet == 1)
            getPA().sendFrame36(172, 1);
        else
            getPA().sendFrame36(172, 0);
    }


    @Override
    public void updateEntities() {
        // synchronized (this) {
        handler.updatePlayer(this, outStream);
        handler.updateNPC(this, outStream);
        flushOutStream();

    }

    public void logout() {
        // synchronized (this) {
        if (System.currentTimeMillis() - logoutDelay > 10000) {
            outStream.createFrame(109);
            properLogout = true;
        } else {
            sendMessage("You must wait 10 seconds from being out of combat to logout.");
        }

    }

    public int packetSize = 0, packetType = -1;

    @Override
    public void process() {
        if (System.currentTimeMillis() - specDelay > Settings.INCREASE_SPECIAL_AMOUNT) {
            specDelay = System.currentTimeMillis();
            if (specAmount < 10) {
                specAmount += .5;
                if (specAmount > 10)
                    specAmount = 10;
                getItems().addSpecialBar(equipment.getItemIds()[playerWeapon]);
            }
        }
        if (followId > 0) {
            getPA().followPlayer();
        } else if (followId2 > 0) {
            getPA().followNpc();
        }
        getCombat().handlePrayerDrain();
        if (System.currentTimeMillis() - singleCombatDelay > 3300) {
            underAttackBy = 0;
        }
        if (System.currentTimeMillis() - singleCombatDelay2 > 3300) {
            underAttackBy2 = 0;
        }

        if (System.currentTimeMillis() - restoreStatsDelay > 60000) {
            restoreStatsDelay = System.currentTimeMillis();
            this.skills.forEach(Skill::restoreStat);
        }

        if (inWild()) {
            int modY = absY > 6400 ? absY - 6400 : absY;
            wildLevel = (((modY - 3520) / 8) + 1);
            getPA().walkableInterface(197);
            if (Settings.SINGLE_AND_MULTI_ZONES) {
                getPA().sendFrame126("@yel@Level: " + wildLevel, 199);
            } else {
                getPA().multiWay(-1);
                getPA().sendFrame126("@yel@Level: " + wildLevel, 199);
            }
            getPA().showOption(3, 0, "Attack", 1);
        }
        if (!hasMultiSign && inMulti()) {
            hasMultiSign = true;
            getPA().multiWay(1);
        }
        if (hasMultiSign && !inMulti()) {
            hasMultiSign = false;
            getPA().multiWay(-1);
        }
        if (skullTimer > 0) {
            skullTimer--;
            if (skullTimer == 1) {
                isSkulled = false;
                attackedPlayers.clear();
                headIconPk = -1;
                skullTimer = -1;
                getPA().requestUpdates();
            }
        }
        if (isDead && respawnTimer == -6) {
            getPA().applyDead();
        }
        if (respawnTimer == 7) {
            respawnTimer = -6;
            getPA().giveLife();
        } else if (respawnTimer == 12) {
            respawnTimer--;
            startAnimation(0x900);
            poisonDamage = -1;
        }
        if (respawnTimer > -6) {
            respawnTimer--;
        }
        if (freezeTimer > -6) {
            freezeTimer--;
            if (frozenBy > 0) {
                if (PlayerHandler.players[frozenBy] == null) {
                    freezeTimer = -1;
                    frozenBy = -1;
                } else if (!goodDistance(absX, absY,
                        PlayerHandler.players[frozenBy].absX,
                        PlayerHandler.players[frozenBy].absY, 20)) {
                    freezeTimer = -1;
                    frozenBy = -1;
                }
            }
        }

        if (hitDelay > 0) {
            hitDelay--;
        }

        if (teleTimer > 0) {
            teleTimer--;
            if (!isDead) {
                if (teleTimer == 1 && newLocation > 0) {
                    teleTimer = 0;
                    getPA().changeLocation();
                }
                if (teleTimer == 5) {
                    teleTimer--;
                    getPA().processTeleport();
                }
                if (teleTimer == 9 && teleGfx > 0) {
                    teleTimer--;
                    gfx100(teleGfx);
                }
            } else {
                teleTimer = 0;
            }
        }

        if (hitDelay == 1) {
            if (oldNpcIndex > 0) {
                getCombat().delayedHit(oldNpcIndex);
            }
            if (oldPlayerIndex > 0) {
                getCombat().playerDelayedHit(oldPlayerIndex);
            }
        }

        if (attackTimer > 0) {
            attackTimer--;
        }

        if (attackTimer == 1) {
            if (npcIndex > 0 && clickNpcType == 0) {
                getCombat().attackNpc(npcIndex);
            }
            if (playerIndex > 0) {
                getCombat().attackPlayer(playerIndex);
            }
        } else if (attackTimer <= 0 && (npcIndex > 0 || playerIndex > 0)) {
            if (npcIndex > 0) {
                attackTimer = 0;
                getCombat().attackNpc(npcIndex);
            } else {
                attackTimer = 0;
                getCombat().attackPlayer(playerIndex);
            }
        }

        if (inTrade && tradeResetNeeded) {
            Client o = (Client) PlayerHandler.players[tradeWith];
            if (o != null) {
                if (o.tradeResetNeeded) {
                    getTradeAndDuel().resetTrade();
                    o.getTradeAndDuel().resetTrade();
                }
            }
        }
    }

    public synchronized Stream getInStream() {
        return inStream;
    }

    public synchronized int getPacketType() {
        return packetType;
    }

    public synchronized int getPacketSize() {
        return packetSize;
    }

    public synchronized Stream getOutStream() {
        return outStream;
    }

    public ItemAssistant getItems() {
        return itemAssistant;
    }

    public PlayerAssistant getPA() {
        return playerAssistant;
    }

    public DialogueHandler getDH() {
        return dialogueHandler;
    }

    public ShopAssistant getShops() {
        return shopAssistant;
    }

    public TradeAndDuel getTradeAndDuel() {
        return tradeAndDuel;
    }

    public CombatAssistant getCombat() {
        return combatAssistant;
    }

    public ActionsPerformed getActions() {
        return actionHandler;
    }

    public void queueMessage(Packet arg1) {
        synchronized (queuedPackets) {
            queuedPackets.add(arg1);
        }
    }

    @Override
    public boolean processQueuedPackets() {
        synchronized (queuedPackets) {
            Packet p;
            while ((p = queuedPackets.poll()) != null) {
                inStream.currentOffset = 0;
                packetType = p.getOpcode();
                packetSize = p.getLength();
                inStream.buffer = p.getPayload().array();
                if (packetType > 0) {
                    PacketHandler.processPacket(this, packetType, packetSize);
                }
            }
        }
        return true;
    }
}