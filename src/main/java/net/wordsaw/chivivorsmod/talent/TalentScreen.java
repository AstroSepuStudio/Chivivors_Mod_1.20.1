package net.wordsaw.chivivorsmod.talent;

import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.gui.widget.ButtonWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;
import net.wordsaw.chivivorsmod.ChivivorsMod;
import net.wordsaw.chivivorsmod.talent.talents.Talent_BasicAttackDamage;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class TalentScreen extends Screen {
    private static final Identifier BACKGROUND_TEXTURE = new Identifier("chivivorsmod", "textures/gui/talent_screen.png");

    private final UUID PLAYER_UUID;
    private TalentTreeData talentData() { return ChivivorsMod.PLAYER_TALENT_DATA.get(PLAYER_UUID); }

    private int contentOffsetX = 0;
    private int contentOffsetY = 0;

    private final Identifier lockedTexture = new Identifier("chivivorsmod", "textures/gui/locked_talent.png");
    private final Identifier unlockedTexture = new Identifier("chivivorsmod", "textures/gui/unlocked_talent.png");

    private final int buttonSize = 60;
    private final List<TalentButton> talentButtons = new ArrayList<>();
    private int guiScale;
    private boolean isDragging = false;
    private double lastMouseX, lastMouseY;

    private final List<TalentButtonData> buttonDataList = new ArrayList<>();

    // Getter
    public boolean IsDragging() { return isDragging; }

    public Talent getTalentWithID(Identifier id)
    {
        for (TalentButton btn : talentButtons){
            if (btn.getId() == id)
                return btn.getTalent();
        }
        return null;
    }

    public TalentButton getTalentButtonWithID(Identifier id)
    {
        for (TalentButton btn : talentButtons){
            if (btn.getId() == id)
                return btn;
        }
        return null;
    }

    public TalentScreen(UUID playerUuid) {
        super(Text.of("Talent Screen"));
        PLAYER_UUID = playerUuid;
    }

    public void setUp(){
        Talent_BasicAttackDamage basicAtkDmg = new Talent_BasicAttackDamage();

        TalentButtonData t0 = new TalentButtonData(basicAtkDmg, 0, 0, new Identifier("t0"), true, false);
        TalentButtonData t1 = new TalentButtonData(basicAtkDmg, -100, 0, new Identifier("t1"), false, false);
        TalentButtonData t2 = new TalentButtonData(basicAtkDmg, -100, 100, new Identifier("t2"), false, false);
        TalentButtonData t3 = new TalentButtonData(basicAtkDmg, -100, 200, new Identifier("t3"), false, false);
        TalentButtonData t4 = new TalentButtonData(basicAtkDmg, 0, 200, new Identifier("t4"), false, false);
        TalentButtonData t5 = new TalentButtonData(basicAtkDmg, -100, -100, new Identifier("t5"), false, false);
        TalentButtonData t6 = new TalentButtonData(basicAtkDmg, -100, -200, new Identifier("t6"), false, false);

        t0.adjacent.add(t1.id);
        t0.adjacent.add(t4.id);

        t1.adjacent.add(t0.id);
        t1.adjacent.add(t2.id);
        t1.adjacent.add(t5.id);

        t2.adjacent.add(t1.id);
        t2.adjacent.add(t3.id);

        t3.adjacent.add(t2.id);
        t3.adjacent.add(t4.id);

        t4.adjacent.add(t0.id);
        t4.adjacent.add(t3.id);

        t5.adjacent.add(t1.id);
        t5.adjacent.add(t6.id);

        t6.adjacent.add(t5.id);

        buttonDataList.add(t0);
        buttonDataList.add(t1);
        buttonDataList.add(t2);
        buttonDataList.add(t3);
        buttonDataList.add(t4);
        buttonDataList.add(t5);
        buttonDataList.add(t6);
    }

    @Override
    protected void init() {
        super.init();
        guiScale = (int) MinecraftClient.getInstance().getWindow().getScaleFactor();

        this.addDrawableChild(ButtonWidget.builder(Text.of("Buy Talent Point"), button ->
                buyTalentPoint()
        ).position(this.width - 150, 20).size(130, 20).build());

        this.addDrawableChild(ButtonWidget.builder(Text.of("Center"), button ->
                recenterCanvas()
        ).position(this.width - 60, this.height - 60).size(40, 40).build());

        talentButtons.clear();
        for (TalentButtonData data : buttonDataList) {
            addButtonToContent(data);
        }

        for (TalentButtonData data : buttonDataList) {
            TalentButton btn = getTalentButtonWithID(data.id);
            for (Identifier adj : data.adjacent) {
                btn.addAdjacent(adj);
            }
        }
    }

    private void addButtonToContent(TalentButtonData btnData) {
        TalentButton talentBtn = new TalentButton(talentData(), btnData, lockedTexture, unlockedTexture);

        TalentButtonWidget button = new TalentButtonWidget(
                btnData.x,
                btnData.y,
                buttonSize / guiScale,
                buttonSize / guiScale,
                lockedTexture
        );

        talentBtn.setButton(button);
        talentBtn.setUnlocked(btnData.getUnlocked());
        talentButtons.add(talentBtn);

        this.addDrawableChild(button);
    }

    @Override
    public void render(DrawContext context, int mouseX, int mouseY, float delta) {
        guiScale = (int) MinecraftClient.getInstance().getWindow().getScaleFactor();
        this.renderBackground(context);
        context.drawTexture(BACKGROUND_TEXTURE, 0, 0, 0, 0, this.width, this.height, this.width, this.height);

        context.drawTextWithShadow(this.textRenderer, Text.of("Talent Points: " + talentData().getTalentPoints()), 20, 20, 0xFFFFFF);

        for (TalentButton talentButton : talentButtons) {
            talentButton.setPosition(
                    this.width / 2 + (talentButton.getInitialX() + contentOffsetX) / guiScale - buttonSize / (2 * guiScale),
                    this.height / 2 + (talentButton.getInitialY() + contentOffsetY) / guiScale - buttonSize / (2 * guiScale));
        }

        super.render(context, mouseX, mouseY, delta);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button == 0) {
            isDragging = false;
            lastMouseX = mouseX;
            lastMouseY = mouseY;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        if (button == 0) {
            double dx = mouseX - lastMouseX;
            double dy = mouseY - lastMouseY;
            double distanceSquared = dx * dx + dy * dy;

            if (distanceSquared > 9) {
                isDragging = true;
            }

            contentOffsetX += (int) deltaX * guiScale;
            contentOffsetY += (int) deltaY * guiScale;
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (button == 0) {
            if (isDragging){
                isDragging = false;
                return super.mouseReleased(mouseX, mouseY, button);
            }

            for (TalentButton talentButton : talentButtons) {
                if (talentButton.isMouseOver(mouseX, mouseY)) {
                    talentButton.onClick(this);
                    break;
                }
            }
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    public void buyTalentPoint(){
        talentData().tryBuyTalentPoint();
    }

    public void recenterCanvas(){
        contentOffsetX = 0;
        contentOffsetY = 0;
    }
}
