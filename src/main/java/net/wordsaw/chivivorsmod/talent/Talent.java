package net.wordsaw.chivivorsmod.talent;

import net.minecraft.entity.player.PlayerEntity;

public abstract class Talent {
    public Talent() {}

    public abstract void applyEffect(PlayerEntity player);
    public abstract void removeEffect(PlayerEntity player);
}