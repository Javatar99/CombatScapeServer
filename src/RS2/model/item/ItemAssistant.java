package RS2.model.item;

import RS2.GameEngine;
import RS2.Settings;
import RS2.model.item.definitions.ItemDefinition;
import RS2.model.npc.handlers.NPCHandler;
import RS2.model.player.Client;
import RS2.model.player.PlayerHandler;
import RS2.util.Misc;

import java.util.ArrayList;
import java.util.List;

/**
 * Indicates Several Usage Of Items
 *
 * @author Sanity Revised by Shawn Notes by Shawn
 */
@SuppressWarnings("all")
public class ItemAssistant {

    private Client c;

    public ItemAssistant(Client client) {
        this.c = client;
    }

    /**
     * Empties all of (a) player's items.
     */
    public void resetItems(int WriteFrame) {
        synchronized (c) {
            if (c.getOutStream() != null && c != null) {
                c.getOutStream().createFrameVarSizeWord(53);
                c.getOutStream().writeWord(WriteFrame);
                c.getOutStream().writeWord(c.inventory.getItemIds().length);
                for (int i = 0; i < c.inventory.getItemIds().length; i++) {
                    if (c.inventory.getItemAmounts()[i] > 254) {
                        c.getOutStream().writeByte(255);
                        c.getOutStream().writeDWord_v2(c.inventory.getItemAmounts()[i]);
                    } else {
                        c.getOutStream().writeByte(c.inventory.getItemAmounts()[i]);
                    }
                    c.getOutStream().writeWordBigEndianA(c.inventory.getItemIds()[i]);
                }
                c.getOutStream().endFrameVarSizeWord();
                c.flushOutStream();
            }
        }
    }

    /**
     * Counts (a) player's items.
     *
     * @param itemID
     * @return count start
     */
    public int getItemCount(int itemID) {
        int count = 0;
        for (int j = 0; j < c.inventory.getItemIds().length; j++) {
            if (c.inventory.getItemIds()[j] == itemID + 1) {
                count += c.inventory.getItemAmounts()[j];
            }
        }
        return count;
    }

    /**
     * Gets the bonus' of an item.
     */
    public void writeBonus() {
        int offset = 0;
        String send = "";
        for (int i = 0; i < c.playerBonus.length; i++) {
            if (c.playerBonus[i] >= 0) {
                send = BONUS_NAMES[i] + ": +" + c.playerBonus[i];
            } else {
                send = BONUS_NAMES[i] + ": -"
                        + java.lang.Math.abs(c.playerBonus[i]);
            }

            if (i == 10) {
                offset = 1;
            }
            c.getPA().sendFrame126(send, (1675 + i + offset));
        }

    }

    /**
     * Gets the total count of (a) player's items.
     *
     * @param itemID
     * @return
     */
    public int getTotalCount(int itemID) {
        int count = 0;
        for (int j = 0; j < c.inventory.getItemIds().length; j++) {
            if (Item.itemIsNote[itemID + 1]) {
                if (itemID + 2 == c.inventory.getItemIds()[j])
                    count += c.inventory.getItemAmounts()[j];
            }
            if (!Item.itemIsNote[itemID + 1]) {
                if (itemID + 1 == c.inventory.getItemIds()[j]) {
                    count += c.inventory.getItemAmounts()[j];
                }
            }
        }
        for (int j = 0; j < c.bank.getItemIds().length; j++) {
            if (c.bank.getItemIds()[j] == itemID + 1) {
                count += c.bank.getItemAmounts()[j];
            }
        }
        return count;
    }

    /**
     * Send the items kept on death.
     */
    public void sendItemsKept() {
        synchronized (c) {
            if (c.getOutStream() != null && c != null) {
                c.getOutStream().createFrameVarSizeWord(53);
                c.getOutStream().writeWord(6963);
                c.getOutStream().writeWord(c.itemKeptId.length);
                for (int i = 0; i < c.itemKeptId.length; i++) {
                    if (c.inventory.getItemAmounts()[i] > 254) {
                        c.getOutStream().writeByte(255);
                        c.getOutStream().writeDWord_v2(1);
                    } else {
                        c.getOutStream().writeByte(1);
                    }
                    if (c.itemKeptId[i] > 0) {
                        c.getOutStream().writeWordBigEndianA(
                                c.itemKeptId[i] + 1);
                    } else {
                        c.getOutStream().writeWordBigEndianA(0);
                    }
                }
                c.getOutStream().endFrameVarSizeWord();
                c.flushOutStream();
            }
        }
    }

    /**
     * Item kept on death
     **/
    public void keepItem(int keepItem, boolean deleteItem) {
        int value = 0;
        int item = 0;
        int slotId = 0;
        boolean itemInInventory = false;
        for (int i = 0; i < c.inventory.getItemIds().length; i++) {
            if (c.inventory.getItemIds()[i] - 1 > 0) {
                int inventoryItemValue = c.getShops().getItemShopValue(
                        c.inventory.getItemIds()[i] - 1);
                if (inventoryItemValue > value && (!c.invSlot[i])) {
                    value = inventoryItemValue;
                    item = c.inventory.getItemIds()[i] - 1;
                    slotId = i;
                    itemInInventory = true;
                }
            }
        }
        for (int i1 = 0; i1 < c.equipment.getItemIds().length; i1++) {
            if (c.equipment.getItemIds()[i1] > 0) {
                int equipmentItemValue = c.getShops().getItemShopValue(
                        c.equipment.getItemIds()[i1]);
                if (equipmentItemValue > value && (!c.equipSlot[i1])) {
                    value = equipmentItemValue;
                    item = c.equipment.getItemIds()[i1];
                    slotId = i1;
                    itemInInventory = false;
                }
            }
        }
        if (itemInInventory) {
            c.invSlot[slotId] = true;
            if (deleteItem) {
                deleteItem(c.inventory.getItemIds()[slotId] - 1,
                        getItemSlot(c.inventory.getItemIds()[slotId] - 1), 1);
            }
        } else {
            c.equipSlot[slotId] = true;
            if (deleteItem) {
                deleteEquipment(item, slotId);
            }
        }
        c.itemKeptId[keepItem] = item;
    }

    /**
     * Reset items kept on death.
     **/
    public void resetKeepItems() {
        for (int i = 0; i < c.itemKeptId.length; i++) {
            c.itemKeptId[i] = -1;
        }
        for (int i1 = 0; i1 < c.invSlot.length; i1++) {
            c.invSlot[i1] = false;
        }
        for (int i2 = 0; i2 < c.equipSlot.length; i2++) {
            c.equipSlot[i2] = false;
        }
    }

    /**
     * Deletes all of a player's items.
     **/
    public void deleteAllItems() {
        for (int i1 = 0; i1 < c.equipment.getItemIds().length; i1++) {
            deleteEquipment(c.equipment.getItemIds()[i1], i1);
        }
        for (int i = 0; i < c.inventory.getItemIds().length; i++) {
            deleteItem(c.inventory.getItemIds()[i] - 1, getItemSlot(c.inventory.getItemIds()[i] - 1),
                    c.inventory.getItemAmounts()[i]);
        }
    }

    /**
     * Drops all items for a killer.
     **/
    public void dropAllItems() {
        Client o = (Client) PlayerHandler.players[c.killerId];

        for (int i = 0; i < c.inventory.getItemIds().length; i++) {
            if (o != null) {
                if (tradeable(c.inventory.getItemIds()[i] - 1)) {
                    GameEngine.itemHandler.createGroundItem(o,
                            c.inventory.getItemIds()[i] - 1, c.getX(), c.getY(),
                            c.inventory.getItemAmounts()[i], c.killerId);
                } else {
                    if (specialCase(c.inventory.getItemIds()[i] - 1))
                        GameEngine.itemHandler.createGroundItem(o, 995, c.getX(),
                                c.getY(),
                                getUntradePrice(c.inventory.getItemIds()[i] - 1),
                                c.killerId);
                    GameEngine.itemHandler.createGroundItem(c,
                            c.inventory.getItemIds()[i] - 1, c.getX(), c.getY(),
                            c.inventory.getItemAmounts()[i], c.playerId);
                }
            } else {
                GameEngine.itemHandler.createGroundItem(c, c.inventory.getItemIds()[i] - 1,
                        c.getX(), c.getY(), c.inventory.getItemAmounts()[i], c.playerId);
            }
        }
        for (int e = 0; e < c.equipment.getItemIds().length; e++) {
            if (o != null) {
                if (tradeable(c.equipment.getItemIds()[e])) {
                    GameEngine.itemHandler.createGroundItem(o,
                            c.equipment.getItemIds()[e], c.getX(), c.getY(),
                            c.equipment.getItemAmounts()[e], c.killerId);
                } else {
                    if (specialCase(c.equipment.getItemIds()[e]))
                        GameEngine.itemHandler.createGroundItem(o, 995, c.getX(),
                                c.getY(),
                                getUntradePrice(c.equipment.getItemIds()[e]),
                                c.killerId);
                    GameEngine.itemHandler.createGroundItem(c,
                            c.equipment.getItemIds()[e], c.getX(), c.getY(),
                            c.equipment.getItemAmounts()[e], c.playerId);
                }
            } else {
                GameEngine.itemHandler.createGroundItem(c, c.equipment.getItemIds()[e],
                        c.getX(), c.getY(), c.equipment.getItemAmounts()[e], c.playerId);
            }
        }
        if (o != null) {
            GameEngine.itemHandler.createGroundItem(o, 526, c.getX(), c.getY(), 1,
                    c.killerId);
        }
    }

    /**
     * Untradable items with a special currency. (Tokkel, etc)
     *
     * @param item
     * @return amount
     */
    public int getUntradePrice(int item) {
        switch (item) {
            case 2518:
            case 2524:
            case 2526:
                return 100000;
            case 2520:
            case 2522:
                return 150000;
        }
        return 0;
    }

    /**
     * Special items with currency.
     */
    public boolean specialCase(int itemId) {
        switch (itemId) {
            case 2518:
            case 2520:
            case 2522:
            case 2524:
            case 2526:
                return true;
        }
        return false;
    }

    /**
     * Voided items. (Not void knight items..)
     *
     * @param itemId
     */
    public void addToVoidList(int itemId) {
        switch (itemId) {
            case 2518:
                c.voidStatus[0]++;
                break;
            case 2520:
                c.voidStatus[1]++;
                break;
            case 2522:
                c.voidStatus[2]++;
                break;
            case 2524:
                c.voidStatus[3]++;
                break;
            case 2526:
                c.voidStatus[4]++;
                break;
        }
    }

    /**
     * Handles tradable items.
     */
    public boolean tradeable(int itemId) {
        for (int j = 0; j < Settings.ITEM_TRADEABLE.length; j++) {
            if (itemId == Settings.ITEM_TRADEABLE[j])
                return false;
        }
        return true;
    }

