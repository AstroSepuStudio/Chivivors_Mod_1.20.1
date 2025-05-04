package net.wordsaw.chivivorsmod.talent.talents;

import net.minecraft.entity.attribute.EntityAttributeInstance;
import net.minecraft.entity.attribute.EntityAttributeModifier;
import net.minecraft.entity.attribute.EntityAttributes;
import net.minecraft.entity.player.PlayerEntity;
import net.wordsaw.chivivorsmod.talent.Talent;
import java.util.UUID;

public class Talent_BasicAttackDamage extends Talent {
    private static final UUID MODIFIER_UUID = UUID.nameUUIDFromBytes("power_strike_bonus".getBytes());

    @Override
    public void applyEffect(PlayerEntity player) {
        EntityAttributeInstance attribute = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (attribute == null) return;

        EntityAttributeModifier modifier = attribute.getModifier(MODIFIER_UUID);
        if (modifier == null) return;

        double value = modifier.getValue() + 1;

        attribute.removeModifier(MODIFIER_UUID);
        attribute.addPersistentModifier(new EntityAttributeModifier(
                MODIFIER_UUID, "power_strike_bonus", value, EntityAttributeModifier.Operation.ADDITION
        ));
    }

    @Override
    public void removeEffect(PlayerEntity player) {
        EntityAttributeInstance attribute = player.getAttributeInstance(EntityAttributes.GENERIC_ATTACK_DAMAGE);
        if (attribute == null) return;

        EntityAttributeModifier modifier = attribute.getModifier(MODIFIER_UUID);
        if (modifier == null) return;

        double value = modifier.getValue() - 1;

        attribute.removeModifier(MODIFIER_UUID);
        attribute.addPersistentModifier(new EntityAttributeModifier(
                MODIFIER_UUID, "power_strike_bonus", value, EntityAttributeModifier.Operation.ADDITION
        ));
    }
}
