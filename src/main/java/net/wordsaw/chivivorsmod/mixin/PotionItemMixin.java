package net.wordsaw.chivivorsmod.mixin;

import net.minecraft.item.Item;
import net.minecraft.item.LingeringPotionItem;
import net.minecraft.item.PotionItem;
import net.minecraft.item.SplashPotionItem;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Item.class)
public class PotionItemMixin {
    @Inject(method = "getMaxCount", at = @At("HEAD"), cancellable = true)
    private void modifyPotionMaxCount(CallbackInfoReturnable<Integer> cir) {
        Item item = (Item)(Object)this;
        // Target PotionItem and its subclasses (splash/lingering potions)
        if (item instanceof PotionItem || item instanceof SplashPotionItem || item instanceof LingeringPotionItem) {
            cir.setReturnValue(8);
        }
    }
}
