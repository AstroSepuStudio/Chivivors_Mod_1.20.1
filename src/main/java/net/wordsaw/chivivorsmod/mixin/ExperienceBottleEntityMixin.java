package net.wordsaw.chivivorsmod.mixin;

import net.minecraft.entity.EntityType;
import net.minecraft.entity.ExperienceOrbEntity;
import net.minecraft.entity.projectile.thrown.ExperienceBottleEntity;
import net.minecraft.entity.projectile.thrown.ThrownItemEntity;
import net.minecraft.util.hit.HitResult;
import net.minecraft.util.math.Vec3d;
import net.minecraft.world.World;
import net.minecraft.sound.SoundEvents;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ExperienceBottleEntity.class)
public abstract class ExperienceBottleEntityMixin extends ThrownItemEntity {

    public ExperienceBottleEntityMixin(EntityType<? extends ThrownItemEntity> entityType, World world) {
        super(entityType, world);
    }

    @Inject(method = "onCollision", at = @At("HEAD"), cancellable = true)
    private void modifyXpDrop(HitResult hitResult, CallbackInfo ci) {
        if (!this.getWorld().isClient) {
            Vec3d pos = this.getPos();

            int customXp = 10;
            this.getWorld().spawnEntity(new ExperienceOrbEntity(this.getWorld(), pos.x, pos.y, pos.z, customXp));

            this.getWorld().syncWorldEvent(2002, this.getBlockPos(), 0); // Bottle break
            this.getWorld().playSound(null, pos.x, pos.y, pos.z, SoundEvents.ENTITY_EXPERIENCE_BOTTLE_THROW, this.getSoundCategory(), 1.0F, 1.0F);

            this.discard();

            ci.cancel();
        }
    }
}