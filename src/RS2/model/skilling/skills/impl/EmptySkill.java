package RS2.model.skilling.skills.impl;

import RS2.model.player.Player;
import RS2.model.skilling.skills.Skill;

public class EmptySkill extends Skill {

    public static final EmptySkill EMPTY_SKILL = new EmptySkill();

    private EmptySkill(){
        super(-1);
        this.currentLevel = 1;
        this.experience = 0;
        this.actualLevel = 1;
        this.maximumLevel = 1;
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
