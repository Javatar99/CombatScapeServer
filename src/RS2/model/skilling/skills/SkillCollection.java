package RS2.model.skilling.skills;

import RS2.model.player.Player;
import RS2.model.skilling.skills.impl.combat.CombatSkill;
import RS2.model.skilling.skills.impl.Fishing;
import RS2.model.skilling.skills.impl.combat.Magic;
import RS2.model.skilling.skills.impl.combat.Prayer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.Spliterator;
import java.util.function.Consumer;

public final class SkillCollection implements Iterable<Skill> {

    public static final int PLAYER_ATTACK = 0;
    public static final int PLAYER_DEFENCE = 1;
    public static final int PLAYER_STRENGTH = 2;
    public static final int PLAYER_HITPOINTS = 3;
    public static final int PLAYER_RANGED = 4;
    public static final int PLAYER_PRAYER = 5;
    public static final int PLAYER_MAGIC = 6;
    public static final int PLAYER_COOKING = 7;
    public static final int PLAYER_WOODCUTTING = 8;
    public static final int PLAYER_FLETCHING = 9;
    public static final int PLAYER_FISHING = 10;
    public static final int PLAYER_FIREMAKING = 11;
    public static final int PLAYER_CRAFTING = 12;
    public static final int PLAYER_SMITHING = 13;
    public static final int PLAYER_MINING = 14;
    public static final int PLAYER_HERBLORE = 15;
    public static final int PLAYER_AGILITY = 16;
    public static final int PLAYER_THIEVING = 17;
    public static final int PLAYER_SLAYER = 18;
    public static final int PLAYER_FARMING = 19;
    public static final int PLAYER_RUNECRAFTING = 20;

    private Player player;
    private Skill[] skills;

    public SkillCollection(Player c) {
        this.player = c;
        this.skills = new Skill[25];
        Arrays.fill(this.skills, null);
        setCombatSkills();
        this.skills[PLAYER_FISHING] = new Fishing();
    }

    private void setCombatSkills() {
        this.skills[PLAYER_ATTACK] = new CombatSkill(PLAYER_ATTACK);
        this.skills[PLAYER_DEFENCE] = new CombatSkill(PLAYER_DEFENCE);
        this.skills[PLAYER_STRENGTH] = new CombatSkill(PLAYER_STRENGTH);
        this.skills[PLAYER_HITPOINTS] = new CombatSkill(PLAYER_HITPOINTS);
        this.skills[PLAYER_RANGED] = new CombatSkill(PLAYER_RANGED);
        this.skills[PLAYER_PRAYER] = new Prayer();
        this.skills[PLAYER_MAGIC] = new Magic();
    }

    public void refreshSkill(final int id){
        if(this.skills[id] != null){
            this.skills[id].updateSkill(this.player);
        }
    }

    public void addExperience(final int id, final int experience){
        if(this.skills[id] != null)
            this.skills[id].addExperience(this.player, experience);
    }

    public void masterAllSkills(){
        for(Skill s: skills){
            if(s != null) {
                s.forceMaximumLevel();
                s.updateSkill(this.player);
            }
        }
    }

    public Skill getSkill(final int id){
        return this.skills[id];
    }

    @Override
    public Iterator<Skill> iterator() {
        return new Iterator<>() {
            private int pos = 0;
            @Override
            public boolean hasNext() {
                return pos < SkillCollection.this.skills.length;
            }

            @Override
            public Skill next() {
                return SkillCollection.this.skills[pos++];
            }
        };
    }
}
