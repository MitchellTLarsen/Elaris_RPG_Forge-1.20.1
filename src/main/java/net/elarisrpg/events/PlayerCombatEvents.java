package net.elarisrpg.events;

import net.elarisrpg.ElarisRPG;
import net.elarisrpg.XPManager;
import net.elarisrpg.classsystem.PlayerClass;
import net.elarisrpg.classsystem.PlayerClassCapability;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.player.Player;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
import net.minecraftforge.event.entity.living.LivingHurtEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

@Mod.EventBusSubscriber(modid = ElarisRPG.MOD_ID)
public class PlayerCombatEvents {

    @SubscribeEvent
    public static void onMobKilled(LivingDeathEvent event) {
        if (event.getSource().getEntity() instanceof ServerPlayer player) {
            LivingEntity mob = event.getEntity();

            // Skip PVP
            if (mob instanceof ServerPlayer) {
                return;
            }

            XPManager.giveXp(player, 20);
        }
    }

    @SubscribeEvent
    public static void onPlayerAttack(LivingHurtEvent event) {
        if (event.getSource().getEntity() instanceof Player player) {
            player.getCapability(PlayerClassCapability.PLAYER_CLASS_CAPABILITY).ifPresent(classCap -> {
                if (classCap.getPlayerClass() == PlayerClass.WARRIOR) {
                    event.setAmount(event.getAmount() + 3.0F);
                }
            });
        }
    }
}
