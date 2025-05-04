package net.wordsaw.chivivorsmod.talent;

import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.narration.NarrationMessageBuilder;
import net.minecraft.client.gui.widget.PressableWidget;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class TalentButtonWidget extends PressableWidget {
    private Identifier texture;

    public TalentButtonWidget(int x, int y, int width, int height, Identifier texture) {
        super(x, y, width, height, Text.of(""));
        this.texture = texture;
    }

    public void setTexture(Identifier texture) {
        this.texture = texture;
    }

    @Override
    public void onPress() {

    }

    @Override
    public void renderButton(DrawContext context, int mouseX, int mouseY, float delta) {
        if (this.visible && texture != null) {
            context.drawTexture(texture, this.getX(), this.getY(), 0, 0, this.getWidth(), this.getHeight(), this.getWidth(), this.getHeight());
        }
    }

    @Override
    protected void appendClickableNarrations(NarrationMessageBuilder builder) {

    }
}
