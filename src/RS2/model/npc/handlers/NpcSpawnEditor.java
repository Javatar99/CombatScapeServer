package RS2.model.npc.handlers;

import RS2.model.player.Client;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.util.ArrayList;
import java.util.List;
import java.util.Stack;

public class NpcSpawnEditor {

    public static final NpcSpawnEditor npcSpawnEditor = new NpcSpawnEditor(new ArrayList<>(), new Stack<>());

    private List<NpcSpawnDefinition> newSpawns;
    private Stack<NpcSpawnDefinition> actions;

    private NpcSpawnEditor(List<NpcSpawnDefinition> newSpawns, Stack<NpcSpawnDefinition> actions) {
        this.newSpawns = newSpawns;
        this.actions = actions;
    }

    public void addSpawn(NpcSpawnDefinition def){
        actions.push(def);
    }

    public void undoSpawn(){
        actions.pop();
    }

    public void finish(){
        for(NpcSpawnDefinition d = actions.pop(); d != null; d = actions.pop()){
            newSpawns.add(d);
        }
    }

    public void restartEditor(){
        this.newSpawns.clear();
        this.actions.clear();
    }

    public void saveAllNewSpawns(){
        Gson gson = new GsonBuilder().disableHtmlEscaping().setPrettyPrinting().create();
        final String data = gson.toJson(newSpawns);
        try {
            Files.write(new File("Data/cfg/npcSpawns.json").toPath(),
                    data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
