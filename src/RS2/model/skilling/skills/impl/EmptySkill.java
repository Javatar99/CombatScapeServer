package RS2.model.skilling.skills.impl;

import RS2.model.player.Player;
import RS2.model.skilling.skills.Skill;

public class EmptySkill extends Skill {

    public EmptySkill(int id){
        super(id);
    }

    @Override
    public void onMaxExperience(Player player) {}

    @Override
    public void onMaxLevel(Player player) {}

    @Override
    public void onGainedLevel(Player player) {}

    @Override
    public void addExperience(Player player, int experience) {}

    @Override
    public void levelUp(Player player) {}
}
