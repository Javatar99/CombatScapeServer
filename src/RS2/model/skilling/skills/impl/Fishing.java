package RS2.model.skilling.skills.impl;

import RS2.model.player.Player;
import RS2.model.skilling.skills.Skill;
import RS2.model.skilling.skills.SkillCollection;

public class Fishing extends Skill {

    public Fishing() {
        super(SkillCollection.PLAYER_FISHING);
        this.maximumLevel = 99;
    }

    @Override
    public void onMaxExperience(Player player) {

    }

    @Override
    public void onMaxLevel(Player player) {

    }

    @Override
    public void onGainedLevel(Player player) {

    }
}
