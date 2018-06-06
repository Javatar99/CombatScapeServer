package RS2.model.player.packets;

import RS2.GameEngine;
import RS2.model.player.Client;
import RS2.model.player.PacketType;

import static java.lang.System.currentTimeMillis;


/**
 * Magic on floor items
 **/
@SuppressWarnings("all")
public class MagicOnFloorItems implements PacketType {

	@Override
	public void processPacket(Client c, int packetType, int packetSize) {
		int itemY = c.getInStream().readSignedWordBigEndian();
		int itemId = c.getInStream().readUnsignedWord();
		int itemX = c.getInStream().readSignedWordBigEndian();
		int spellId = c.getInStream().readUnsignedWordA();

		if(!GameEngine.itemHandler.itemExists(itemId, itemX, itemY)) {
			c.stopMovement();
			return;
		}
		c.usingMagic = true;
		if(!c.getCombat().checkMagicReqs(51)) {
			c.stopMovement();
			return;
		}
		
		if(c.goodDistance(c.getX(), c.getY(), itemX, itemY, 12)) {
            int offY = (c.getX() - itemX) * -1;
            int offX = (c.getY() - itemY) * -1;
            c.teleGrabX = itemX;
            c.teleGrabY = itemY;
            c.teleGrabItem = itemId;
            c.turnPlayerTo(itemX, itemY);
            c.teleGrabDelay = currentTimeMillis();
            c.startAnimation(c.MAGIC_SPELLS[51][2]);
            c.gfx100(c.MAGIC_SPELLS[51][3]);
            c.getPA().createPlayersStillGfx(144, itemX, itemY, 0, 72);
            c.getPA().createPlayersProjectile(c.getX(), c.getY(), offX, offY, 50, 70, c.MAGIC_SPELLS[51][4], 50, 10, 0, 50);
            c.getPA().addSkillXP(c.MAGIC_SPELLS[51][7], 6);
            c.skills.getSkill(6).updateSkill(c);
            c.stopMovement();
        }
	}

}