    /**
     * Adds an item to a player's inventory.
     **/
    public boolean addItem(int item, int amount) {
        synchronized (c) {
            if (amount < 1) {
                amount = 1;
            }
            if (item <= 0) {
                return false;
            }
            if ((((freeSlots() >= 1) || playerHasItem(item, 1)) && Item.itemStackable[item])
                    || ((freeSlots() > 0) && !Item.itemStackable[item])) {
                for (int i = 0; i < c.inventory.getItemIds().length; i++) {
                    if ((c.inventory.getItemIds()[i] == (item + 1))
                            && Item.itemStackable[item]
                            && (c.inventory.getItemIds()[i] > 0)) {
                        c.inventory.getItemIds()[i] = (item + 1);
                        if (((c.inventory.getItemAmounts()[i] + amount) < Settings.MAXITEM_AMOUNT)
                                && ((c.inventory.getItemAmounts()[i] + amount) > -1)) {
                            c.inventory.getItemAmounts()[i] += amount;
                        } else {
                            c.inventory.getItemAmounts()[i] = Settings.MAXITEM_AMOUNT;
                        }
                        if (c.getOutStream() != null && c != null) {
                            c.getOutStream().createFrameVarSizeWord(34);
                            c.getOutStream().writeWord(3214);
                            c.getOutStream().writeByte(i);
                            c.getOutStream().writeWord(c.inventory.getItemIds()[i]);
                            if (c.inventory.getItemAmounts()[i] > 254) {
                                c.getOutStream().writeByte(255);
                                c.getOutStream().writeDWord(c.inventory.getItemAmounts()[i]);
                            } else {
                                c.getOutStream().writeByte(c.inventory.getItemAmounts()[i]);
                            }
                            c.getOutStream().endFrameVarSizeWord();
                            c.flushOutStream();
                        }
                        i = 30;
                        return true;
                    }
                }
                for (int i = 0; i < c.inventory.getItemIds().length; i++) {
                    if (c.inventory.getItemIds()[i] <= 0) {
                        c.inventory.getItemIds()[i] = item + 1;
                        if ((amount < Settings.MAXITEM_AMOUNT) && (amount > -1)) {
                            c.inventory.getItemAmounts()[i] = 1;
                            if (amount > 1) {
                                c.getItems().addItem(item, amount - 1);
                                return true;
                            }
                        } else {
                            c.inventory.getItemAmounts()[i] = Settings.MAXITEM_AMOUNT;
                        }
                        resetItems(3214);
                        i = 30;
                        return true;
                    }
                }
                return false;
            } else {
                resetItems(3214);
                c.sendMessage("Not enough space in your inventory.");
                return false;
            }
        }
    }

    /**
     * Gets the item type.
     */
    public String itemType(int item) {
        for (int i = 0; i < Item.capes.length; i++) {
            if (item == Item.capes[i])
                return "cape";
        }
        for (int i = 0; i < Item.hats.length; i++) {
            if (item == Item.hats[i])
                return "hat";
        }
        for (int i = 0; i < Item.boots.length; i++) {
            if (item == Item.boots[i])
                return "boots";
        }
        for (int i = 0; i < Item.gloves.length; i++) {
            if (item == Item.gloves[i])
                return "gloves";
        }
        for (int i = 0; i < Item.shields.length; i++) {
            if (item == Item.shields[i])
                return "shield";
        }
        for (int i = 0; i < Item.amulets.length; i++) {
            if (item == Item.amulets[i])
                return "amulet";
        }
        for (int i = 0; i < Item.arrows.length; i++) {
            if (item == Item.arrows[i])
                return "arrows";
        }
        for (int i = 0; i < Item.rings.length; i++) {
            if (item == Item.rings[i])
                return "ring";
        }
        for (int i = 0; i < Item.body.length; i++) {
            if (item == Item.body[i])
                return "body";
        }
        for (int i = 0; i < Item.legs.length; i++) {
            if (item == Item.legs[i])
                return "legs";
        }
        return "weapon";
    }

    /**
     * Item bonuses.
     **/
    public final String[] BONUS_NAMES = {"Stab", "Slash", "Crush", "Magic",
            "Range", "Stab", "Slash", "Crush", "Magic", "Range", "Strength",
            "Prayer"};

    /**
     * Resets item bonuses.
     */
    public void resetBonus() {
        for (int i = 0; i < c.playerBonus.length; i++) {
            c.playerBonus[i] = 0;
        }
    }

    /**
     * Gets the item bonus from the item.cfg.
     */
    public void getBonus() {
        for (int i = 0; i < c.equipment.getItemIds().length; i++) {
            if (c.equipment.getItemIds()[i] > -1) {
                for (int j = 0; j < Settings.ITEM_LIMIT; j++) {
                    if (GameEngine.itemHandler.itemDefinitions[j] != null) {
                        if (GameEngine.itemHandler.itemDefinitions[j].itemId == c.equipment.getItemIds()[i]) {
                            for (int k = 0; k < c.playerBonus.length; k++) {
                                c.playerBonus[k] += GameEngine.itemHandler.itemDefinitions[j].Bonuses[k];
                            }
                            break;
                        }
                    }
                }
            }
        }
    }

    /**
     * Weapon type.
     **/
    public void sendWeapon(int Weapon, String WeaponName) {
        String WeaponName2 = WeaponName.replaceAll("Bronze", "");
        WeaponName2 = WeaponName2.replaceAll("Iron", "");
        WeaponName2 = WeaponName2.replaceAll("Steel", "");
        WeaponName2 = WeaponName2.replaceAll("Black", "");
        WeaponName2 = WeaponName2.replaceAll("Mithril", "");
        WeaponName2 = WeaponName2.replaceAll("Adamant", "");
        WeaponName2 = WeaponName2.replaceAll("Rune", "");
        WeaponName2 = WeaponName2.replaceAll("Granite", "");
        WeaponName2 = WeaponName2.replaceAll("Dragon", "");
        WeaponName2 = WeaponName2.replaceAll("Drag", "");
        WeaponName2 = WeaponName2.replaceAll("Crystal", "");
        WeaponName2 = WeaponName2.trim();
        /**
         * Attack styles.
         */
        if (WeaponName.equals("Unarmed")) {
            c.setSidebarInterface(0, 5855); // punch, kick, block
            c.getPA().sendFrame126(WeaponName, 5857);
        } else if (WeaponName.endsWith("whip")) {
            c.setSidebarInterface(0, 12290); // flick, lash, deflect
            c.getPA().sendFrame246(12291, 200, Weapon);
            c.getPA().sendFrame126(WeaponName, 12293);
        } else if (WeaponName.endsWith("bow") || WeaponName.endsWith("10")
                || WeaponName.endsWith("full")
                || WeaponName.startsWith("seercull")) {
            c.setSidebarInterface(0, 1764); // accurate, rapid, longrange
            c.getPA().sendFrame246(1765, 200, Weapon);
            c.getPA().sendFrame126(WeaponName, 1767);
        } else if (WeaponName.startsWith("Staff")
                || WeaponName.endsWith("staff") || WeaponName.endsWith("wand")) {
            c.setSidebarInterface(0, 328); // spike, impale, smash, block
            c.getPA().sendFrame246(329, 200, Weapon);
            c.getPA().sendFrame126(WeaponName, 331);
        } else if (WeaponName2.startsWith("dart")
                || WeaponName2.startsWith("knife")
                || WeaponName2.startsWith("javelin")
                || WeaponName.equalsIgnoreCase("toktz-xil-ul")) {
            c.setSidebarInterface(0, 4446); // accurate, rapid, longrange
            c.getPA().sendFrame246(4447, 200, Weapon);
            c.getPA().sendFrame126(WeaponName, 4449);
        } else if (WeaponName2.startsWith("dagger")
                || WeaponName2.contains("sword")) {
            c.setSidebarInterface(0, 2276); // stab, lunge, slash, block
            c.getPA().sendFrame246(2277, 200, Weapon);
            c.getPA().sendFrame126(WeaponName, 2279);
        } else if (WeaponName2.startsWith("pickaxe")) {
            c.setSidebarInterface(0, 5570); // spike, impale, smash, block
            c.getPA().sendFrame246(5571, 200, Weapon);
            c.getPA().sendFrame126(WeaponName, 5573);
        } else if (WeaponName2.startsWith("axe")
                || WeaponName2.startsWith("battleaxe")) {
            c.setSidebarInterface(0, 1698); // chop, hack, smash, block
            c.getPA().sendFrame246(1699, 200, Weapon);
            c.getPA().sendFrame126(WeaponName, 1701);
        } else if (WeaponName2.startsWith("halberd")) {
            c.setSidebarInterface(0, 8460); // jab, swipe, fend
            c.getPA().sendFrame246(8461, 200, Weapon);
            c.getPA().sendFrame126(WeaponName, 8463);
        } else if (WeaponName2.startsWith("Scythe")) {
            c.setSidebarInterface(0, 8460); // jab, swipe, fend
            c.getPA().sendFrame246(8461, 200, Weapon);
            c.getPA().sendFrame126(WeaponName, 8463);
        } else if (WeaponName2.startsWith("spear")) {
            c.setSidebarInterface(0, 4679); // lunge, swipe, pound, block
            c.getPA().sendFrame246(4680, 200, Weapon);
            c.getPA().sendFrame126(WeaponName, 4682);
        } else if (WeaponName2.toLowerCase().contains("mace")) {
            c.setSidebarInterface(0, 3796);
            c.getPA().sendFrame246(3797, 200, Weapon);
            c.getPA().sendFrame126(WeaponName, 3799);

        } else if (c.equipment.getItemIds()[c.playerWeapon] == 4153) {
            c.setSidebarInterface(0, 425); // war hammer equip.
            c.getPA().sendFrame246(426, 200, Weapon);
            c.getPA().sendFrame126(WeaponName, 428);
        } else {
            c.setSidebarInterface(0, 2423); // chop, slash, lunge, block
            c.getPA().sendFrame246(2424, 200, Weapon);
            c.getPA().sendFrame126(WeaponName, 2426);
        }

    }

    public boolean hasItemRequirements(final int itemId){
        ItemDefinition def = GameEngine.itemHandler.itemDefinitions[itemId];
        if(def.itemId != itemId) {
            c.sendMessage("Item mis-match! : " + def.itemId + " =? " + itemId);
            return true;
        }
        List<String> msgs = new ArrayList<>();
        for (int id = 0; id < def.levelRequirements.length; id++) {
            final int level = def.levelRequirements[id];
            if (level > c.skills.getSkill(id).getActualLevel()) {
                msgs.add("You need a skill level of " + level + " in " + Misc.getSkillName(id) + ".");
            }
        }
        if(!msgs.isEmpty()){
            msgs.forEach(c::sendMessage);
            return false;
        }
        return true;
    }

