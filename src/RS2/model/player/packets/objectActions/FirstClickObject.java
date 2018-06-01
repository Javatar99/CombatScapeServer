package RS2.model.player.packets.objectActions;

import RS2.model.player.Client;

public interface FirstClickObject {

    default void firstClickObject(Client c, int objectId, int objectX, int objectY){
        switch (objectId){
            case 2213:
            case 11758:
                c.getPA().openUpBank();
                break;
            case 2113:
                c.getPA().movePlayer(3680, 9895, 0);
                c.sendMessage("You have climbed into a mine.");
                break;
            case 1755:
                c.getPA().movePlayer(3003, 3383, 0);
                break;
            case 9398:
                c.getItems().bankInventory();
                break;
        }
    }

}
