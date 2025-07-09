package net.elarisrpg;

import net.elarisrpg.capability.IPlayerStats;
import net.elarisrpg.capability.ModCapabilities;
import net.elarisrpg.network.PlayerStatsSyncPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

public class XPManager {

    /**
     * Grants XP to the player and handles leveling up.
     *
     * @param player the player receiving XP
     * @param amount the amount of XP to add
     */
    public static void giveXp(ServerPlayer player, int amount) {
        player.getCapability(ModCapabilities.PLAYER_STATS).ifPresent(stats -> {
            int currentXp = stats.getXp();
            int newXp = currentXp + amount;
            stats.setXp(newXp);

            // Check for level up
            checkLevelUp(stats, player);

            player.sendSystemMessage(
                    Component.literal("§b[DEBUG] You gained " + amount + " XP!")
            );

        });
    }

    private static void checkLevelUp(IPlayerStats stats, ServerPlayer player) {
        int currentLevel = stats.getLevel();
        int xpRequired = Math.max(currentLevel * 100, 100);

        while (stats.getXp() >= xpRequired) {
            stats.setXp(stats.getXp() - xpRequired);
            stats.setLevel(currentLevel + 1);
            stats.setSkillPoints(stats.getSkillPoints() + 1);

            // Sync to client
            PlayerStatsSyncPacket.sendToClient(player, stats);

            currentLevel = stats.getLevel();
            xpRequired = currentLevel * 100;

            player.sendSystemMessage(Component.literal(
                    "§aYou leveled up! New level: " + currentLevel
            ));
        }
    }
}
