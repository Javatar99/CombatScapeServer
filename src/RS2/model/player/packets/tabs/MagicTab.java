package RS2.model.player.packets.tabs;

import RS2.model.player.Client;

public interface MagicTab {
    int MAGIC_TAB = 1151;
    enum Teleports{
        FALADOR(3012, 3364, 0, 18);
        private int x, y, h, id;
        Teleports(int x, int y, int h, int id) {
            this.x = x;
            this.y = y;
            this.h = h;
            this.id = id;
        }
    }
    default void teleportMagicTab(Client c, int id){
        for(Teleports t: Teleports.values()){
            if(t.id == id){
                c.getPA().startTeleport2(t.x, t.y, t.h);
            }
        }
    }
}
