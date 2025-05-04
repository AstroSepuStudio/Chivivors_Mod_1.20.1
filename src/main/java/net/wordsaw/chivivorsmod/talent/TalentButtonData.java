package net.wordsaw.chivivorsmod.talent;

import net.minecraft.util.Identifier;
import java.util.ArrayList;
import java.util.List;

public class TalentButtonData {
    public final Talent talent;
    public final int x, y;
    public final Identifier id;
    public final boolean startingTalent;
    public final List<Identifier> adjacent = new ArrayList<>();
    private boolean unlocked;

    public TalentButtonData(Talent talent, int x, int y, Identifier id, boolean startingTalent, boolean unlocked) {
        this.talent = talent;
        this.x = x;
        this.y = y;
        this.id = id;
        this.startingTalent = startingTalent;
        this.unlocked = unlocked;
    }

    public void setUnlocked(boolean unlocked){
        this.unlocked = unlocked;
    }

    public boolean getUnlocked(){
        return unlocked;
    }
}
