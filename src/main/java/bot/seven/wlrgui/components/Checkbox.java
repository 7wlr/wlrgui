package bot.seven.wlrgui.components;

import bot.seven.wlrgui.theme.GuiColors;
import bot.seven.wlrgui.utils.GuiDrawingUtils;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import static bot.seven.wlrgui.theme.GuiDimensions.MODERN_CHECKBOX_SIZE;

import java.util.function.Consumer;

public class Checkbox extends GuiComponentBase {
    private boolean checked;
    private final Consumer<Boolean> onValueChanged;

    public Checkbox(int id, int x, int y, String label, boolean initialValue, Consumer<Boolean> onValueChanged) {
        super(id, x, y, MODERN_CHECKBOX_SIZE, MODERN_CHECKBOX_SIZE, label);
        this.checked = initialValue;
        this.onValueChanged = onValueChanged;
    }

    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) return;

        this.updateHoverState(mouseX, mouseY);

        int boxColor = this.enabled ? (this.hovered ? GuiColors.CHECKBOX_BOX_HOVER : GuiColors.CHECKBOX_BOX) : GuiColors.COMPONENT_BACKGROUND_DISABLED;
        GuiDrawingUtils.drawRoundedRect(
                (float) this.x, (float) this.y,
                (float) MODERN_CHECKBOX_SIZE, (float) MODERN_CHECKBOX_SIZE,
                2f, boxColor
        );

        if (this.checked) {
            int checkColor = this.enabled ? GuiColors.CHECKBOX_CHECK : GuiColors.TEXT_DISABLED;
            float checkSize = MODERN_CHECKBOX_SIZE * 0.6f;
            float checkX = this.x + (MODERN_CHECKBOX_SIZE - checkSize) / 2f;
            float checkY = this.y + (MODERN_CHECKBOX_SIZE - checkSize) / 2f;
            GuiDrawingUtils.drawRoundedRect(
                    checkX, checkY,
                    checkSize, checkSize,
                    checkSize / 2f, checkColor
            );
        }

        if (this.label != null && !this.label.isEmpty()) {
            int textColor = this.enabled ? GuiColors.TEXT_PRIMARY : GuiColors.TEXT_DISABLED;
            int textX = this.x + MODERN_CHECKBOX_SIZE + 4;
            int textY = this.y + (MODERN_CHECKBOX_SIZE - this.fontRenderer.FONT_HEIGHT) / 2 + 1;
            this.fontRenderer.drawStringWithShadow(
                    this.label,
                    (float) textX,
                    (float) textY,
                    textColor
            );
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!this.enabled || !this.visible || mouseButton != 0) return false;

        if (this.isMouseOver(mouseX, mouseY)) {
            this.checked = !this.checked;
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 0.7F));
            if (this.onValueChanged != null) {
                this.onValueChanged.accept(this.checked);
            }
            return true;
        }
        return false;
    }

    public boolean isChecked() {
        return this.checked;
    }

    public void setChecked(boolean checked) {
        if (this.checked != checked) {
            this.checked = checked;
            if (this.onValueChanged != null) {
                this.onValueChanged.accept(this.checked);
            }
        }
    }
}