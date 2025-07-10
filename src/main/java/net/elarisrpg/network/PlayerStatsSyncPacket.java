package net.elarisrpg.network;

import net.elarisrpg.capability.ModCapabilities;
import net.elarisrpg.capability.IPlayerStats;
import net.elarisrpg.classsystem.PlayerClass;
import net.elarisrpg.classsystem.PlayerClassCapability;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.client.Minecraft;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkEvent;

import java.util.function.Supplier;

public class PlayerStatsSyncPacket {

    private final int xp;
    private final int level;
    private final int skillPoints;
    private final String playerClassName;

    public PlayerStatsSyncPacket(int xp, int level, int skillPoints, String playerClassName) {
        this.xp = xp;
        this.level = level;
        this.skillPoints = skillPoints;
        this.playerClassName = playerClassName;
    }

    public static void encode(PlayerStatsSyncPacket packet, FriendlyByteBuf buffer) {
        buffer.writeInt(packet.xp);
        buffer.writeInt(packet.level);
        buffer.writeInt(packet.skillPoints);
        buffer.writeUtf(packet.playerClassName);
    }

    public static PlayerStatsSyncPacket decode(FriendlyByteBuf buffer) {
        int xp = buffer.readInt();
        int level = buffer.readInt();
        int skillPoints = buffer.readInt();
        String playerClass = buffer.readUtf();
        return new PlayerStatsSyncPacket(xp, level, skillPoints, playerClass);
    }

    public static void handle(PlayerStatsSyncPacket packet, Supplier<NetworkEvent.Context> ctx) {
        ctx.get().enqueueWork(() -> {
            var player = Minecraft.getInstance().player;
            if (player == null) return;

            player.getCapability(ModCapabilities.PLAYER_STATS).ifPresent(stats -> {
                stats.setXp(packet.xp);
                stats.setLevel(packet.level);
                stats.setSkillPoints(packet.skillPoints);
            });
            player.getCapability(PlayerClassCapability.PLAYER_CLASS_CAPABILITY).ifPresent(classCap -> {
                classCap.setPlayerClass(PlayerClass.fromString(packet.playerClassName));
            });
        });
        ctx.get().setPacketHandled(true);
    }

    // Helper for sending from server
    public static void sendToClient(ServerPlayer player, IPlayerStats stats, PlayerClass playerClass) {
        ModNetwork.INSTANCE.sendTo(
                new PlayerStatsSyncPacket(stats.getXp(), stats.getLevel(), stats.getSkillPoints(), playerClass.name()),
                player.connection.connection,
                net.minecraftforge.network.NetworkDirection.PLAY_TO_CLIENT
        );
    }
}

