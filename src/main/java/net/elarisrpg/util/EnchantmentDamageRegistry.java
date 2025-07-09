package net.elarisrpg.util;

import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.Enchantments;

import java.util.HashMap;
import java.util.Map;
import java.util.function.IntToDoubleFunction;

public class EnchantmentDamageRegistry {

    private static final Map<Enchantment, IntToDoubleFunction> GENERIC_BONUSES = new HashMap<>();
    private static final Map<Enchantment, IntToDoubleFunction> CONDITIONAL_BONUSES = new HashMap<>();

    static {
        // Sharpness is generic → applies vs all mobs
        GENERIC_BONUSES.put(Enchantments.SHARPNESS, level -> 0.5 * level + 0.5);

        // Smite → only vs undead
        CONDITIONAL_BONUSES.put(Enchantments.SMITE, level -> 2.5 * level);

        // Bane → only vs arthropods
        CONDITIONAL_BONUSES.put(Enchantments.BANE_OF_ARTHROPODS, level -> 2.5 * level);

        // You can add modded enchants here too:
        // GENERIC_BONUSES.put(MyModEnchantments.VORPAL, level -> 1.0 * level);
    }

    public static double getGenericBonus(Map<Enchantment, Integer> enchants) {
        double bonus = 0.0;
        for (var entry : enchants.entrySet()) {
            var calculator = GENERIC_BONUSES.get(entry.getKey());
            if (calculator != null) {
                bonus += calculator.applyAsDouble(entry.getValue());
            }
        }
        return bonus;
    }

    public static double getConditionalBonus(Map<Enchantment, Integer> enchants) {
        double bonus = 0.0;
        for (var entry : enchants.entrySet()) {
            var calculator = CONDITIONAL_BONUSES.get(entry.getKey());
            if (calculator != null) {
                bonus += calculator.applyAsDouble(entry.getValue());
            }
        }
        return bonus;
    }
}
