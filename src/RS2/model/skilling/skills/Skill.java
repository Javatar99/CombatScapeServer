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

    public Skill(int id) {
        this.id = id;
        this.currentLevel = 1;
        this.experience = 0;
        this.actualLevel = 1;
        this.maximumLevel = 1;
    }

    @Override
    public void addExperience(Player player, int experience) {
        if ((this.experience + experience) >= MAX_EXPERIENCE) {
            this.onMaxExperience(player);
            return;
        }
        this.experience += experience;
        final int levelFromXP = getLevelForXP(this.experience, this.maximumLevel);
        if (this.actualLevel < levelFromXP) {
            this.setNewLevel(levelFromXP);
            levelUp(player);
        }
    }

    @Override
    public void levelUp(Player player) {
        if (this.actualLevel < this.maximumLevel) {
            ((Client) player).getPA().showLevelUpInterface(this.id);
            player.gfx100(199);
            ((Client) player).getPA().requestUpdates();
            this.onGainedLevel(player);
            return;
        }
        this.onMaxLevel(player);
    }

    public void updateSkill(Player player) {
        switch (this.id) {
            case 0:
                sendSkillTabUpdate((Client) player, 0, 4004, 4005, 4044, 4045);
                break;

            case 1:
                sendSkillTabUpdate((Client) player, 1, 4008, 4009, 4056, 4057);
                break;

            case 2:
                sendSkillTabUpdate((Client) player, 2, 4006, 4007, 4050, 4051);
                break;

            case 3:
                sendSkillTabUpdate((Client) player, 3, 4016, 4017, 4080, 4081);
                break;

            case 4:
                sendSkillTabUpdate((Client) player, 4, 4010, 4011, 4062, 4063);
                break;

            case 5:
                sendSkillTabUpdate((Client) player, 5, 4012, 4013, 4068, 4069);
                ((Client) player).getPA().sendFrame126("" + ((Client) player).skills.getSkill(5).getCurrentLevel() + "/" + ((Client) player).skills.getSkill(5).getActualLevel() + "", 687);//Prayer frame
                break;

            case 6:
                sendSkillTabUpdate((Client) player, 6, 4014, 4015, 4074, 4075);
                break;

            case 7:
                sendSkillTabUpdate((Client) player, 7, 4034, 4035, 4134, 4135);
                break;

            case 8:
                sendSkillTabUpdate((Client) player, 8, 4038, 4039, 4146, 4147);
                break;

            case 9:
                sendSkillTabUpdate((Client) player, 9, 4026, 4027, 4110, 4111);
                break;

            case 10:
                sendSkillTabUpdate((Client) player, 10, 4032, 4033, 4128, 4129);
                break;

            case 11:
                sendSkillTabUpdate((Client) player, 11, 4036, 4037, 4140, 4141);
                break;

            case 12:
                sendSkillTabUpdate((Client) player, 12, 4024, 4025, 4104, 4105);
                break;

            case 13:
                sendSkillTabUpdate((Client) player, 13, 4030, 4031, 4122, 4123);
                break;

            case 14:
                sendSkillTabUpdate((Client) player, 14, 4028, 4029, 4116, 4117);
                break;

            case 15:
                sendSkillTabUpdate((Client) player, 15, 4020, 4021, 4092, 4093);
                break;

            case 16:
                sendSkillTabUpdate((Client) player, 16, 4018, 4019, 4086, 4087);
                break;

            case 17:
                sendSkillTabUpdate((Client) player, 17, 4022, 4023, 4098, 4099);
                break;

            case 18:
                sendSkillTabUpdate((Client) player, 18, 12166, 12167, 12171, 12172);
                break;

            case 19:
                sendSkillTabUpdate((Client) player, 19, 13926, 13927, 13921, 13922);
                break;

            case 20:
                sendSkillTabUpdate((Client) player, 20, 4152, 4153, 4157, 4158);
                break;
        }
    }

    private void sendSkillTabUpdate(Client player, int i, int i2, int i3, int i4, int i5) {
        int currentLevel = player.skills.getSkill(i).getCurrentLevel();
        int actualLevel = player.skills.getSkill(i).getActualLevel();
        int experience = player.skills.getSkill(i).getExperience();
        int xpForLevel = player.getPA().getXPForLevel(actualLevel + 1);
        player.getPA().sendFrame126("" + currentLevel + "", i2);
        player.getPA().sendFrame126("" + actualLevel + "", i3);
        player.getPA().sendFrame126("" + experience + "", i4);
        player.getPA().sendFrame126("" + xpForLevel + "", i5);
    }

    public final void forceMaximumLevel() {
        this.experience = MAX_EXPERIENCE;
        this.currentLevel = this.maximumLevel;
    }

    public void set(int value) {
        if (value > this.maximumLevel) {
            this.currentLevel = getLevelForXP(value, this.maximumLevel);
            this.actualLevel = this.currentLevel;
            this.experience = value;
        } else {
            this.currentLevel = value;
            this.actualLevel = value;
            this.experience = getXPForLevel(value);
        }
    }

    public void reset() {
        this.currentLevel = 1;
        this.actualLevel = 1;
        this.experience = 0;
    }

    public void restoreStat() {
        if ((this.currentLevel + 1) < this.actualLevel) {
            this.currentLevel++;
        } else if ((this.currentLevel + 1) > this.actualLevel) {
            this.currentLevel--;
        }
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

    public void setCurrentLevel(int currentLevel) {
        this.currentLevel = currentLevel;
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

    public void setExperience(int experience) {
        this.experience = experience;
    }

    public void decrementCurrentLevel(final int by) {
        this.currentLevel -= by;
    }

    protected void setNewLevel(int newLevel) {
        this.currentLevel = newLevel;
        this.actualLevel = newLevel;
    }
}
