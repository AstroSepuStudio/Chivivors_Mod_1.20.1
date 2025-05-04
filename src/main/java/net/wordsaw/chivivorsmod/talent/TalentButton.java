package net.wordsaw.chivivorsmod.talent;

import net.minecraft.util.Identifier;
import java.util.ArrayList;
import java.util.List;

public class TalentButton {
    private final Identifier id;
    private final Talent talent;
    private final TalentTreeData data;
    private final int initialX, initialY;
    private boolean isUnlocked;
    private TalentButtonWidget button;

    private final List<Identifier> adjacentTalents = new ArrayList<>();
    private final boolean startingTalent;

    private final TalentButtonData btnData;
    private final Identifier lockedTexture;
    private final Identifier unlockedTexture;

    // Getter
    public Identifier getId() { return id; }
    public Talent getTalent() { return talent; }
    public int getInitialY() { return initialY; }
    public int getInitialX() { return initialX; }
    public List<Identifier> getAdjacentTalents() { return adjacentTalents; }

    public TalentButton(TalentTreeData data, TalentButtonData btnData, Identifier lockedTexture, Identifier unlockedTexture) {
        this.data = data;
        this.btnData = btnData;
        this.id = btnData.id;
        this.talent = btnData.talent;
        this.initialX = btnData.x;
        this.initialY = btnData.y;
        this.startingTalent = btnData.startingTalent;
        this.lockedTexture = lockedTexture;
        this.unlockedTexture = unlockedTexture;
    }

    public void setButton(TalentButtonWidget button) { this.button = button; }

    public void setPosition(int x, int y){
        button.setPosition(x, y);
    }

    public void onClick(TalentScreen screen){
        if (screen.IsDragging())
            return;

        if (data.isUnlocked(id)){
            data.removeTalent(id);

            ArrayList<Identifier> visited = new ArrayList<>();

            for (Identifier id : adjacentTalents){
                TalentButton btn = screen.getTalentButtonWithID(id);
                if (btn.isUnlocked){
                    visited.clear();
                    visited.add(id);

                    if (!isConnectedToStartingTalent(btn, visited)){
                        removeTalentChain(btn);
                    }
                }
            }
            return;
        }

        if (!data.hasTalentPoints())
            return;

        if (startingTalent){
            data.unlockTalent(id);
            return;
        }

        for (Identifier id : adjacentTalents){
            TalentButton btn = screen.getTalentButtonWithID(id);
            if (btn.isUnlocked){
                data.unlockTalent(this.id);
                return;
            }
        }
    }

    public void setUnlocked(boolean unlock){
        isUnlocked = unlock;
        btnData.setUnlocked(unlock);

        if (unlock) {
            button.setTexture(unlockedTexture);
        } else {
            button.setTexture(lockedTexture);
        }
    }

    public void addAdjacent(Identifier id) { adjacentTalents.add(id); }

    public boolean isMouseOver(double mouseX, double mouseY) {
        return button.isMouseOver(mouseX, mouseY);
    }

    private boolean isConnectedToStartingTalent(TalentButton button, List<Identifier> visited) {
        if (button.startingTalent)
            return true;

        visited.add(button.getId());
        for (Identifier id : button.getAdjacentTalents()){
            TalentButton btn = data.getTalentScreen().getTalentButtonWithID(id);
            if (btn.isUnlocked && !visited.contains(id)){
                return (isConnectedToStartingTalent(btn, visited));
            }
        }

        return false;
    }

    private void removeTalentChain(TalentButton button){
        data.removeTalent(button.getId());

        for (Identifier id : button.getAdjacentTalents()){
            TalentButton btn = data.getTalentScreen().getTalentButtonWithID(id);
            if (btn.isUnlocked){
                btn.removeTalentChain(btn);
            }
        }
    }
}
