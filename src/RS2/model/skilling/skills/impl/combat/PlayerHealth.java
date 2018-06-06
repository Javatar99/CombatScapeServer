package RS2.model.skilling.skills.impl.combat;

import RS2.model.skilling.skills.SkillCollection;

public class PlayerHealth extends CombatSkill {
    public PlayerHealth() {
        super(SkillCollection.PLAYER_HITPOINTS);
        this.currentLevel = 10;
        this.actualLevel = 10;
        this.experience = 1300;
        this.maximumLevel = 99;
    }

    @Override
    protected void setNewLevel(int newLevel) {
        this.actualLevel = newLevel;
    }
}
