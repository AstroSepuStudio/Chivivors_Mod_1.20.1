package net.wordsaw.chivivorsmod.talent.talents;

import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.wordsaw.chivivorsmod.talent.Talent;
import java.util.UUID;

public class Talent_BasicHealth extends Talent {
    @Override
    public void applyEffect(PlayerEntity player) {
        player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
                .addPersistentModifier(new EntityAttributeModifier("endurance_bonus", 1.0, EntityAttributeModifier.Operation.ADDITION));
        player.setHealth(player.getHealth() + 1.0F);
    }

    @Override
    public void removeEffect(PlayerEntity player) {
        player.getAttributeInstance(EntityAttributes.GENERIC_MAX_HEALTH)
                .removeModifier(UUID.nameUUIDFromBytes("endurance_bonus".getBytes()));
    }
}
