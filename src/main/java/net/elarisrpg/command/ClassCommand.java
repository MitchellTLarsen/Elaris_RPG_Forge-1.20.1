package net.elarisrpg.command;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.exceptions.CommandSyntaxException;
import net.elarisrpg.classsystem.PlayerClass;
import net.elarisrpg.classsystem.PlayerClassCapability;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.commands.Commands;
import net.minecraft.network.chat.Component;
import net.minecraft.server.level.ServerPlayer;

public class ClassCommand {

    public static void register(CommandDispatcher<CommandSourceStack> dispatcher) {
        dispatcher.register(
                Commands.literal("elaris_class")
                        .then(Commands.literal("warrior")
                                .executes(ctx -> setClass(ctx.getSource(), PlayerClass.WARRIOR)))
                        .then(Commands.literal("mage")
                                .executes(ctx -> setClass(ctx.getSource(), PlayerClass.MAGE)))
                        .then(Commands.literal("rogue")
                                .executes(ctx -> setClass(ctx.getSource(), PlayerClass.ROGUE)))
        );
    }

    private static int setClass(CommandSourceStack source, PlayerClass chosenClass) throws CommandSyntaxException {
        ServerPlayer player = source.getPlayerOrException();

        player.getCapability(PlayerClassCapability.PLAYER_CLASS_CAPABILITY).ifPresent(classCap -> {
            classCap.setPlayerClass(chosenClass);
            player.sendSystemMessage(Component.literal("You are now a " + chosenClass.name() + "!"));

            // Fetch player stats to sync alongside class
            player.getCapability(net.elarisrpg.capability.ModCapabilities.PLAYER_STATS).ifPresent(stats -> {
                net.elarisrpg.network.PlayerStatsSyncPacket.sendToClient(player, stats, chosenClass);
            });
        });

        return 1;
    }

}