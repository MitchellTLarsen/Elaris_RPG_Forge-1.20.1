package net.elarisrpg.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.elarisrpg.capability.PlayerStatsHelper;
import net.elarisrpg.network.PlayerStatsSyncPacket;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.network.chat.Component;

public class StatsCommand {

    /**
     * Registers the /elaris_stats command.
     */
    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("elaris_stats")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();

                            PlayerStatsHelper.playerStatsHelper.get(player).ifPresent(stats -> {
                                int level = stats.getLevel();
                                int xp = stats.getXp();
                                int xpToNext = Math.max(level * 100 - xp, 0);

                                context.getSource().sendSuccess(
                                        () -> Component.literal(
                                                "Level: " + level +
                                                        " | XP: " + xp + " / " + (level * 100) +
                                                        " | XP to next level: " + xpToNext +
                                                        " | Skill Points: " + stats.getSkillPoints()
                                        ),
                                        false
                                );
                            });

                            return 1;
                        })
                        .then(Commands.literal("setxp")
                                .then(Commands.argument("value", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            ServerPlayer player = context.getSource().getPlayerOrException();
                                            int value = IntegerArgumentType.getInteger(context, "value");

                                            PlayerStatsHelper.playerStatsHelper.get(player).ifPresent(stats -> {
                                                stats.setXp(value);
                                            });

                                            context.getSource().sendSuccess(
                                                    () -> Component.literal("XP set to " + value),
                                                    false
                                            );

                                            return 1;
                                        })
                                )
                        )
                        .then(Commands.literal("reset")
                        .executes(context -> {
                            ServerPlayer player = context.getSource().getPlayerOrException();

                            PlayerStatsHelper.playerStatsHelper.get(player).ifPresent(stats -> {
                                stats.setXp(0);
                                stats.setLevel(1);
                                stats.setSkillPoints(0);

                                // Sync to client
                                PlayerStatsSyncPacket.sendToClient(player, stats);
                            });

                            context.getSource().sendSuccess(
                                    () -> Component.literal("Player stats have been reset to default."),
                                    false
                            );

                            return 1;
                        })
                ));
    }
}
