package net.elarisrpg.network;

import net.elarisrpg.ElarisRPG;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;
import net.minecraft.resources.ResourceLocation;

public class ModNetwork {

    private static final String PROTOCOL_VERSION = "1.0";

    public static final SimpleChannel INSTANCE = NetworkRegistry.newSimpleChannel(
            ResourceLocation.fromNamespaceAndPath(ElarisRPG.MOD_ID, "main"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    private static int packetId = 0;

    public static void register() {
        INSTANCE.registerMessage(
                packetId++,
                PlayerStatsSyncPacket.class,
                PlayerStatsSyncPacket::encode,
                PlayerStatsSyncPacket::decode,
                PlayerStatsSyncPacket::handle
        );
    }
}