    /**
     * Weapon requirements.
     **/
    public void getRequirements(String itemName, int itemId) {
        c.attackLevelReq = c.defenceLevelReq = c.strengthLevelReq = c.rangeLevelReq = c.magicLevelReq = 0;
        if (itemName.contains("mystic") || itemName.contains("nchanted")) {
            if (itemName.contains("staff")) {
                c.magicLevelReq = 20;
                c.attackLevelReq = 40;
            } else {
                c.magicLevelReq = 20;
                c.defenceLevelReq = 20;
            }
        }
        if (itemName.contains("infinity")) {
            c.magicLevelReq = 50;
            c.defenceLevelReq = 25;
        }
        if (itemName.contains("splitbark")) {
            c.magicLevelReq = 40;
            c.defenceLevelReq = 40;
        }
        if (itemName.contains("Green")) {
            if (itemName.contains("hide")) {
                c.rangeLevelReq = 40;
                if (itemName.contains("body"))
                    c.defenceLevelReq = 40;
                return;
            }
        }
        if (itemName.contains("Blue")) {
            if (itemName.contains("hide")) {
                c.rangeLevelReq = 50;
                if (itemName.contains("body"))
                    c.defenceLevelReq = 40;
                return;
            }
        }
        if (itemName.contains("Red")) {
            if (itemName.contains("hide")) {
                c.rangeLevelReq = 60;
                if (itemName.contains("body"))
                    c.defenceLevelReq = 40;
                return;
            }
        }
        if (itemName.contains("Black")) {
            if (itemName.contains("hide")) {
                c.rangeLevelReq = 70;
                if (itemName.contains("body"))
                    c.defenceLevelReq = 40;
                return;
            }
        }
        if (itemName.contains("bronze")) {
            if (!itemName.contains("knife") && !itemName.contains("dart")
                    && !itemName.contains("javelin")
                    && !itemName.contains("thrownaxe")) {
                c.attackLevelReq = c.defenceLevelReq = 1;
            }
            return;
        }
        if (itemName.contains("iron")) {
            if (!itemName.contains("knife") && !itemName.contains("dart")
                    && !itemName.contains("javelin")
                    && !itemName.contains("thrownaxe")) {
                c.attackLevelReq = c.defenceLevelReq = 1;
            }
            return;
        }
        if (itemName.contains("steel")) {
            if (!itemName.contains("knife") && !itemName.contains("dart")
                    && !itemName.contains("javelin")
                    && !itemName.contains("thrownaxe")) {
                c.attackLevelReq = c.defenceLevelReq = 5;
            }
            return;
        }
        if (itemName.contains("black")) {
            if (!itemName.contains("knife") && !itemName.contains("dart")
                    && !itemName.contains("javelin")
                    && !itemName.contains("thrownaxe")
                    && !itemName.contains("vamb") && !itemName.contains("chap")) {
                c.attackLevelReq = c.defenceLevelReq = 10;
            }
            return;
        }
        if (itemName.contains("mithril")) {
            if (!itemName.contains("knife") && !itemName.contains("dart")
                    && !itemName.contains("javelin")
                    && !itemName.contains("thrownaxe")) {
                c.attackLevelReq = c.defenceLevelReq = 20;
            }
            return;
        }
        if (itemName.contains("adamant")) {
            if (!itemName.contains("knife") && !itemName.contains("dart")
                    && !itemName.contains("javelin")
                    && !itemName.contains("thrownaxe")) {
                c.attackLevelReq = c.defenceLevelReq = 30;
            }
            return;
        }
        if (itemName.contains("rune")) {
            if (!itemName.contains("knife") && !itemName.contains("dart")
                    && !itemName.contains("javelin")
                    && !itemName.contains("thrownaxe")
                    && !itemName.contains("'bow")) {
                c.attackLevelReq = c.defenceLevelReq = 40;
            }
            return;
        }
        if (itemName.contains("dragon")) {
            if (!itemName.contains("nti-") && !itemName.contains("fire")) {
                c.attackLevelReq = c.defenceLevelReq = 60;
                return;
            }
        }
        if (itemName.contains("crystal")) {
            if (itemName.contains("shield")) {
                c.defenceLevelReq = 70;
            } else {
                c.rangeLevelReq = 70;
            }
            return;
        }
        if (itemName.contains("ahrim")) {
            if (itemName.contains("staff")) {
                c.magicLevelReq = 70;
                c.attackLevelReq = 70;
            } else {
                c.magicLevelReq = 70;
                c.defenceLevelReq = 70;
            }
        }
        if (itemName.contains("karil")) {
            if (itemName.contains("crossbow")) {
                c.rangeLevelReq = 70;
            } else {
                c.rangeLevelReq = 70;
                c.defenceLevelReq = 70;
            }
        }
        if (itemName.contains("godsword")) {
            c.attackLevelReq = 75;
        }
        if (itemName.contains("3rd age") && !itemName.contains("amulet")) {
            c.defenceLevelReq = 60;
        }
        if (itemName.contains("Initiate")) {
            c.defenceLevelReq = 20;
        }
        if (itemName.contains("verac") || itemName.contains("guthan")
                || itemName.contains("dharok") || itemName.contains("torag")) {

            if (itemName.contains("hammers")) {
                c.attackLevelReq = 70;
                c.strengthLevelReq = 70;
            } else if (itemName.contains("axe")) {
                c.attackLevelReq = 70;
                c.strengthLevelReq = 70;
            } else if (itemName.contains("warspear")) {
                c.attackLevelReq = 70;
                c.strengthLevelReq = 70;
            } else if (itemName.contains("flail")) {
                c.attackLevelReq = 70;
                c.strengthLevelReq = 70;
            } else {
                c.defenceLevelReq = 70;
            }
        }

        switch (itemId) {
            case 8839:
            case 8840:
            case 8842:
            case 11663:
            case 11664:
            case 11665:
                c.attackLevelReq = 42;
                c.rangeLevelReq = 42;
                c.strengthLevelReq = 42;
                c.magicLevelReq = 42;
                c.defenceLevelReq = 42;
                return;
            case 10551:
            case 2503:
            case 2501:
            case 2499:
            case 1135:
                c.defenceLevelReq = 40;
                return;
            case 11235:
            case 6522:
                c.rangeLevelReq = 60;
                break;
            case 6524:
                c.defenceLevelReq = 60;
                break;
            case 11284:
                c.defenceLevelReq = 75;
                return;
            case 6889:
            case 6914:
                c.magicLevelReq = 60;
                break;
            case 861:
                c.rangeLevelReq = 50;
                break;
            case 10828:
                c.defenceLevelReq = 55;
                break;
            case 11724:
            case 11726:
            case 11728:
                c.defenceLevelReq = 65;
                break;
            case 3751:
            case 3749:
            case 3755:
                c.defenceLevelReq = 40;
                break;

            case 7462:
            case 7461:
                c.defenceLevelReq = 40;
                break;
            case 8846:
                c.defenceLevelReq = 5;
                break;
            case 8847:
                c.defenceLevelReq = 10;
                break;
            case 8848:
                c.defenceLevelReq = 20;
                break;
            case 8849:
                c.defenceLevelReq = 30;
                break;
            case 8850:
                c.defenceLevelReq = 40;
                break;

            case 7460:
                c.defenceLevelReq = 40;
                break;

            case 837:
                c.rangeLevelReq = 61;
                break;

            case 4151:
                c.attackLevelReq = 70;
                return;

            case 6724:
                c.rangeLevelReq = 60;
                return;
            case 4153:
                c.attackLevelReq = 50;
                c.strengthLevelReq = 50;
                return;
        }
    }

    /**
     * Two handed weapon check.
     **/
    public boolean is2handed(String itemName, int itemId) {
        if (itemName.contains("ahrim") || itemName.contains("karil")
                || itemName.contains("verac") || itemName.contains("guthan")
                || itemName.contains("dharok") || itemName.contains("torag")) {
            return true;
        }
        if (itemName.contains("longbow") || itemName.contains("shortbow")
                || itemName.contains("ark bow")) {
            return true;
        }
        if (itemName.contains("crystal")) {
            return true;
        }
        if (itemName.contains("godsword")
                || itemName.contains("aradomin sword")
                || itemName.contains("2h") || itemName.contains("spear")) {
            return true;
        }
        switch (itemId) {
            case 6724:
            case 11730:
            case 4153:
            case 6528:
            case 14484:
                return true;
        }
        return false;
    }

    /**
     * Adds special attack bar to special attack weapons. Removes special attack
     * bar to weapons that do not have special attacks.
     **/
    public void addSpecialBar(int weapon) {
        switch (weapon) {

            case 4151: // whip
                c.getPA().sendFrame171(0, 12323);
                specialAmount(weapon, c.specAmount, 12335);
                break;

            case 859: // magic bows
            case 861:
            case 11235:
                c.getPA().sendFrame171(0, 7549);
                specialAmount(weapon, c.specAmount, 7561);
                break;

            case 4587: // dscimmy
                c.getPA().sendFrame171(0, 7599);
                specialAmount(weapon, c.specAmount, 7611);
                break;

            case 3204: // d hally
                c.getPA().sendFrame171(0, 8493);
                specialAmount(weapon, c.specAmount, 8505);
                break;

            case 1377: // d battleaxe
                c.getPA().sendFrame171(0, 7499);
                specialAmount(weapon, c.specAmount, 7511);
                break;

            case 4153: // gmaul
                c.getPA().sendFrame171(0, 7474);
                specialAmount(weapon, c.specAmount, 7486);
                break;

            case 1249: // dspear
                c.getPA().sendFrame171(0, 7674);
                specialAmount(weapon, c.specAmount, 7686);
                break;

            case 1215:// dragon dagger
            case 1231:
            case 5680:
            case 5698:
            case 1305: // dragon long
            case 11694:
            case 11698:
            case 11700:
            case 11730:
            case 11696:
                c.getPA().sendFrame171(0, 7574);
                specialAmount(weapon, c.specAmount, 7586);
                break;

            case 1434: // dragon mace
                c.getPA().sendFrame171(0, 7624);
                specialAmount(weapon, c.specAmount, 7636);
                break;

            default:
                c.getPA().sendFrame171(1, 7624); // mace interface
                c.getPA().sendFrame171(1, 7474); // hammer, gmaul
                c.getPA().sendFrame171(1, 7499); // axe
                c.getPA().sendFrame171(1, 7549); // bow interface
                c.getPA().sendFrame171(1, 7574); // sword interface
                c.getPA().sendFrame171(1, 7599); // scimmy sword interface, for most
                // swords
                c.getPA().sendFrame171(1, 8493);
                c.getPA().sendFrame171(1, 12323); // whip interface
                break;
        }
    }

