package RS2.model.player.packets.objectActions;

import RS2.model.player.Client;

public interface FirstClickObject {

    default void firstClickObject(Client c, int objectId, int objectX, int objectY){
        switch (objectId){
            case 2213:
                c.getPA().openUpBank();
                break;
        }
    }

}
