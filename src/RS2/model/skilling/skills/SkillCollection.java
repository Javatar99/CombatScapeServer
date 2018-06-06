package RS2.model.skilling.skills;

import RS2.model.player.Player;
import RS2.model.skilling.skills.impl.EmptySkill;
import RS2.model.skilling.skills.impl.Fishing;
import RS2.model.skilling.skills.impl.combat.CombatSkill;
import RS2.model.skilling.skills.impl.combat.Magic;
import RS2.model.skilling.skills.impl.combat.PlayerHealth;
import RS2.model.skilling.skills.impl.combat.Prayer;

import java.util.Arrays;
import java.util.Iterator;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

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
    private Prayer prayerRef;
    private Magic magicRef;

    public SkillCollection(Player c) {
        this.player = c;
        this.skills = new Skill[25];
        for (int i = 0; i < this.skills.length; i++) {
            this.skills[i] = new EmptySkill(i);
        }
        setCombatSkills();
        this.skills[PLAYER_FISHING] = new Fishing();
    }

    private void setCombatSkills() {
        this.skills[PLAYER_ATTACK] = new CombatSkill(PLAYER_ATTACK);
        this.skills[PLAYER_DEFENCE] = new CombatSkill(PLAYER_DEFENCE);
        this.skills[PLAYER_STRENGTH] = new CombatSkill(PLAYER_STRENGTH);
        this.skills[PLAYER_HITPOINTS] = new PlayerHealth();
        this.skills[PLAYER_RANGED] = new CombatSkill(PLAYER_RANGED);
        this.skills[PLAYER_PRAYER] = prayerRef = new Prayer();
        this.skills[PLAYER_MAGIC] = magicRef = new Magic();
    }

    public void refreshSkill(final int id) {
        this.skills[id].updateSkill(this.player);
    }

    public void addExperience(final int id, final int experience) {
        this.skills[id].addExperience(this.player, experience);
    }

    public void masterAllSkills() {
        for (Skill s : skills) {
            s.forceMaximumLevel();
            s.updateSkill(this.player);
        }
    }

    public Skill getSkill(final int id) {
        return this.skills[id];
    }

    public final Stream<Skill> stream() {
        return StreamSupport.stream(this.spliterator(), false);
    }

    public void addHitpoints(final int adding) {
        this.skills[PLAYER_HITPOINTS].currentLevel += adding;
        this.skills[PLAYER_HITPOINTS].updateSkill(this.player);
    }

    public void addPrayerPoints(final int adding) {
        this.prayerRef.currentLevel += adding;
        this.prayerRef.updateSkill(this.player);
    }

    public final Prayer getPrayer() {
        return this.prayerRef;
    }

    public final Magic getMagic() {
        return this.magicRef;
    }

    public final int size(){
        return this.skills.length;
    }

    public final int getTotalLevel(){
        return stream().mapToInt(Skill::getActualLevel).sum();
    }

    public final void refreshAll(){
        forEach(s -> s.updateSkill(this.player));
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
