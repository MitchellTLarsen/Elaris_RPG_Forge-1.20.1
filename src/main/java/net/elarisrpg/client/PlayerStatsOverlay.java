package net.elarisrpg.client;

import com.mojang.blaze3d.vertex.PoseStack;
import net.elarisrpg.ElarisRPG;
import net.elarisrpg.capability.ModCapabilities;
import net.elarisrpg.classsystem.PlayerClassCapability;
import net.elarisrpg.util.EnchantmentDamageRegistry;
import net.elarisrpg.util.RayTraceUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RenderGuiEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;

@Mod.EventBusSubscriber(modid = ElarisRPG.MOD_ID, value = Dist.CLIENT)
public class PlayerStatsOverlay {

    @SubscribeEvent
    public static void onRenderOverlay(RenderGuiEvent.Post event) {
        var mc = Minecraft.getInstance();

        if (mc.player == null || mc.options.hideGui) {
            return;
        }

        Player player = mc.player;

        var result = RayTraceUtils.rayTraceEntitiesRespectingBlocks(mc.player, 16.0);

        if (result != null) {
            Entity entity = result.getEntity();
            String text = "Looking at: " + entity.getName().getString();

            GuiGraphics guiGraphics = event.getGuiGraphics();
            guiGraphics.drawString(mc.font, text, 10, 60, 0xFFFFFF, false);
        }

        // Armor
        int armorValue = player.getArmorValue();

        // Base item damage
        double itemDamage = 1.0;
        double genericBonus = 0.0;
        double conditionalBonus = 0.0;
        ItemStack heldItem = player.getMainHandItem();

        if (!heldItem.isEmpty()) {
            var attributeMap = heldItem.getAttributeModifiers(EquipmentSlot.MAINHAND);

            if (attributeMap != null) {
                var modifiers = attributeMap.get(Attributes.ATTACK_DAMAGE);
                if (modifiers != null) {
                    itemDamage += modifiers.stream()
                            .mapToDouble(modifier -> modifier.getAmount())
                            .sum();
                }
            }

            var enchants = EnchantmentHelper.getEnchantments(heldItem);
            genericBonus = EnchantmentDamageRegistry.getGenericBonus(enchants);
            conditionalBonus = EnchantmentDamageRegistry.getConditionalBonus(enchants);
        }

        // Global player bonuses
        double playerBase = player.getAttributeBaseValue(Attributes.ATTACK_DAMAGE);
        double playerTotal = player.getAttributeValue(Attributes.ATTACK_DAMAGE);
        double externalModifiers = playerTotal - playerBase;

        double baseDamage = itemDamage + externalModifiers;
        double allMobsDamage = baseDamage + genericBonus;
        double specificDamage = conditionalBonus;

        AtomicInteger level = new AtomicInteger(0);
        player.getCapability(ModCapabilities.PLAYER_STATS).ifPresent(stats -> {
            level.set(stats.getLevel());
        });

        // New: read class name
        AtomicReference<String> playerClassName = new AtomicReference<>("NONE");
        player.getCapability(PlayerClassCapability.PLAYER_CLASS_CAPABILITY).ifPresent(classCap -> {
            var playerClass = classCap.getPlayerClass();
            playerClassName.set(playerClass.name());
        });

        String baseAttackLine;

        if (specificDamage == 0) {
            baseAttackLine = String.format(
                    "Base Attack: %.1f",
                    allMobsDamage
            );
        } else {
            baseAttackLine = String.format(
                    "Base Attack: %.1f (+%.1f)",
                    allMobsDamage,
                    specificDamage
            );
        }

        String text = String.format(
                "Level: %d\nClass: %s\nArmor: %d\n%s",
                level.get(),
                playerClassName,
                armorValue,
                baseAttackLine
        );

        GuiGraphics guiGraphics = event.getGuiGraphics();
        PoseStack poseStack = guiGraphics.pose();

        poseStack.pushPose();
        String[] lines = text.split("\n");
        int y = 10;
        for (String line : lines) {
            guiGraphics.drawString(mc.font, line, 10, y, 0xFFFFFF, false);
            y += 10;
        }
        poseStack.popPose();
    }
}
