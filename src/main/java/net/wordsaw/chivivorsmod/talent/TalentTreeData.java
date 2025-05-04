package net.wordsaw.chivivorsmod.talent;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.Identifier;
import java.util.ArrayList;
import java.util.List;

public class TalentTreeData {
    private final PlayerEntity player;
    private final TalentScreen talentScreen;
    private int talentPoints;
    private List<Identifier> unlockedTalents;

    // Getters
    public PlayerEntity getPlayer() { return player; }
    public int getTalentPoints() { return talentPoints; }
    public TalentScreen getTalentScreen() { return talentScreen; }

    public TalentTreeData(PlayerEntity player, TalentScreen talentScreen){
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
}
