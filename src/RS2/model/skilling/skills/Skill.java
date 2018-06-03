package RS2.model.skilling.skills;

import RS2.model.player.Client;
import RS2.model.player.Player;
import RS2.model.skilling.AbstractSkill;

public abstract class Skill implements AbstractSkill {

    protected int id;
    protected int currentLevel;
    protected int actualLevel;
    protected int maximumLevel;
    protected int experience;

    @Override
    public void addExperience(Player player, int experience) {
        if((this.experience + experience) >= MAX_EXPERIENCE){
            this.onMaxExperience(player);
            return;
        }
        this.experience += experience;
        if(this.currentLevel < getLevelForXP(this.experience, this.maximumLevel)){
            levelUp(player);
        }
    }

    @Override
    public void levelUp(Player player) {
        if(this.currentLevel < this.maximumLevel){
            this.actualLevel++;
            this.onGainedLevel(player);
            return;
        }
        this.onMaxLevel(player);
    }

    public final void updateSkill(Player player){
        ((Client)player).getPA().refreshSkill(this.id);
    }

    public final void forceMaximumLevel(){
        this.experience = MAX_EXPERIENCE;
        this.currentLevel = this.maximumLevel;
    }

    public abstract void onMaxExperience(Player player);
    public abstract void onMaxLevel(Player player);
    public abstract void onGainedLevel(Player player);

    public int getId() {
        return id;
    }

    public int getCurrentLevel() {
        return currentLevel;
    }

    public int getActualLevel() {
        return actualLevel;
    }

    public int getMaximumLevel() {
        return maximumLevel;
    }

    public int getExperience() {
        return experience;
    }
}
