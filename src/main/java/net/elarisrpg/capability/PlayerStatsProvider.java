package net.elarisrpg.capability;

import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ICapabilitySerializable;
import net.minecraftforge.common.util.LazyOptional;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

/**
 * Provides access to our capability on the player.
 *
 * This class connects the Forge capability system to our custom PlayerStats object,
 * and handles saving/loading the data to NBT for persistence across logouts, deaths, etc.
 */
public class PlayerStatsProvider implements ICapabilitySerializable<CompoundTag> {

    // The actual player stats data object we're storing for this player
    private final PlayerStats stats;

    // A LazyOptional wrapper to safely expose our capability
    private final LazyOptional<IPlayerStats> lazyOptional;

    /**
     * Constructor - create a new provider around a PlayerStats instance.
     *
     * @param stats the PlayerStats data object to hold
     */
    public PlayerStatsProvider(PlayerStats stats) {
        this.stats = stats;
        this.lazyOptional = LazyOptional.of(() -> stats);
    }

    /**
     * Called by Forge whenever code wants access to a capability on this player entity.
     *
     * @param cap  the capability being requested
     * @param side which side (e.g. NORTH, SOUTH) - usually irrelevant for player capabilities
     * @return a LazyOptional containing our PlayerStats if the request matches
     */
    @Nonnull
    @Override
    public <T> LazyOptional<T> getCapability(@Nonnull Capability<T> cap, @Nullable Direction side) {
        return cap == ModCapabilities.PLAYER_STATS
                ? lazyOptional.cast() // return our stats if it's the right capability
                : LazyOptional.empty(); // otherwise return empty
    }

    /**
     * Saves our capability data into NBT so Forge can persist it.
     * Forge calls this automatically during world saves, player saves, etc.
     *
     * @return a CompoundTag containing all our player stats data
     */
    public CompoundTag serializeNBT() {
        CompoundTag nbt = new CompoundTag();
        stats.saveNBTData(nbt); // dump our PlayerStats fields into the NBT
        System.out.println("[ElarisRPG] Saving stats: " + nbt);
        return nbt;
    }

    /**
     * Loads our capability data back from NBT.
     * Called automatically by Forge when the player loads from disk.
     *
     * @param nbt the CompoundTag previously written during serializeNBT()
     */
    public void deserializeNBT(CompoundTag nbt) {
        stats.loadNBTData(nbt);
    }
}