    /**
     * Special attack bar filling amount.
     **/
    public void specialAmount(int weapon, double specAmount, int barId) {
        c.specBarId = barId;
        c.getPA().sendFrame70(specAmount >= 10 ? 500 : 0, 0, (--barId));
        c.getPA().sendFrame70(specAmount >= 9 ? 500 : 0, 0, (--barId));
        c.getPA().sendFrame70(specAmount >= 8 ? 500 : 0, 0, (--barId));
        c.getPA().sendFrame70(specAmount >= 7 ? 500 : 0, 0, (--barId));
        c.getPA().sendFrame70(specAmount >= 6 ? 500 : 0, 0, (--barId));
        c.getPA().sendFrame70(specAmount >= 5 ? 500 : 0, 0, (--barId));
        c.getPA().sendFrame70(specAmount >= 4 ? 500 : 0, 0, (--barId));
        c.getPA().sendFrame70(specAmount >= 3 ? 500 : 0, 0, (--barId));
        c.getPA().sendFrame70(specAmount >= 2 ? 500 : 0, 0, (--barId));
        c.getPA().sendFrame70(specAmount >= 1 ? 500 : 0, 0, (--barId));
        updateSpecialBar();
        sendWeapon(weapon, getItemName(weapon));
    }

    /**
     * Special attack text.
     **/
    public void updateSpecialBar() {
        if (c.usingSpecial) {
            c.getPA()
                    .sendFrame126(
                            ""
                                    + (c.specAmount >= 2 ? "@yel@S P"
                                    : "@bla@S P")
                                    + ""
                                    + (c.specAmount >= 3 ? "@yel@ E"
                                    : "@bla@ E")
                                    + ""
                                    + (c.specAmount >= 4 ? "@yel@ C I"
                                    : "@bla@ C I")
                                    + ""
                                    + (c.specAmount >= 5 ? "@yel@ A L"
                                    : "@bla@ A L")
                                    + ""
                                    + (c.specAmount >= 6 ? "@yel@  A"
                                    : "@bla@  A")
                                    + ""
                                    + (c.specAmount >= 7 ? "@yel@ T T"
                                    : "@bla@ T T")
                                    + ""
                                    + (c.specAmount >= 8 ? "@yel@ A"
                                    : "@bla@ A")
                                    + ""
                                    + (c.specAmount >= 9 ? "@yel@ C"
                                    : "@bla@ C")
                                    + ""
                                    + (c.specAmount >= 10 ? "@yel@ K"
                                    : "@bla@ K"), c.specBarId);
        } else {
            c.getPA().sendFrame126("@bla@S P E C I A L  A T T A C K",
                    c.specBarId);
        }
    }

    /**
     * Wielding items.
     **/
    public boolean wearItem(int slot) {
        synchronized (c) {
            int wearID = c.inventory.getItemIds()[slot];
            int targetSlot = 0;
            boolean canWearItem = hasItemRequirements(wearID - 1);
            if(!canWearItem){
                return false;
            }
            targetSlot = Item.targetSlots[wearID];
            if (itemType(wearID).equalsIgnoreCase("cape")) {
                targetSlot = 1;
            } else if (itemType(wearID).equalsIgnoreCase("hat")) {
                targetSlot = 0;
            } else if (itemType(wearID).equalsIgnoreCase("amulet")) {
                targetSlot = 2;
            } else if (itemType(wearID).equalsIgnoreCase("arrows")) {
                targetSlot = 13;
            } else if (itemType(wearID).equalsIgnoreCase("body")) {
                targetSlot = 4;
            } else if (itemType(wearID).equalsIgnoreCase("shield")) {
                targetSlot = 5;
            } else if (itemType(wearID).equalsIgnoreCase("legs")) {
                targetSlot = 7;
            } else if (itemType(wearID).equalsIgnoreCase("gloves")) {
                targetSlot = 9;
            } else if (itemType(wearID).equalsIgnoreCase("boots")) {
                targetSlot = 10;
            } else if (itemType(wearID).equalsIgnoreCase("ring")) {
                targetSlot = 12;
            }
            if (Settings.itemRequirements) {
                if (targetSlot == 10 || targetSlot == 7 || targetSlot == 5
                        || targetSlot == 4 || targetSlot == 0
                        || targetSlot == 9 || targetSlot == 10) {
                    if (c.defenceLevelReq > 0) {
                        if (c.skills.getSkill(1).getActualLevel() < c.defenceLevelReq) {
                            c.sendMessage("You need a defence level of "
                                    + c.defenceLevelReq
                                    + " to wear this item.");
                            canWearItem = false;
                        }
                    }
                    if (c.rangeLevelReq > 0) {
                        if (c.skills.getSkill(4).getActualLevel() < c.rangeLevelReq) {
                            c.sendMessage("You need a range level of "
                                    + c.rangeLevelReq
                                    + " to wear this item.");
                            canWearItem = false;
                        }
                    }
                    if (c.magicLevelReq > 0) {
                        if (c.skills.getSkill(6).getActualLevel() < c.magicLevelReq) {
                            c.sendMessage("You need a magic level of "
                                    + c.magicLevelReq
                                    + " to wear this item.");
                            canWearItem = false;
                        }
                    }
                }
                if (targetSlot == 3) {
                    if (c.attackLevelReq > 0) {
                        if (c.skills.getSkill(0).getActualLevel() < c.attackLevelReq) {
                            c.sendMessage("You need an attack level of "
                                    + c.attackLevelReq
                                    + " to wield this weapon.");
                            canWearItem = false;
                        }
                    }
                    if (c.rangeLevelReq > 0) {
                        if (c.skills.getSkill(4).getActualLevel() < c.rangeLevelReq) {
                            c.sendMessage("You need a range level of "
                                    + c.rangeLevelReq
                                    + " to wield this weapon.");
                            canWearItem = false;
                        }
                    }
                    if (c.magicLevelReq > 0) {
                        if (c.skills.getSkill(6).getActualLevel() < c.magicLevelReq) {
                            c.sendMessage("You need a magic level of "
                                    + c.magicLevelReq
                                    + " to wield this weapon.");
                            canWearItem = false;
                        }
                    }
                }
            }

            if (!canWearItem) {
                return false;
            }

            int wearAmount = c.inventory.getItemAmounts()[slot];
            if (wearAmount < 1) {
                return false;
            }

            if (targetSlot == c.playerWeapon) {
                c.autocasting = false;
                c.autocastId = 0;
                c.getPA().sendFrame36(108, 0);
            }

            if (slot >= 0 && wearID >= 0) {
                int toEquip = c.inventory.getItemIds()[slot];
                int toEquipN = c.inventory.getItemAmounts()[slot];
                int toRemove = c.equipment.getItemIds()[targetSlot];
                int toRemoveN = c.equipment.getItemAmounts()[targetSlot];
                if (toEquip == toRemove + 1 && Item.itemStackable[toRemove]) {
                    deleteItem(toRemove, getItemSlot(toRemove), toEquipN);
                    c.equipment.getItemAmounts()[targetSlot] += toEquipN;
                } else if (targetSlot != 5 && targetSlot != 3) {
                    c.inventory.getItemIds()[slot] = toRemove + 1;
                    c.inventory.getItemAmounts()[slot] = toRemoveN;
                    c.equipment.getItemIds()[targetSlot] = toEquip - 1;
                    c.equipment.getItemAmounts()[targetSlot] = toEquipN;
                } else if (targetSlot == 5) {
                    boolean wearing2h = is2handed(
                            getItemName(c.equipment.getItemIds()[c.playerWeapon])
                                    .toLowerCase(),
                            c.equipment.getItemIds()[c.playerWeapon]);
                    boolean wearingShield = c.equipment.getItemIds()[c.playerShield] > 0;
                    if (wearing2h) {
                        toRemove = c.equipment.getItemIds()[c.playerWeapon];
                        toRemoveN = c.equipment.getItemAmounts()[c.playerWeapon];
                        c.equipment.getItemIds()[c.playerWeapon] = -1;
                        c.equipment.getItemAmounts()[c.playerWeapon] = 0;
                        updateSlot(c.playerWeapon);
                    }
                    c.inventory.getItemIds()[slot] = toRemove + 1;
                    c.inventory.getItemAmounts()[slot] = toRemoveN;
                    c.equipment.getItemIds()[targetSlot] = toEquip - 1;
                    c.equipment.getItemAmounts()[targetSlot] = toEquipN;
                } else if (targetSlot == 3) {
                    boolean is2h = is2handed(getItemName(wearID)
                            .toLowerCase(), wearID);
                    boolean wearingShield = c.equipment.getItemIds()[c.playerShield] > 0;
                    boolean wearingWeapon = c.equipment.getItemIds()[c.playerWeapon] > 0;
                    if (is2h) {
                        if (wearingShield && wearingWeapon) {
                            if (freeSlots() > 0) {
                                c.inventory.getItemIds()[slot] = toRemove + 1;
                                c.inventory.getItemAmounts()[slot] = toRemoveN;
                                c.equipment.getItemIds()[targetSlot] = toEquip - 1;
                                c.equipment.getItemAmounts()[targetSlot] = toEquipN;
                                removeItem(
                                        c.equipment.getItemIds()[c.playerShield],
                                        c.playerShield);
                            } else {
                                c.sendMessage("You do not have enough inventory space to do this.");
                                return false;
                            }
                        } else if (wearingShield && !wearingWeapon) {
                            c.inventory.getItemIds()[slot] = c.equipment.getItemIds()[c.playerShield] + 1;
                            c.inventory.getItemAmounts()[slot] = c.equipment.getItemAmounts()[c.playerShield];
                            c.equipment.getItemIds()[targetSlot] = toEquip - 1;
                            c.equipment.getItemAmounts()[targetSlot] = toEquipN;
                            c.equipment.getItemIds()[c.playerShield] = -1;
                            c.equipment.getItemAmounts()[c.playerShield] = 0;
                            updateSlot(c.playerShield);
                        } else {
                            c.inventory.getItemIds()[slot] = toRemove + 1;
                            c.inventory.getItemAmounts()[slot] = toRemoveN;
                            c.equipment.getItemIds()[targetSlot] = toEquip - 1;
                            c.equipment.getItemAmounts()[targetSlot] = toEquipN;
                        }
                    } else {
                        c.inventory.getItemIds()[slot] = toRemove + 1;
                        c.inventory.getItemAmounts()[slot] = toRemoveN;
                        c.equipment.getItemIds()[targetSlot] = toEquip - 1;
                        c.equipment.getItemAmounts()[targetSlot] = toEquipN;
                    }
                }
                resetItems(3214);
            }
            if (targetSlot == 3) {
                c.usingSpecial = false;
                addSpecialBar(wearID);
            }
            if (c.getOutStream() != null && c != null) {
                c.getOutStream().createFrameVarSizeWord(34);
                c.getOutStream().writeWord(1688);
                c.getOutStream().writeByte(targetSlot);
                c.getOutStream().writeWord(wearID);

                if (c.equipment.getItemAmounts()[targetSlot] > 254) {
                    c.getOutStream().writeByte(255);
                    c.getOutStream().writeDWord(
                            c.equipment.getItemAmounts()[targetSlot]);
                } else {
                    c.getOutStream().writeByte(
                            c.equipment.getItemAmounts()[targetSlot]);
                }

                c.getOutStream().endFrameVarSizeWord();
                c.flushOutStream();
            }
            sendWeapon(c.equipment.getItemIds()[c.playerWeapon],
                    getItemName(c.equipment.getItemIds()[c.playerWeapon]));
            resetBonus();
            getBonus();
            writeBonus();
            c.getCombat().getPlayerAnimIndex(
            );
            c.getPA().requestUpdates();
            return true;
        }
    }

