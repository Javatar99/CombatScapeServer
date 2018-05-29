package RS2.model.npc.handlers;

import RS2.GameEngine;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.reflect.TypeToken;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class NpcSpawnEditor {

    public static final NpcSpawnEditor npcSpawnEditor = new NpcSpawnEditor(new ArrayList<>(), new Stack<>());

    private List<NpcSpawnDefinition> newSpawns;
    private Stack<NpcSpawnDefinition> actions;
    private Stack<Integer> npcIndexs;

    private NpcSpawnEditor(List<NpcSpawnDefinition> newSpawns, Stack<NpcSpawnDefinition> actions) {
        this.newSpawns = newSpawns;
        this.actions = actions;
        this.npcIndexs = new Stack<>();
    }

    public void addSpawn(NpcSpawnDefinition def) {
        actions.push(def);
    }

    public void undoSpawn() {
        if (!actions.isEmpty())
            actions.pop();
    }

    public void finish() {
        while (!actions.isEmpty()){
            newSpawns.add(actions.pop());
        }
    }

    public void showNPCS() {
        if (newSpawns.isEmpty())
            return;
        if(!npcIndexs.isEmpty()){
            deleteSpawnedNpcs();
        }
        newSpawns.forEach(s -> {
            int slot = GameEngine.npcHandler.newNPC(
                    s.getNpcType(), s.getX(), s.getY(), s.getHeightLevel(),
                    s.getWalkingType(), s.getHP(), s.getMaxHit(), s.getAttack(),
                    s.getDefence()
            );
            if (slot != -1)
                npcIndexs.push(slot);
        });
    }

    public void deleteSpawnedNpcs() {
        for (int t = npcIndexs.pop(); !npcIndexs.isEmpty(); t = npcIndexs.pop()) {
            NPCHandler.npcs[t] = null;
        }
    }

    public void restartEditor() {
        this.newSpawns.clear();
        this.actions.clear();
    }

    public void saveAllNewSpawns() {
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        final String data = gson.toJson(newSpawns);
        try {
            Files.write(new File("Data/cfg/npcSpawns.json").toPath(),
                    data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void checkEditor() {
        System.out.println("Actions Size: " + actions.size());
        System.out.println("Index Size: " + npcIndexs.size());
        System.out.println("New Spawns: " + newSpawns.size());
    }

    public void loadSpawns(){
        try {
            newSpawns = new Gson().fromJson(new FileReader("Data/cfg/npcSpawns.json"),
                    new TypeToken<List<NpcSpawnDefinition>>(){}.getType());
            newSpawns.forEach(def -> GameEngine.npcHandler.newNPC(def.getNpcType(), def.getX(), def.getY(), def.getHeightLevel(), def.getWalkingType(), def.getHP()
                    , def.getMaxHit(), def.getAttack(), def.getDefence()));
            if(newSpawns == null)
                newSpawns = new ArrayList<>();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
    }

}
