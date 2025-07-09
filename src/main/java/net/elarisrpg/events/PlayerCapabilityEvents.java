package net.elarisrpg.events;

import net.elarisrpg.ElarisRPG;
import net.elarisrpg.capability.*;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

/**
 * Handles capability attachment and cloning logic for Elaris RPG.
 *
 * This ensures our custom player stats (XP, level, skill points)
 * are properly added to every player and persist across deaths
 * and dimension changes.
 */
@Mod.EventBusSubscriber(modid = ElarisRPG.MOD_ID)
public class PlayerCapabilityEvents {

    /**
     * This is the unique key under which our capability data
     * will be stored in the player's NBT data.
     *
     * For example:
     * "elarisrpg:player_stats"
     */
    public static final ResourceLocation PLAYER_STATS_ID =
            ResourceLocation.fromNamespaceAndPath(ElarisRPG.MOD_ID, "player_stats");

    /**
     * Called by Forge whenever a new Entity is created (including players).
     *
     * This is where we attach our custom capability to the Player entity.
     *
     * Without this step, our PlayerStats capability would not exist
     * on the player at all!
     */
    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            // Create a new PlayerStats object for this player
            PlayerStats provider = new PlayerStats();

            // Attach it under our unique ResourceLocation key
            event.addCapability(PLAYER_STATS_ID, new PlayerStatsProvider(provider));
        }
    }

    /**
     * Called whenever a player is "cloned" — e.g. when they die and respawn,
     * or travel between dimensions.
     *
     * We need to manually copy the old player's data into the new player entity,
     * or the player would lose all their XP, level, etc.
     */
    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        // Only clone data on death (you could remove this check if you want to copy data for dimension changes too)
        if (!event.isWasDeath()) return;

        // Get the capability from the original (dead) player
        LazyOptional<IPlayerStats> oldCap = event.getOriginal().getCapability(ModCapabilities.PLAYER_STATS);

        // Get the capability from the new (respawned) player
        LazyOptional<IPlayerStats> newCap = event.getEntity().getCapability(ModCapabilities.PLAYER_STATS);

        // If both exist, copy values from old to new
        oldCap.ifPresent(oldStats -> {
            newCap.ifPresent(newStats -> {
                newStats.setXp(oldStats.getXp());
                newStats.setLevel(oldStats.getLevel());
                newStats.setSkillPoints(oldStats.getSkillPoints());
            });
        });
    }

    /**
     * Called during mod setup. Registers our custom capability class
     * with Forge's capability system so it knows it exists.
     *
     * Without this, Forge won't serialize/deserialize our data.
     */
    @SubscribeEvent
    public static void onRegisterCapabilities(net.minecraftforge.common.capabilities.RegisterCapabilitiesEvent event) {
        ModCapabilities.register(event);
    }

    /**
     * This event fires when a player joins the world (singleplayer or multiplayer).
     *
     * Here, we simply log their stats to the console for testing.
     * (Optional but very handy for debugging.)
     */
    @SubscribeEvent
    public static void onPlayerLoggedIn(net.minecraftforge.event.entity.player.PlayerEvent.PlayerLoggedInEvent event) {
        Player player = event.getEntity();
        player.getCapability(ModCapabilities.PLAYER_STATS).ifPresent(stats -> {
            System.out.println("Player joined with stats:");
            System.out.println("XP: " + stats.getXp());
            System.out.println("Level: " + stats.getLevel());
            System.out.println("Skill Points: " + stats.getSkillPoints());
        });
    }
}
