package RS2.model.skilling.skills.impl.combat;

import RS2.model.player.Client;
import RS2.model.player.Player;
import RS2.model.skilling.skills.Skill;
import RS2.util.Misc;

public class CombatSkill extends Skill {

    public CombatSkill(final int id) {
        super(id);
    }

    @Override
    public void onMaxExperience(Player player) {}

    @Override
    public void onMaxLevel(Player player) {}

    @Override
    public void onGainedLevel(Player player) {
        ((Client)player).sendMessage("You have gained a level in " + Misc.getSkillName(this.id));
    }
}