    /**
     * Indicates the action to wear an item.
     *
     * @param wearID
     * @param wearAmount
     * @param targetSlot
     */
    public void wearItem(int wearID, int wearAmount, int targetSlot) {
        synchronized (c) {
            if (c.getOutStream() != null && c != null) {
                c.getOutStream().createFrameVarSizeWord(34);
                c.getOutStream().writeWord(1688);
                c.getOutStream().writeByte(targetSlot);
                c.getOutStream().writeWord(wearID + 1);

                if (wearAmount > 254) {
                    c.getOutStream().writeByte(255);
                    c.getOutStream().writeDWord(wearAmount);
                } else {
                    c.getOutStream().writeByte(wearAmount);
                }
                c.getOutStream().endFrameVarSizeWord();
                c.flushOutStream();
                c.equipment.getItemIds()[targetSlot] = wearID;
                c.equipment.getItemAmounts()[targetSlot] = wearAmount;
                c.getItems().sendWeapon(
                        c.equipment.getItemIds()[c.playerWeapon],
                        c.getItems().getItemName(
                                c.equipment.getItemIds()[c.playerWeapon]));
                c.getItems().resetBonus();
                c.getItems().getBonus();
                c.getItems().writeBonus();
                c.getCombat().getPlayerAnimIndex(
                );
                c.updateRequired = true;
                c.setAppearanceUpdateRequired(true);
            }
        }
    }

    /**
     * Updates the slot when wielding an item.
     *
     * @param slot
     */
    public void updateSlot(int slot) {
        synchronized (c) {
            if (c.getOutStream() != null && c != null) {
                c.getOutStream().createFrameVarSizeWord(34);
                c.getOutStream().writeWord(1688);
                c.getOutStream().writeByte(slot);
                c.getOutStream().writeWord(c.equipment.getItemIds()[slot] + 1);
                if (c.equipment.getItemAmounts()[slot] > 254) {
                    c.getOutStream().writeByte(255);
                    c.getOutStream().writeDWord(c.equipment.getItemAmounts()[slot]);
                } else {
                    c.getOutStream().writeByte(c.equipment.getItemAmounts()[slot]);
                }
                c.getOutStream().endFrameVarSizeWord();
                c.flushOutStream();
            }
        }

    }

    /**
     * Removes a wielded item.
     **/
    public void removeItem(int wearID, int slot) {
        synchronized (c) {
            if (c.getOutStream() != null && c != null) {
                if (c.equipment.getItemIds()[slot] > -1) {
                    if (addItem(c.equipment.getItemIds()[slot],
                            c.equipment.getItemAmounts()[slot])) {
                        c.equipment.getItemIds()[slot] = -1;
                        c.equipment.getItemAmounts()[slot] = 0;
                        sendWeapon(c.equipment.getItemIds()[c.playerWeapon],
                                getItemName(c.equipment.getItemIds()[c.playerWeapon]));
                        resetBonus();
                        getBonus();
                        writeBonus();
                        c.getCombat()
                                .getPlayerAnimIndex(
                                );
                        c.getOutStream().createFrame(34);
                        c.getOutStream().writeWord(6);
                        c.getOutStream().writeWord(1688);
                        c.getOutStream().writeByte(slot);
                        c.getOutStream().writeWord(0);
                        c.getOutStream().writeByte(0);
                        c.flushOutStream();
                        c.updateRequired = true;
                        c.setAppearanceUpdateRequired(true);
                    }
                }
            }
        }
    }

    /**
     * Items in your bank.
     */
    public void rearrangeBank() {
        int totalItems = 0;
        int highestSlot = 0;
        for (int i = 0; i < Settings.BANK_SIZE; i++) {
            if (c.bank.getItemIds()[i] != 0) {
                totalItems++;
                if (highestSlot <= i) {
                    highestSlot = i;
                }
            }
        }

        for (int i = 0; i <= highestSlot; i++) {
            if (c.bank.getItemIds()[i] == 0) {
                boolean stop = false;

                for (int k = i; k <= highestSlot; k++) {
                    if (c.bank.getItemIds()[k] != 0 && !stop) {
                        int spots = k - i;
                        for (int j = k; j <= highestSlot; j++) {
                            c.bank.getItemIds()[j - spots] = c.bank.getItemIds()[j];
                            c.bank.getItemAmounts()[j - spots] = c.bank.getItemAmounts()[j];
                            stop = true;
                            c.bank.getItemIds()[j] = 0;
                            c.bank.getItemAmounts()[j] = 0;
                        }
                    }
                }
            }
        }

        int totalItemsAfter = 0;
        for (int i = 0; i < Settings.BANK_SIZE; i++) {
            if (c.bank.getItemIds()[i] != 0) {
                totalItemsAfter++;
            }
        }

        if (totalItems != totalItemsAfter)
            c.disconnected = true;
    }

    /**
     * Items displayed on the armor interface.
     *
     * @param id
     * @param amount
     */
    public void itemOnInterface(int id, int amount) {
        synchronized (c) {
            c.getOutStream().createFrameVarSizeWord(53);
            c.getOutStream().writeWord(2274);
            c.getOutStream().writeWord(1);
            if (amount > 254) {
                c.getOutStream().writeByte(255);
                c.getOutStream().writeDWord_v2(amount);
            } else {
                c.getOutStream().writeByte(amount);
            }
            c.getOutStream().writeWordBigEndianA(id);
            c.getOutStream().endFrameVarSizeWord();
            c.flushOutStream();
        }
    }

    /**
     * Reseting your bank.
     */
    public void resetBank() {
        synchronized (c) {
            c.getOutStream().createFrameVarSizeWord(53);
            c.getOutStream().writeWord(5382); // Bank
            c.getOutStream().writeWord(Settings.BANK_SIZE);
            for (int i = 0; i < Settings.BANK_SIZE; i++) {
                if (c.bank.getItemAmounts()[i] > 254) {
                    c.getOutStream().writeByte(255);
                    c.getOutStream().writeDWord_v2(c.bank.getItemAmounts()[i]);
                } else {
                    c.getOutStream().writeByte(c.bank.getItemAmounts()[i]);
                }
                if (c.bank.getItemAmounts()[i] < 1) {
                    c.bank.getItemIds()[i] = 0;
                }
                if (c.bank.getItemIds()[i] > Settings.ITEM_LIMIT || c.bank.getItemIds()[i] < 0) {
                    c.bank.getItemIds()[i] = Settings.ITEM_LIMIT;
                }
                c.getOutStream().writeWordBigEndianA(c.bank.getItemIds()[i]);
            }
            c.getOutStream().endFrameVarSizeWord();
            c.flushOutStream();
        }
    }

    /**
     * Resets temporary worn items. Used in minigames, etc
     */
    public void resetTempItems() {
        synchronized (c) {
            int itemCount = 0;
            for (int i = 0; i < c.inventory.getItemIds().length; i++) {
                if (c.inventory.getItemIds()[i] > -1) {
                    itemCount = i;
                }
            }
            c.getOutStream().createFrameVarSizeWord(53);
            c.getOutStream().writeWord(5064);
            c.getOutStream().writeWord(itemCount + 1);
            for (int i = 0; i < itemCount + 1; i++) {
                if (c.inventory.getItemAmounts()[i] > 254) {
                    c.getOutStream().writeByte(255);
                    c.getOutStream().writeDWord_v2(c.inventory.getItemAmounts()[i]);
                } else {
                    c.getOutStream().writeByte(c.inventory.getItemAmounts()[i]);
                }
                if (c.inventory.getItemIds()[i] > Settings.ITEM_LIMIT
                        || c.inventory.getItemIds()[i] < 0) {
                    c.inventory.getItemIds()[i] = Settings.ITEM_LIMIT;
                }
                c.getOutStream().writeWordBigEndianA(c.inventory.getItemIds()[i]);
            }
            c.getOutStream().endFrameVarSizeWord();
            c.flushOutStream();
        }
    }

    public void bankInventory() {
        for (int i = 0; i < c.inventory.getItemIds().length; i++) {
            if (c.inventory.getItemIds()[i] > 0) {
                this.bankItem(c.inventory.getItemIds()[i], i, c.inventory.getItemAmounts()[i]);
            }
        }
    }

