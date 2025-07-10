package net.elarisrpg.classsystem;

import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.util.INBTSerializable;

public class PlayerClassProvider implements IPlayerClass, INBTSerializable<CompoundTag> {

    private PlayerClass playerClass = PlayerClass.NONE;

    @Override
    public PlayerClass getPlayerClass() {
        return playerClass;
    }

    @Override
    public void setPlayerClass(PlayerClass playerClass) {
        this.playerClass = playerClass;
    }

    @Override
    public CompoundTag serializeNBT() {
        CompoundTag tag = new CompoundTag();
        tag.putString("PlayerClass", playerClass.name());
        return tag;
    }

    @Override
    public void deserializeNBT(CompoundTag nbt) {
        playerClass = PlayerClass.fromString(nbt.getString("PlayerClass"));
    }
}
