package net.elarisrpg.classsystem;

import net.minecraft.resources.ResourceLocation;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.Entity;
import net.minecraftforge.common.capabilities.*;
import net.minecraftforge.event.AttachCapabilitiesEvent;
import net.minecraftforge.event.entity.player.PlayerEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.common.util.LazyOptional;
import net.elarisrpg.ElarisRPG;
import org.jetbrains.annotations.NotNull;

public class PlayerClassCapability {

    public static final Capability<IPlayerClass> PLAYER_CLASS_CAPABILITY =
            CapabilityManager.get(new CapabilityToken<>() {});

    public static ResourceLocation ID = ResourceLocation.fromNamespaceAndPath(ElarisRPG.MOD_ID, "player_class");

    @SubscribeEvent
    public static void onAttachCapabilities(AttachCapabilitiesEvent<Entity> event) {
        if (event.getObject() instanceof Player) {
            PlayerClassProvider backend = new PlayerClassProvider();

            ICapabilityProvider provider = new ICapabilityProvider() {
                private final LazyOptional<IPlayerClass> optional = LazyOptional.of(() -> backend);

                @Override
                public <T> @NotNull LazyOptional<T> getCapability(@NotNull Capability<T> cap, Direction side) {
                    return cap == PLAYER_CLASS_CAPABILITY ? optional.cast() : LazyOptional.empty();
                }
            };

            event.addCapability(ID, provider);
        }
    }

    @SubscribeEvent
    public static void onPlayerClone(PlayerEvent.Clone event) {
        event.getOriginal().getCapability(PLAYER_CLASS_CAPABILITY).ifPresent(oldStore -> {
            event.getEntity().getCapability(PLAYER_CLASS_CAPABILITY).ifPresent(newStore -> {
                newStore.setPlayerClass(oldStore.getPlayerClass());
            });
        });
    }
}
