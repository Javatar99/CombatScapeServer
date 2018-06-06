package RS2.model.skilling.skills.impl.combat;

import RS2.model.skilling.skills.SkillCollection;

public class Prayer extends CombatSkill{

    public Prayer() {
        super(SkillCollection.PLAYER_PRAYER);
    }

    public void decrementPrayer(){
        this.currentLevel--;
    }

    @Override
    protected void setNewLevel(int newLevel) {
        this.actualLevel = newLevel;
    }

    @Override
    public void restoreStat() {}
}
