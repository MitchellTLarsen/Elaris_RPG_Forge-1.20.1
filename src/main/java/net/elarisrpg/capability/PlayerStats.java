package net.elarisrpg.capability;

import net.minecraft.nbt.CompoundTag;

public class PlayerStats implements IPlayerStats{

    private int xp = 0;
    private int level = 1;
    private int skillPoints = 0;

    @Override
    public int getXp() {
        return xp;
    }

    @Override
    public void setXp(int xp) {
        this.xp = xp;
    }

    @Override
    public int getLevel() {
        return level;
    }

    @Override
    public void setLevel(int level) {
        this.level = level;
    }

    @Override
    public int getSkillPoints() {
        return skillPoints;
    }

    @Override
    public void setSkillPoints(int skillPoints) {
        this.skillPoints = skillPoints;
    }

    /**
     * Save data to NBT.
     */
    public void saveNBTData(CompoundTag nbt) {
        nbt.putInt("xp", xp);
        nbt.putInt("level", level);
        nbt.putInt("skillPoints", skillPoints);
    }

    /**
     * Load data from NBT.
     */
    public void loadNBTData(CompoundTag nbt) {
        this.xp = nbt.getInt("xp");
        this.level = nbt.getInt("level");
        this.skillPoints = nbt.getInt("skillPoints");
    }
}