    /**
     * Banking your item.
     *
     * @param itemID
     * @param fromSlot
     * @param amount
     * @return
     */
    public boolean bankItem(int itemID, int fromSlot, int amount) {
        if (c.inventory.getItemAmounts()[fromSlot] <= 0) {
            return false;
        }
        if (!Item.itemIsNote[c.inventory.getItemIds()[fromSlot] - 1]) {
            if (c.inventory.getItemIds()[fromSlot] <= 0) {
                return false;
            }
            if (Item.itemStackable[c.inventory.getItemIds()[fromSlot] - 1]
                    || c.inventory.getItemAmounts()[fromSlot] > 1) {
                int toBankSlot = 0;
                boolean alreadyInBank = false;
                for (int i = 0; i < Settings.BANK_SIZE; i++) {
                    if (c.bank.getItemIds()[i] == c.inventory.getItemIds()[fromSlot]) {
                        if (c.inventory.getItemAmounts()[fromSlot] < amount)
                            amount = c.inventory.getItemAmounts()[fromSlot];
                        alreadyInBank = true;
                        toBankSlot = i;
                        i = Settings.BANK_SIZE + 1;
                    }
                }

                /*
                 * Checks if you already have the same item in your bank.
                 */
                if (!alreadyInBank && freeBankSlots() > 0) {
                    for (int i = 0; i < Settings.BANK_SIZE; i++) {
                        if (c.bank.getItemIds()[i] <= 0) {
                            toBankSlot = i;
                            i = Settings.BANK_SIZE + 1;
                        }
                    }
                    c.bank.getItemIds()[toBankSlot] = c.inventory.getItemIds()[fromSlot];
                    if (c.inventory.getItemAmounts()[fromSlot] < amount) {
                        amount = c.inventory.getItemAmounts()[fromSlot];
                    }
                    if ((c.bank.getItemAmounts()[toBankSlot] + amount) <= Settings.MAXITEM_AMOUNT
                            && (c.bank.getItemAmounts()[toBankSlot] + amount) > -1) {
                        c.bank.getItemAmounts()[toBankSlot] += amount;
                    } else {
                        c.sendMessage("Bank full!");
                        return false;
                    }
                    deleteItem((c.inventory.getItemIds()[fromSlot] - 1), fromSlot, amount);
                    resetTempItems();
                    resetBank();
                    return true;
                } else if (alreadyInBank) {
                    if ((c.bank.getItemAmounts()[toBankSlot] + amount) <= Settings.MAXITEM_AMOUNT
                            && (c.bank.getItemAmounts()[toBankSlot] + amount) > -1) {
                        c.bank.getItemAmounts()[toBankSlot] += amount;
                    } else {
                        c.sendMessage("Bank full!");
                        return false;
                    }
                    deleteItem((c.inventory.getItemIds()[fromSlot] - 1), fromSlot, amount);
                    resetTempItems();
                    resetBank();
                    return true;
                } else {
                    c.sendMessage("Bank full!");
                    return false;
                }
            } else {
                itemID = c.inventory.getItemIds()[fromSlot];
                int toBankSlot = 0;
                boolean alreadyInBank = false;
                for (int i = 0; i < Settings.BANK_SIZE; i++) {
                    if (c.bank.getItemIds()[i] == c.inventory.getItemIds()[fromSlot]) {
                        alreadyInBank = true;
                        toBankSlot = i;
                        i = Settings.BANK_SIZE + 1;
                    }
                }
                if (!alreadyInBank && freeBankSlots() > 0) {
                    for (int i = 0; i < Settings.BANK_SIZE; i++) {
                        if (c.bank.getItemIds()[i] <= 0) {
                            toBankSlot = i;
                            i = Settings.BANK_SIZE + 1;
                        }
                    }
                    int firstPossibleSlot = 0;
                    boolean itemExists = false;
                    while (amount > 0) {
                        itemExists = false;
                        for (int i = firstPossibleSlot; i < c.inventory.getItemIds().length; i++) {
                            if ((c.inventory.getItemIds()[i]) == itemID) {
                                firstPossibleSlot = i;
                                itemExists = true;
                                i = 30;
                            }
                        }
                        if (itemExists) {
                            c.bank.getItemIds()[toBankSlot] = c.inventory.getItemIds()[firstPossibleSlot];
                            c.bank.getItemAmounts()[toBankSlot] += 1;
                            deleteItem((c.inventory.getItemIds()[firstPossibleSlot] - 1),
                                    firstPossibleSlot, 1);
                            amount--;
                        } else {
                            amount = 0;
                        }
                    }
                    resetTempItems();
                    resetBank();
                    return true;
                } else if (alreadyInBank) {
                    int firstPossibleSlot = 0;
                    boolean itemExists = false;
                    while (amount > 0) {
                        itemExists = false;
                        for (int i = firstPossibleSlot; i < c.inventory.getItemIds().length; i++) {
                            if ((c.inventory.getItemIds()[i]) == itemID) {
                                firstPossibleSlot = i;
                                itemExists = true;
                                i = 30;
                            }
                        }
                        if (itemExists) {
                            c.bank.getItemAmounts()[toBankSlot] += 1;
                            deleteItem((c.inventory.getItemIds()[firstPossibleSlot] - 1),
                                    firstPossibleSlot, 1);
                            amount--;
                        } else {
                            amount = 0;
                        }
                    }
                    resetTempItems();
                    resetBank();
                    return true;
                } else {
                    c.sendMessage("Bank full!");
                    return false;
                }
            }
        } else if (Item.itemIsNote[c.inventory.getItemIds()[fromSlot] - 1]
                && !Item.itemIsNote[c.inventory.getItemIds()[fromSlot] - 2]) {
            if (c.inventory.getItemIds()[fromSlot] <= 0) {
                return false;
            }
            if (Item.itemStackable[c.inventory.getItemIds()[fromSlot] - 1]
                    || c.inventory.getItemAmounts()[fromSlot] > 1) {
                int toBankSlot = 0;
                boolean alreadyInBank = false;
                for (int i = 0; i < Settings.BANK_SIZE; i++) {
                    if (c.bank.getItemIds()[i] == (c.inventory.getItemIds()[fromSlot] - 1)) {
                        if (c.inventory.getItemAmounts()[fromSlot] < amount)
                            amount = c.inventory.getItemAmounts()[fromSlot];
                        alreadyInBank = true;
                        toBankSlot = i;
                        i = Settings.BANK_SIZE + 1;
                    }
                }

                if (!alreadyInBank && freeBankSlots() > 0) {
                    for (int i = 0; i < Settings.BANK_SIZE; i++) {
                        if (c.bank.getItemIds()[i] <= 0) {
                            toBankSlot = i;
                            i = Settings.BANK_SIZE + 1;
                        }
                    }
                    c.bank.getItemIds()[toBankSlot] = (c.inventory.getItemIds()[fromSlot] - 1);
                    if (c.inventory.getItemAmounts()[fromSlot] < amount) {
                        amount = c.inventory.getItemAmounts()[fromSlot];
                    }
                    if ((c.bank.getItemAmounts()[toBankSlot] + amount) <= Settings.MAXITEM_AMOUNT
                            && (c.bank.getItemAmounts()[toBankSlot] + amount) > -1) {
                        c.bank.getItemAmounts()[toBankSlot] += amount;
                    } else {
                        return false;
                    }
                    deleteItem((c.inventory.getItemIds()[fromSlot] - 1), fromSlot, amount);
                    resetTempItems();
                    resetBank();
                    return true;
                } else if (alreadyInBank) {
                    if ((c.bank.getItemAmounts()[toBankSlot] + amount) <= Settings.MAXITEM_AMOUNT
                            && (c.bank.getItemAmounts()[toBankSlot] + amount) > -1) {
                        c.bank.getItemAmounts()[toBankSlot] += amount;
                    } else {
                        return false;
                    }
                    deleteItem((c.inventory.getItemIds()[fromSlot] - 1), fromSlot, amount);
                    resetTempItems();
                    resetBank();
                    return true;
                } else {
                    c.sendMessage("Bank full!");
                    return false;
                }
            } else {
                itemID = c.inventory.getItemIds()[fromSlot];
                int toBankSlot = 0;
                boolean alreadyInBank = false;
                for (int i = 0; i < Settings.BANK_SIZE; i++) {
                    if (c.bank.getItemIds()[i] == (c.inventory.getItemIds()[fromSlot] - 1)) {
                        alreadyInBank = true;
                        toBankSlot = i;
                        i = Settings.BANK_SIZE + 1;
                    }
                }
                if (!alreadyInBank && freeBankSlots() > 0) {
                    for (int i = 0; i < Settings.BANK_SIZE; i++) {
                        if (c.bank.getItemIds()[i] <= 0) {
                            toBankSlot = i;
                            i = Settings.BANK_SIZE + 1;
                        }
                    }
                    int firstPossibleSlot = 0;
                    boolean itemExists = false;
                    while (amount > 0) {
                        itemExists = false;
                        for (int i = firstPossibleSlot; i < c.inventory.getItemIds().length; i++) {
                            if ((c.inventory.getItemIds()[i]) == itemID) {
                                firstPossibleSlot = i;
                                itemExists = true;
                                i = 30;
                            }
                        }
                        if (itemExists) {
                            c.bank.getItemIds()[toBankSlot] = (c.inventory.getItemIds()[firstPossibleSlot] - 1);
                            c.bank.getItemAmounts()[toBankSlot] += 1;
                            deleteItem((c.inventory.getItemIds()[firstPossibleSlot] - 1),
                                    firstPossibleSlot, 1);
                            amount--;
                        } else {
                            amount = 0;
                        }
                    }
                    resetTempItems();
                    resetBank();
                    return true;
                } else if (alreadyInBank) {
                    int firstPossibleSlot = 0;
                    boolean itemExists = false;
                    while (amount > 0) {
                        itemExists = false;
                        for (int i = firstPossibleSlot; i < c.inventory.getItemIds().length; i++) {
                            if ((c.inventory.getItemIds()[i]) == itemID) {
                                firstPossibleSlot = i;
                                itemExists = true;
                                i = 30;
                            }
                        }
                        if (itemExists) {
                            c.bank.getItemAmounts()[toBankSlot] += 1;
                            deleteItem((c.inventory.getItemIds()[firstPossibleSlot] - 1),
                                    firstPossibleSlot, 1);
                            amount--;
                        } else {
                            amount = 0;
                        }
                    }
                    resetTempItems();
                    resetBank();
                    return true;
                } else {
                    c.sendMessage("Bank full!");
                    return false;
                }
            }
        } else {
            c.sendMessage("Item not supported " + (c.inventory.getItemIds()[fromSlot] - 1));
            return false;
        }
    }

    /**
     * Checks if you have free bank slots.
     */
    public int freeBankSlots() {
        int freeS = 0;
        for (int i = 0; i < Settings.BANK_SIZE; i++) {
            if (c.bank.getItemIds()[i] <= 0) {
                freeS++;
            }
        }
        return freeS;
    }

