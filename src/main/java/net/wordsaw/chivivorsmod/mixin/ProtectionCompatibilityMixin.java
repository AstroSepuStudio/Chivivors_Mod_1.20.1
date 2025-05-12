package net.wordsaw.chivivorsmod.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentTarget;
import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.EquipmentSlot;
import org.spongepowered.asm.mixin.Mixin;

@Mixin(ProtectionEnchantment.class)
public abstract class ProtectionCompatibilityMixin extends Enchantment {
    protected ProtectionCompatibilityMixin(Rarity weight, EnchantmentTarget target, EquipmentSlot[] slotTypes) {
        super(weight, target, slotTypes);
    }

    @Override
    public boolean canAccept(Enchantment other) {
        if (other instanceof ProtectionEnchantment) {
            return true;
        }
        return super.canAccept(other);
    }
}
