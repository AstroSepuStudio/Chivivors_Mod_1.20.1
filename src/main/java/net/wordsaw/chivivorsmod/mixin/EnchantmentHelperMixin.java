package net.wordsaw.chivivorsmod.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.enchantment.EnchantmentLevelEntry;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.random.Random;
import net.wordsaw.chivivorsmod.utils.EnchantmentUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @Inject(method = "generateEnchantments", at = @At("RETURN"), cancellable = true)
    private static void filterEnchantments(Random random, ItemStack stack, int level, boolean treasureAllowed, CallbackInfoReturnable<List<EnchantmentLevelEntry>> cir) {
        List<EnchantmentLevelEntry> list = cir.getReturnValue();
        Set<String> protectionTypes = new HashSet<>();
        List<EnchantmentLevelEntry> filtered = new ArrayList<>();

        for (EnchantmentLevelEntry entry : list) {
            Enchantment e = entry.enchantment;
            if (EnchantmentUtils.isProtection(e)) {
                String type = (e instanceof ProtectionEnchantment p) ? p.protectionType.name() : "MAGICAL";
                if (protectionTypes.size() < 2 && !protectionTypes.contains(type)) {
                    protectionTypes.add(type);
                    filtered.add(entry);
                }
            } else {
                filtered.add(entry);
            }
        }

        cir.setReturnValue(filtered);
    }
}
