package RS2.model.item.definitions;

import RS2.GameEngine;
import RS2.model.item.Item;
import RS2.model.player.Client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Arrays;

/**
 * Gets Items From The Item List
 * 
 * @author Sanity Revised by Shawn Notes by Shawn
 */
public class ItemDefinition {
	public int itemId;
	public String itemName;
	public String itemDescription;
	public double ShopValue;
	public double LowAlch;
	public double HighAlch;
	public int[] Bonuses = new int[12];
	public int[] levelRequirements = new int[25];
	public int targetSlot;

	/**
	 * Gets the item ID.
	 * 
	 * @param _itemId
	 */
	public ItemDefinition(int _itemId) {
		itemId = _itemId;
	}

	public ItemAnimationDefinition getAnimations(){
		return ItemAnimationDefinition.animationDefinitions[this.itemId];
	}

	public static void convertOldToNewDefinitions(Client c){
		for(ItemDefinition def: GameEngine.itemHandler.itemDefinitions){
			if (def != null) {
				c.getItems().getRequirements(def.itemName, def.itemId);
				def.targetSlot = Item.targetSlots[def.itemId];
				Arrays.fill(def.levelRequirements, 0);
				def.levelRequirements[c.playerAttack] = c.attackLevelReq;
				def.levelRequirements[c.playerMagic] = c.magicLevelReq;
				def.levelRequirements[c.playerDefence] = c.defenceLevelReq;
				def.levelRequirements[c.playerStrength] = c.strengthLevelReq;
				def.levelRequirements[c.playerRanged] = c.rangeLevelReq;

				if(def.LowAlch <= 0){
					def.LowAlch = def.ShopValue * 0.25;
				}
				if(def.HighAlch <= 0){
					def.HighAlch = def.ShopValue * 0.75;
					if(def.HighAlch <= 0)
						def.HighAlch = 2;
				}
			}
		}
		Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
		try {
			Files.write(new File("Data/cfg/ItemDefinitions.json").toPath(),
					gson.toJson(GameEngine.itemHandler.itemDefinitions).getBytes());
			c.sendMessage("Finished converting items.");
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
}
