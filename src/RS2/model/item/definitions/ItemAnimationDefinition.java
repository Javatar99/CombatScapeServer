package RS2.model.item.definitions;

import RS2.GameEngine;
import RS2.Settings;
import RS2.model.player.Client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;

public class ItemAnimationDefinition {

    public static final ItemAnimationDefinition[] animationDefinitions = new ItemAnimationDefinition[Settings.ITEM_LIMIT];

    private int itemId;
    private int walkAnimation;
    private int standAnimation;
    private int runAnimation;
    private int attackAnimation;
    private int blockAnimation;

    public ItemAnimationDefinition(int itemId, int walkAnimation, int standAnimation, int runAnimation, int attackAnimation, int blockAnimation) {
        this.itemId = itemId;
        this.walkAnimation = walkAnimation;
        this.standAnimation = standAnimation;
        this.runAnimation = runAnimation;
        this.attackAnimation = attackAnimation;
        this.blockAnimation = blockAnimation;
    }

    public int getItemId() {
        return itemId;
    }

    public int getStandAnimation() {
        return standAnimation;
    }

    public int getRunAnimation() {
        return runAnimation;
    }

    public int getAttackAnimation() {
        return attackAnimation;
    }

    public int getBlockAnimation() {
        return blockAnimation;
    }

    public int getWalkAnimation() {
        return walkAnimation;
    }

    public static void writeDefinitions(){
        Gson gson = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
        try {
            Files.write(new File("Data/cfg/ItemAnimationDefinitions.json").toPath(),
                    gson.toJson(animationDefinitions).getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void saveOldSystem(Client c){
        for(ItemDefinition def: GameEngine.itemHandler.itemDefinitions){
            if (def != null) {
                int attackAnim = c.getCombat().getWeaponAnimation(def.itemName, def.itemId);
                int blockAnim = c.getCombat().getBlockAnimation(def.itemId);
                if(!c.getCombat().getIdleAnimations(def.itemName, def.itemId)){
                    continue;
                }
                animationDefinitions[def.itemId] =
                        new ItemAnimationDefinition(def.itemId, c.playerWalkIndex, c.playerStandIndex, c.playerRunIndex, attackAnim, blockAnim);
            }
        }
        writeDefinitions();
    }

    public static void loadDefinitions(){
        try {
            ItemAnimationDefinition[] list =
                    new Gson().fromJson(
                            new FileReader("Data/cfg/ItemAnimationDefinitions.json"),
                            ItemAnimationDefinition[].class
                    );
            for(ItemAnimationDefinition def: list){
                if (def != null) {
                    animationDefinitions[def.itemId] = def;
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }
}
