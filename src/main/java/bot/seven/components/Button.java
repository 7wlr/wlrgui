package bot.seven.wlrgui.components;

import bot.seven.wlrgui.theme.GuiColors;
import bot.seven.wlrgui.utils.GuiDrawingUtils;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import static bot.seven.wlrgui.theme.GuiDimensions.MODERN_BUTTON_HEIGHT;

import java.util.function.Consumer;

public class Button extends GuiComponentBase {
    private final Consumer<Button> onClick;
    private boolean isPressed = false;

    public Button(int id, int x, int y, int width, String label, Consumer<Button> onClick) {
        super(id, x, y, width, MODERN_BUTTON_HEIGHT, label);
        this.onClick = onClick;
    }

    public Button(int id, int x, int y, int width, String label, Runnable onClick) {
        this(id, x, y, width, label, button -> onClick.run());
    }

    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) return;

        this.updateHoverState(mouseX, mouseY);

        int backgroundColor;
        if (!this.enabled) {
            backgroundColor = GuiColors.COMPONENT_BACKGROUND_DISABLED;
        } else if (this.isPressed) {
            backgroundColor = GuiColors.BUTTON_MODERN_BACKGROUND_HOVER;
        } else if (this.hovered) {
            backgroundColor = GuiColors.BUTTON_MODERN_BACKGROUND_HOVER;
        } else {
            backgroundColor = GuiColors.BUTTON_MODERN_BACKGROUND;
        }

        GuiDrawingUtils.drawRoundedRect(
                (float) this.x, (float) this.y, (float) this.width, (float) this.height,
                4f, backgroundColor
        );

        int textColor = this.enabled ? GuiColors.BUTTON_MODERN_TEXT : GuiColors.TEXT_DISABLED;
        int textWidth = this.fontRenderer.getStringWidth(this.label);
        int textX = this.x + (this.width - textWidth) / 2;
        int textY = this.y + (this.height - this.fontRenderer.FONT_HEIGHT) / 2 + 1;

        this.fontRenderer.drawStringWithShadow(
                this.label,
                (float) textX,
                (float) textY,
                textColor
        );
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!this.enabled || !this.visible || mouseButton != 0) return false;

        if (this.isMouseOver(mouseX, mouseY)) {
            this.isPressed = true;
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 0.7F));
            return true;
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0 && this.isPressed) {
            this.isPressed = false;
            if (this.isMouseOver(mouseX, mouseY) && this.onClick != null) {
                this.onClick.accept(this);
            }
        }
    }
}