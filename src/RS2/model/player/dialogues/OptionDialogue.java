package RS2.model.player.dialogues;

import RS2.model.player.Client;

/**
 * @author david (Javatar)
 */

public class OptionDialogue {

    private OptionAction[] doAction;
    private Client c;

    public OptionDialogue(Client c) {
        this.c = c;
        this.doAction = new OptionAction[5];
        c.getPA().sendFrame126("Select an Option", 2493);
        c.getPA().sendFrame126("", 2494);
        c.getPA().sendFrame126("", 2495);
        c.getPA().sendFrame126("", 2496);
        c.getPA().sendFrame126("", 2497);
        c.getPA().sendFrame126("", 2498);
    }

    public void setOption(String msg, int id, OptionAction action){
        this.doAction[id] = action;
        c.getPA().sendFrame126(msg, (id + 2494));
    }

    public boolean doAction(final int id){
        return this.doAction[(id - 1)].doAction(c);
    }

    public void show(){
        c.currentOptionDialogue = this;
        c.getPA().sendFrame164(2492);
    }
}
