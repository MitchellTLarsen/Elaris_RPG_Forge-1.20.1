package net.elarisrpg.capability;

import net.minecraft.world.entity.player.Player;

import java.util.Optional;

public class PlayerStatsHelper {
    /**
     * Utility class for quickly accessing player stats.
     */
    public class playerStatsHelper {
        public static Optional<IPlayerStats> get(Player player) {
            return player.getCapability(ModCapabilities.PLAYER_STATS).resolve();
        }
    }
}