    /**
     * Getting items from your bank.
     *
     * @param itemID
     * @param fromSlot
     * @param amount
     */
    public void fromBank(int itemID, int fromSlot, int amount) {
        if (amount > 0) {
            if (c.bank.getItemIds()[fromSlot] > 0) {
                if (!c.takeAsNote) {
                    if (Item.itemStackable[c.bank.getItemIds()[fromSlot] - 1]) {
                        if (c.bank.getItemAmounts()[fromSlot] > amount) {
                            if (addItem((c.bank.getItemIds()[fromSlot] - 1), amount)) {
                                c.bank.getItemAmounts()[fromSlot] -= amount;
                                resetBank();
                                c.getItems().resetItems(5064);
                            }
                        } else {
                            if (addItem((c.bank.getItemIds()[fromSlot] - 1),
                                    c.bank.getItemAmounts()[fromSlot])) {
                                c.bank.getItemIds()[fromSlot] = 0;
                                c.bank.getItemAmounts()[fromSlot] = 0;
                                resetBank();
                                c.getItems().resetItems(5064);
                            }
                        }
                    } else {
                        while (amount > 0) {
                            if (c.bank.getItemAmounts()[fromSlot] > 0) {
                                if (addItem((c.bank.getItemIds()[fromSlot] - 1), 1)) {
                                    c.bank.getItemAmounts()[fromSlot] += -1;
                                    amount--;
                                } else {
                                    amount = 0;
                                }
                            } else {
                                amount = 0;
                            }
                        }
                        resetBank();
                        c.getItems().resetItems(5064);
                    }
                } else if (c.takeAsNote
                        && Item.itemIsNote[c.bank.getItemIds()[fromSlot]]) {
                    if (c.bank.getItemAmounts()[fromSlot] > amount) {
                        if (addItem(c.bank.getItemIds()[fromSlot], amount)) {
                            c.bank.getItemAmounts()[fromSlot] -= amount;
                            resetBank();
                            c.getItems().resetItems(5064);
                        }
                    } else {
                        if (addItem(c.bank.getItemIds()[fromSlot],
                                c.bank.getItemAmounts()[fromSlot])) {
                            c.bank.getItemIds()[fromSlot] = 0;
                            c.bank.getItemAmounts()[fromSlot] = 0;
                            resetBank();
                            c.getItems().resetItems(5064);
                        }
                    }
                } else {
                    c.sendMessage("This item can't be withdrawn as a note.");
                    if (Item.itemStackable[c.bank.getItemIds()[fromSlot] - 1]) {
                        if (c.bank.getItemAmounts()[fromSlot] > amount) {
                            if (addItem((c.bank.getItemIds()[fromSlot] - 1), amount)) {
                                c.bank.getItemAmounts()[fromSlot] -= amount;
                                resetBank();
                                c.getItems().resetItems(5064);
                            }
                        } else {
                            if (addItem((c.bank.getItemIds()[fromSlot] - 1),
                                    c.bank.getItemAmounts()[fromSlot])) {
                                c.bank.getItemIds()[fromSlot] = 0;
                                c.bank.getItemAmounts()[fromSlot] = 0;
                                resetBank();
                                c.getItems().resetItems(5064);
                            }
                        }
                    } else {
                        while (amount > 0) {
                            if (c.bank.getItemAmounts()[fromSlot] > 0) {
                                if (addItem((c.bank.getItemIds()[fromSlot] - 1), 1)) {
                                    c.bank.getItemAmounts()[fromSlot] += -1;
                                    amount--;
                                } else {
                                    amount = 0;
                                }
                            } else {
                                amount = 0;
                            }
                        }
                        resetBank();
                        c.getItems().resetItems(5064);
                    }
                }
            }
        }
    }

    /**
     * Checking item amounts.
     *
     * @param itemID
     * @return
     */
    public int itemAmount(int itemID) {
        int tempAmount = 0;
        for (int i = 0; i < c.inventory.getItemIds().length; i++) {
            if (c.inventory.getItemIds()[i] == itemID) {
                tempAmount += c.inventory.getItemAmounts()[i];
            }
        }
        return tempAmount;
    }

    /**
     * Checks if the item is stackable.
     *
     * @param itemID
     * @return
     */
    public boolean isStackable(int itemID) {
        return Item.itemStackable[itemID];
    }

    /**
     * Updates the equipment tab.
     **/
    public void setEquipment(int wearID, int amount, int targetSlot) {
        synchronized (c) {
            c.getOutStream().createFrameVarSizeWord(34);
            c.getOutStream().writeWord(1688);
            c.getOutStream().writeByte(targetSlot);
            c.getOutStream().writeWord(wearID + 1);
            if (amount > 254) {
                c.getOutStream().writeByte(255);
                c.getOutStream().writeDWord(amount);
            } else {
                c.getOutStream().writeByte(amount);
            }
            c.getOutStream().endFrameVarSizeWord();
            c.flushOutStream();
            c.equipment.getItemIds()[targetSlot] = wearID;
            c.equipment.getItemAmounts()[targetSlot] = amount;
            c.updateRequired = true;
            c.setAppearanceUpdateRequired(true);
        }
    }

    /**
     * Moving Items in your bag.
     **/
    public void moveItems(int from, int to, int moveWindow) {
        if (moveWindow == 3724) {
            int tempI;
            int tempN;
            tempI = c.inventory.getItemIds()[from];
            tempN = c.inventory.getItemAmounts()[from];

            c.inventory.getItemIds()[from] = c.inventory.getItemIds()[to];
            c.inventory.getItemAmounts()[from] = c.inventory.getItemAmounts()[to];
            c.inventory.getItemIds()[to] = tempI;
            c.inventory.getItemAmounts()[to] = tempN;
        }

        if (moveWindow == 34453 && from >= 0 && to >= 0
                && from < Settings.BANK_SIZE && to < Settings.BANK_SIZE
                && to < Settings.BANK_SIZE) {
            int tempI;
            int tempN;
            tempI = c.bank.getItemIds()[from];
            tempN = c.bank.getItemAmounts()[from];

            c.bank.getItemIds()[from] = c.bank.getItemIds()[to];
            c.bank.getItemAmounts()[from] = c.bank.getItemAmounts()[to];
            c.bank.getItemIds()[to] = tempI;
            c.bank.getItemAmounts()[to] = tempN;
        }

        if (moveWindow == 34453) {
            resetBank();
        }
        if (moveWindow == 18579) {
            int tempI;
            int tempN;
            tempI = c.inventory.getItemIds()[from];
            tempN = c.inventory.getItemAmounts()[from];

            c.inventory.getItemIds()[from] = c.inventory.getItemIds()[to];
            c.inventory.getItemAmounts()[from] = c.inventory.getItemAmounts()[to];
            c.inventory.getItemIds()[to] = tempI;
            c.inventory.getItemAmounts()[to] = tempN;
            resetItems(3214);
        }
        resetTempItems();
        if (moveWindow == 3724) {
            resetItems(3214);
        }

    }

    /**
     * Delete item equipment.
     **/
    public void deleteEquipment(int i, int j) {
        synchronized (c) {
            if (PlayerHandler.players[c.playerId] == null) {
                return;
            }
            if (i < 0) {
                return;
            }

            c.equipment.getItemIds()[j] = -1;
            c.equipment.getItemAmounts()[j] = c.equipment.getItemAmounts()[j] - 1;
            c.getOutStream().createFrame(34);
            c.getOutStream().writeWord(6);
            c.getOutStream().writeWord(1688);
            c.getOutStream().writeByte(j);
            c.getOutStream().writeWord(0);
            c.getOutStream().writeByte(0);
            getBonus();
            if (j == c.playerWeapon) {
                sendWeapon(-1, "Unarmed");
            }
            resetBonus();
            getBonus();
            writeBonus();
            c.updateRequired = true;
            c.setAppearanceUpdateRequired(true);
        }
    }

    /**
     * Delete items.
     *
     * @param id
     * @param amount
     */
    public void deleteNonStackable(int id, int amount) {
        if (id <= 0)
            return;
        for (int j = 0; j < c.inventory.getItemIds().length; j++) {
            if (amount <= 0)
                break;
            if (c.inventory.getItemIds()[j] == id + 1) {
                c.inventory.getItemIds()[j] = 0;
                c.inventory.getItemAmounts()[j] = 0;
                amount--;
            }
        }
        resetItems(3214);
    }

    public void deleteItem(int id, int slot, int amount) {
        if (id <= 0 || slot < 0) {
            return;
        }
        if (c.inventory.getItemIds()[slot] == (id + 1)) {
            if (c.inventory.getItemAmounts()[slot] > amount) {
                c.inventory.getItemAmounts()[slot] -= amount;
            } else {
                c.inventory.getItemAmounts()[slot] = 0;
                c.inventory.getItemIds()[slot] = 0;
            }
            resetItems(3214);
        }
    }

    public void deleteItemAmount(int id, int amount) {
        int am = amount;
        for (int i = 0; i < c.inventory.getItemIds().length; i++) {
            if (am == 0) {
                break;
            }
            if (c.inventory.getItemIds()[i] == (id + 1)) {
                if (c.inventory.getItemAmounts()[i] > amount) {
                    c.inventory.getItemAmounts()[i] -= amount;
                    break;
                } else {
                    c.inventory.getItemIds()[i] = 0;
                    c.inventory.getItemAmounts()[i] = 0;
                    am--;
                }
            }
        }
        resetItems(3214);
    }

    /**
     * Delete arrows.
     **/
    public void deleteArrow() {
        synchronized (c) {
            if (c.equipment.getItemIds()[c.playerCape] == 10499 && Misc.random(5) != 1
                    && c.equipment.getItemIds()[c.playerArrows] != 4740)
                return;
            if (c.equipment.getItemAmounts()[c.playerArrows] == 1) {
                c.getItems().deleteEquipment(c.equipment.getItemIds()[c.playerArrows],
                        c.playerArrows);
            }
            if (c.equipment.getItemAmounts()[c.playerArrows] != 0) {
                c.getOutStream().createFrameVarSizeWord(34);
                c.getOutStream().writeWord(1688);
                c.getOutStream().writeByte(c.playerArrows);
                c.getOutStream().writeWord(
                        c.equipment.getItemIds()[c.playerArrows] + 1);
                if (c.equipment.getItemAmounts()[c.playerArrows] - 1 > 254) {
                    c.getOutStream().writeByte(255);
                    c.getOutStream().writeDWord(
                            c.equipment.getItemAmounts()[c.playerArrows] - 1);
                } else {
                    c.getOutStream().writeByte(
                            c.equipment.getItemAmounts()[c.playerArrows] - 1);
                }
                c.getOutStream().endFrameVarSizeWord();
                c.flushOutStream();
                c.equipment.getItemAmounts()[c.playerArrows] -= 1;
            }
            c.updateRequired = true;
            c.setAppearanceUpdateRequired(true);
        }
    }

    public void deleteEquipment() {
        synchronized (c) {
            if (c.equipment.getItemAmounts()[c.playerWeapon] == 1) {
                c.getItems().deleteEquipment(c.equipment.getItemIds()[c.playerWeapon],
                        c.playerWeapon);
            }
            if (c.equipment.getItemAmounts()[c.playerWeapon] != 0) {
                c.getOutStream().createFrameVarSizeWord(34);
                c.getOutStream().writeWord(1688);
                c.getOutStream().writeByte(c.playerWeapon);
                c.getOutStream().writeWord(
                        c.equipment.getItemIds()[c.playerWeapon] + 1);
                if (c.equipment.getItemAmounts()[c.playerWeapon] - 1 > 254) {
                    c.getOutStream().writeByte(255);
                    c.getOutStream().writeDWord(
                            c.equipment.getItemAmounts()[c.playerWeapon] - 1);
                } else {
                    c.getOutStream().writeByte(
                            c.equipment.getItemAmounts()[c.playerWeapon] - 1);
                }
                c.getOutStream().endFrameVarSizeWord();
                c.flushOutStream();
                c.equipment.getItemAmounts()[c.playerWeapon] -= 1;
            }
            c.updateRequired = true;
            c.setAppearanceUpdateRequired(true);
        }
    }

