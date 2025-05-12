package net.wordsaw.chivivorsmod.mixin;

import net.minecraft.enchantment.ProtectionEnchantment;
import net.minecraft.entity.damage.DamageSource;
import net.minecraft.registry.tag.DamageTypeTags;
import net.wordsaw.chivivorsmod.ChivivorsMod;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(ProtectionEnchantment.class)
public class ProtectionEnchantmentMixin {
    @Inject(method = "getProtectionAmount", at = @At("HEAD"), cancellable = true)
    private void adjustProtection(int level, DamageSource source, CallbackInfoReturnable<Integer> cir) {
        ProtectionEnchantment self = (ProtectionEnchantment)(Object)this;

        if (self.protectionType == ProtectionEnchantment.Type.ALL) {
            if (source.isIn(DamageTypeTags.IS_FIRE) ||
                    source.isIn(DamageTypeTags.IS_EXPLOSION) ||
                    source.isIn(ChivivorsMod.MAGIC_DAMAGE_TAG) ||
                    source.isIn(DamageTypeTags.IS_PROJECTILE)) {
                cir.setReturnValue(0);
            }
        }
    }
}
