package net.wordsaw.chivivorsmod.mixin;

import net.minecraft.enchantment.Enchantment;
import net.minecraft.enchantment.EnchantmentHelper;
import net.minecraft.item.ItemStack;
import net.minecraft.screen.AnvilScreenHandler;
import net.wordsaw.chivivorsmod.utils.EnchantmentUtils;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import java.util.Map;

@Mixin(AnvilScreenHandler.class)
public class AnvilMixin {
    @Inject(method = "updateResult", at = @At("TAIL"))
    private void limitProtectionEnchantments(CallbackInfo ci) {
        AnvilScreenHandler handler = (AnvilScreenHandler)(Object)this;

        ItemStack output = handler.getSlot(2).getStack();
        if (output.isEmpty()) return;

        Map<Enchantment, Integer> enchantments = EnchantmentHelper.get(output);
        if (EnchantmentUtils.countProtectionTypes(enchantments) > 2) {
            handler.getSlot(2).setStack(ItemStack.EMPTY);
        }
    }
}
