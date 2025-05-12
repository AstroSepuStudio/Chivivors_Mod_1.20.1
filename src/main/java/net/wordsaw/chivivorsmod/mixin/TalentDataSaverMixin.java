package net.wordsaw.chivivorsmod.mixin;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.network.ServerPlayerEntity;
import net.wordsaw.chivivorsmod.ChivivorsMod;
import net.wordsaw.chivivorsmod.talent.TalentScreen;
import net.wordsaw.chivivorsmod.talent.TalentTreeData;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ServerPlayerEntity.class)
public class TalentDataSaverMixin {
    @Unique
    private static final String CHIVIVORS_TALENT_NBT_KEY = "chivivorsmod.TalentTree";

    @Unique
    private TalentTreeData talentTreeData;

    @Inject(method = "readCustomDataFromNbt", at = @At("RETURN"))
    private void onRead(NbtCompound nbt, CallbackInfo ci) {
        ServerPlayerEntity player = (ServerPlayerEntity)(Object)this;
        TalentScreen screen = new TalentScreen(player.getUuid());
        this.talentTreeData = new TalentTreeData(player, screen);
        if (nbt.contains(CHIVIVORS_TALENT_NBT_KEY)) {
            this.talentTreeData.readFromNbt(nbt.getCompound(CHIVIVORS_TALENT_NBT_KEY));
            ChivivorsMod.PLAYER_TALENT_DATA.put(player.getUuid(), this.talentTreeData);
        }
    }

    @Inject(method = "writeCustomDataToNbt", at = @At("RETURN"))
    private void onWrite(NbtCompound nbt, CallbackInfo ci) {
        if (talentTreeData != null) {
            nbt.put(CHIVIVORS_TALENT_NBT_KEY, talentTreeData.writeToNbt());
        }
    }

    public TalentTreeData getTalentTreeData() {
        return talentTreeData;
    }
}
