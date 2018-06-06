package RS2.model.skilling.skills.impl.combat;

import RS2.model.player.Client;
import RS2.model.player.Player;
import RS2.model.skilling.skills.Skill;
import RS2.model.skilling.skills.SkillCollection;
import RS2.util.Misc;

public class CombatSkill extends Skill {

    public CombatSkill(final int id) {
        super(id);
        this.maximumLevel = 99;
    }

    @Override
    public void onMaxExperience(Player player) {}

    @Override
    public void onMaxLevel(Player player) {}

    @Override
    public void onGainedLevel(Player player) {
        ((Client)player).sendMessage("You have gained a level in " + Misc.getSkillName(this.id));
    }

    @Override
    public void reset() {
        if(this.id == SkillCollection.PLAYER_HITPOINTS){
            this.currentLevel = 10;
            this.actualLevel = 10;
            this.experience = 1300;
        } else {
            this.currentLevel = 1;
            this.actualLevel = 1;
            this.experience = 0;
        }
    }
}
