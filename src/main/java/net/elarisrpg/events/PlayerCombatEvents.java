package net.elarisrpg.events;

import net.elarisrpg.ElarisRPG;
import net.elarisrpg.XPManager;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.LivingEntity;
import net.minecraftforge.event.entity.living.LivingDeathEvent;
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
}
