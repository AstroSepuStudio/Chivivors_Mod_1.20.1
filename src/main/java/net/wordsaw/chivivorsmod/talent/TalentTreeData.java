package net.wordsaw.chivivorsmod.talent;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.nbt.NbtElement;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.server.network.ServerPlayerEntity;
import net.minecraft.util.Identifier;

import java.util.ArrayList;
import java.util.List;

public class TalentTreeData {
    private final ServerPlayerEntity player;
    private final TalentScreen talentScreen;
    private int talentPoints;
    private final List<Identifier> unlockedTalents;

    public PlayerEntity getPlayer() { return player; }
    public int getTalentPoints() { return talentPoints; }
    public TalentScreen getTalentScreen() { return talentScreen; }

    public TalentTreeData(ServerPlayerEntity player, TalentScreen talentScreen){
        this.player = player;
        this.talentScreen = talentScreen;
        talentPoints = 1;
        unlockedTalents = new ArrayList<>();
    }

    public void addTalentPoints(int amount) { talentPoints += amount; }
    public void removeTalentPoint(int amount) { talentPoints -= amount; }
    public boolean hasTalentPoints() { return talentPoints > 0; }

    public void unlockTalent(Identifier id) {
        unlockedTalents.add(id);
        talentScreen.getTalentButtonWithID(id).setUnlocked(true);
        talentScreen.getTalentWithID(id).applyEffect(player);
        removeTalentPoint(1);
    }

    public void removeTalent(Identifier id) {
        unlockedTalents.remove(id);
        talentScreen.getTalentButtonWithID(id).setUnlocked(false);
        talentScreen.getTalentWithID(id).removeEffect(player);
        addTalentPoints(1);
    }

    public boolean isUnlocked(Identifier id) {
        return unlockedTalents.contains(id);
    }

    public NbtCompound writeToNbt() {
        NbtCompound nbt = new NbtCompound();
        nbt.putInt("TalentPoints", talentPoints);

        NbtList unlocked = new NbtList();
        for (Identifier id : unlockedTalents) {
            unlocked.add(NbtString.of(id.toString()));
        }
        nbt.put("UnlockedTalents", unlocked);

        return nbt;
    }

    public void readFromNbt(NbtCompound nbt) {
        talentPoints = nbt.getInt("TalentPoints");

        unlockedTalents.clear();
        NbtList unlocked = nbt.getList("UnlockedTalents", NbtElement.STRING_TYPE);
        for (int i = 0; i < unlocked.size(); i++) {
            Identifier id = new Identifier(unlocked.getString(i));
            unlockedTalents.add(id);
            talentScreen.getTalentButtonWithID(id).setUnlocked(true);
            talentScreen.getTalentWithID(id).applyEffect(player);
        }
    }

    public void tryBuyTalentPoint() {
        final int cost = 5;
        if (player.experienceLevel >= cost) {
            player.addExperienceLevels(-cost);
            addTalentPoints(1);
        }
    }
}
