package RS2.model.npc.handlers;

import RS2.GameEngine;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.List;

public class NpcSpawnDefinition {

    public static  List<NpcSpawnDefinition> npcSpawnDefinitions = new ArrayList<>();

    private int npcType;
    private int x;
    private int y;
    private int heightLevel;
    private int walkingType;
    private int HP;
    private int maxHit;
    private int attack;
    private int defence;

    public NpcSpawnDefinition(int npcType, int x, int y, int heightLevel, int walkingType, int HP, int maxHit, int attack, int defence) {
        this.npcType = npcType;
        this.x = x;
        this.y = y;
        this.heightLevel = heightLevel;
        this.walkingType = walkingType;
        this.HP = HP;
        this.maxHit = maxHit;
        this.attack = attack;
        this.defence = defence;
    }

    static void loadDefinitions(NPCHandler handler){
        try {
            npcSpawnDefinitions = new Gson().fromJson(new FileReader("Data/cfg/npcSpawns.json"),
                    new TypeToken<List<NpcSpawnDefinition>>(){}.getType());
            npcSpawnDefinitions.forEach(def -> handler.newNPC(def.npcType, def.x, def.y, def.heightLevel, def.walkingType, def.HP
            , def.maxHit, def.attack, def.defence));
            if(npcSpawnDefinitions == null)
                npcSpawnDefinitions = new ArrayList<>();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

}

