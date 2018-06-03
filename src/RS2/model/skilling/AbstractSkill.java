package RS2.model.skilling;

import RS2.model.player.Player;

public interface AbstractSkill {
    int MAX_EXPERIENCE = 200000000;
    void addExperience(Player player, final int experience);
    void levelUp(Player player);

    default int getLevelForXP(int exp, int maxLevel) {
        int points = 0;
        int output;
        if(exp >= MAX_EXPERIENCE){
            return maxLevel;
        }
        for (int lvl = 1; lvl <= maxLevel; lvl++) {
            points += Math.floor(lvl + 300.0
                    * Math.pow(2.0, lvl / 7.0));
            output = (int) Math.floor(points / 4);
            if (output >= exp) {
                return lvl;
            }
        }
        return 0;
    }
}
