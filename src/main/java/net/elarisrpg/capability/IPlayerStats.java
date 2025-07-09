package net.elarisrpg.capability;

public interface IPlayerStats {

    int getXp();
    int getLevel();
    int getSkillPoints();

    void setXp(int xp);
    void setLevel(int level);
    void setSkillPoints(int skillPoints);

}
