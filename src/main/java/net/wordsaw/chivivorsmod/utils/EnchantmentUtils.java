package net.wordsaw.chivivorsmod.utils;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.ProtectionEnchantment;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class EnchantmentUtils {
    public static boolean isProtection(Enchantment enchantment) {
        return enchantment instanceof ProtectionEnchantment
                || enchantment.getClass().getSimpleName().equals("MagicalProtectionEnchantment");
    }

    public static int countProtectionTypes(Map<Enchantment, Integer> enchantments) {
        Set<String> types = new HashSet<>();
        for (Enchantment e : enchantments.keySet()) {
            if (e instanceof ProtectionEnchantment p) {
                types.add(p.protectionType.name());
            } else if (e.getClass().getSimpleName().equals("MagicalProtectionEnchantment")) {
                types.add("MAGICAL");
            }
        }
        return types.size();
    }
}