    /**
     * Dropping arrows
     **/
    public void dropArrowNpc() {
        if (c.equipment.getItemIds()[c.playerCape] == 10499)
            return;
        int enemyX = NPCHandler.npcs[c.oldNpcIndex].getX();
        int enemyY = NPCHandler.npcs[c.oldNpcIndex].getY();
        if (Misc.random(10) >= 4) {
            if (GameEngine.itemHandler.itemAmount(c.rangeItemUsed, enemyX, enemyY) == 0) {
                GameEngine.itemHandler.createGroundItem(c, c.rangeItemUsed, enemyX,
                        enemyY, 1, c.getId());
            } else if (GameEngine.itemHandler.itemAmount(c.rangeItemUsed, enemyX,
                    enemyY) != 0) {
                int amount = GameEngine.itemHandler.itemAmount(c.rangeItemUsed,
                        enemyX, enemyY);
                GameEngine.itemHandler.removeGroundItem(c, c.rangeItemUsed, enemyX,
                        enemyY, false);
                GameEngine.itemHandler.createGroundItem(c, c.rangeItemUsed, enemyX,
                        enemyY, amount + 1, c.getId());
            }
        }
    }

    /**
     * Ranging arrows.
     */
    public void dropArrowPlayer() {
        int enemyX = PlayerHandler.players[c.oldPlayerIndex].getX();
        int enemyY = PlayerHandler.players[c.oldPlayerIndex].getY();
        if (c.equipment.getItemIds()[c.playerCape] == 10499)
            return;
        if (Misc.random(10) >= 4) {
            if (GameEngine.itemHandler.itemAmount(c.rangeItemUsed, enemyX, enemyY) == 0) {
                GameEngine.itemHandler.createGroundItem(c, c.rangeItemUsed, enemyX,
                        enemyY, 1, c.getId());
            } else if (GameEngine.itemHandler.itemAmount(c.rangeItemUsed, enemyX,
                    enemyY) != 0) {
                int amount = GameEngine.itemHandler.itemAmount(c.rangeItemUsed,
                        enemyX, enemyY);
                GameEngine.itemHandler.removeGroundItem(c, c.rangeItemUsed, enemyX,
                        enemyY, false);
                GameEngine.itemHandler.createGroundItem(c, c.rangeItemUsed, enemyX,
                        enemyY, amount + 1, c.getId());
            }
        }
    }

    /**
     * Removes all items from player's equipment.
     */
    public void removeAllItems() {
        for (int i = 0; i < c.inventory.getItemIds().length; i++) {
            c.inventory.getItemIds()[i] = 0;
        }
        for (int i = 0; i < c.inventory.getItemAmounts().length; i++) {
            c.inventory.getItemAmounts()[i] = 0;
        }
        resetItems(3214);
    }

    /**
     * Checks if you have a free slot.
     *
     * @return
     */
    public int freeSlots() {
        int freeS = 0;
        for (int i = 0; i < c.inventory.getItemIds().length; i++) {
            if (c.inventory.getItemIds()[i] <= 0) {
                freeS++;
            }
        }
        return freeS;
    }

    /**
     * Finds the item.
     *
     * @param id
     * @param items
     * @param amounts
     * @return
     */
    public int findItem(int id, int[] items, int[] amounts) {
        for (int i = 0; i < c.inventory.getItemIds().length; i++) {
            if (((items[i] - 1) == id) && (amounts[i] > 0)) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the item name from the item.cfg
     *
     * @param ItemID
     * @return
     */
    public String getItemName(int ItemID) {
        for (int i = 0; i < Settings.ITEM_LIMIT; i++) {
            if (GameEngine.itemHandler.itemDefinitions[i] != null) {
                if (GameEngine.itemHandler.itemDefinitions[i].itemId == ItemID) {
                    return GameEngine.itemHandler.itemDefinitions[i].itemName.toLowerCase();
                }
            }
        }
        return "Unarmed";
    }

    /**
     * Gets the item ID from the item.cfg
     *
     * @param itemName
     * @return
     */
    public int getItemId(String itemName) {
        for (int i = 0; i < Settings.ITEM_LIMIT; i++) {
            if (GameEngine.itemHandler.itemDefinitions[i] != null) {
                if (GameEngine.itemHandler.itemDefinitions[i].itemName
                        .equalsIgnoreCase(itemName)) {
                    return GameEngine.itemHandler.itemDefinitions[i].itemId;
                }
            }
        }
        return -1;
    }

    /**
     * Gets the item slot.
     *
     * @param ItemID
     * @return
     */
    public int getItemSlot(int ItemID) {
        for (int i = 0; i < c.inventory.getItemIds().length; i++) {
            if ((c.inventory.getItemIds()[i] - 1) == ItemID) {
                return i;
            }
        }
        return -1;
    }

    /**
     * Gets the item amount.
     *
     * @param ItemID
     * @return
     */
    public int getItemAmount(int ItemID) {
        int itemCount = 0;
        for (int i = 0; i < c.inventory.getItemIds().length; i++) {
            if ((c.inventory.getItemIds()[i] - 1) == ItemID) {
                itemCount += c.inventory.getItemAmounts()[i];
            }
        }
        return itemCount;
    }

    /**
     * Checks if the player has the item.
     *
     * @param itemID
     * @param amt
     * @param slot
     * @return
     */
    public boolean playerHasItem(int itemID, int amt, int slot) {
        itemID++;
        int found = 0;
        if (c.inventory.getItemIds()[slot] == (itemID)) {
            for (int i = 0; i < c.inventory.getItemIds().length; i++) {
                if (c.inventory.getItemIds()[i] == itemID) {
                    if (c.inventory.getItemAmounts()[i] >= amt) {
                        return true;
                    } else {
                        found++;
                    }
                }
            }
            if (found >= amt) {
                return true;
            }
            return false;
        }
        return false;
    }

    public boolean playerHasItem(int itemID) {
        itemID++;
        for (int i = 0; i < c.inventory.getItemIds().length; i++) {
            if (c.inventory.getItemIds()[i] == itemID)
                return true;
        }
        return false;
    }

    public boolean playerHasItem(int itemID, int amt) {
        itemID++;
        int found = 0;
        for (int i = 0; i < c.inventory.getItemIds().length; i++) {
            if (c.inventory.getItemIds()[i] == itemID) {
                if (c.inventory.getItemAmounts()[i] >= amt) {
                    return true;
                } else {
                    found++;
                }
            }
        }
        if (found >= amt) {
            return true;
        }
        return false;
    }

    /**
     * Getting un-noted items.
     *
     * @param ItemID
     * @return
     */
    public int getUnnotedItem(int ItemID) {
        int NewID = ItemID - 1;
        String NotedName = "";
        for (int i = 0; i < Settings.ITEM_LIMIT; i++) {
            if (GameEngine.itemHandler.itemDefinitions[i] != null) {
                if (GameEngine.itemHandler.itemDefinitions[i].itemId == ItemID) {
                    NotedName = GameEngine.itemHandler.itemDefinitions[i].itemName;
                }
            }
        }
        for (int i = 0; i < Settings.ITEM_LIMIT; i++) {
            if (GameEngine.itemHandler.itemDefinitions[i] != null) {
                if (GameEngine.itemHandler.itemDefinitions[i].itemName == NotedName) {
                    if (GameEngine.itemHandler.itemDefinitions[i].itemDescription
                            .startsWith("Swap this note at any bank for a") == false) {
                        NewID = GameEngine.itemHandler.itemDefinitions[i].itemId;
                        break;
                    }
                }
            }
        }
        return NewID;
    }

    /**
     * Dropping items
     **/
    public void createGroundItem(int itemID, int itemX, int itemY,
                                 int itemAmount) {
        synchronized (c) {
            c.getOutStream().createFrame(85);
            c.getOutStream().writeByteC((itemY - 8 * c.mapRegionY));
            c.getOutStream().writeByteC((itemX - 8 * c.mapRegionX));
            c.getOutStream().createFrame(44);
            c.getOutStream().writeWordBigEndianA(itemID);
            c.getOutStream().writeWord(itemAmount);
            c.getOutStream().writeByte(0);
            c.flushOutStream();
        }
    }

    /**
     * Pickup items from the ground.
     **/
    public void removeGroundItem(int itemID, int itemX, int itemY, int Amount) {
        synchronized (c) {
            c.getOutStream().createFrame(85);
            c.getOutStream().writeByteC((itemY - 8 * c.mapRegionY));
            c.getOutStream().writeByteC((itemX - 8 * c.mapRegionX));
            c.getOutStream().createFrame(156);
            c.getOutStream().writeByteS(0);
            c.getOutStream().writeWord(itemID);
            c.flushOutStream();
        }
    }

    /**
     * Checks if a player owns a cape.
     *
     * @return
     */
    public boolean ownsCape() {
        if (c.getItems().playerHasItem(2412, 1)
                || c.getItems().playerHasItem(2413, 1)
                || c.getItems().playerHasItem(2414, 1))
            return true;
        for (int j = 0; j < Settings.BANK_SIZE; j++) {
            if (c.bank.getItemIds()[j] == 2412 || c.bank.getItemIds()[j] == 2413
                    || c.bank.getItemIds()[j] == 2414)
                return true;
        }
        if (c.equipment.getItemIds()[c.playerCape] == 2413
                || c.equipment.getItemIds()[c.playerCape] == 2414
                || c.equipment.getItemIds()[c.playerCape] == 2415)
            return true;
        return false;
    }

    /**
     * Checks if the player has all the shards.
     *
     * @return
     */
    public boolean hasAllShards() {
        return playerHasItem(11712, 1) && playerHasItem(11712, 1)
                && playerHasItem(11714, 1);
    }

    /**
     * Makes the godsword blade.
     */
    public void makeBlade() {
        deleteNonStackable(11710, 1);
        deleteNonStackable(11712, 1);
        deleteNonStackable(11714, 1);
        addItem(11690, 1);
        c.sendMessage("You combine the shards to make a blade.");
    }

    /**
     * Makes the godsword.
     *
     * @param i
     */
    public void makeGodsword(int i) {
        int godsword = i - 8;
        if (playerHasItem(11690) && playerHasItem(i)) {
            deleteNonStackable(11690, 1);
            deleteNonStackable(i, 1);
            addItem(i - 8, 1);
            c.sendMessage("You combine the hilt and the blade to make a godsword.");
        }
    }

    /**
     * Checks if the item is a godsword hilt.
     *
     * @param i
     * @return
     */
    public boolean isHilt(int i) {
        return i >= 11702 && i <= 11708 && i % 2 == 0;
    }

}