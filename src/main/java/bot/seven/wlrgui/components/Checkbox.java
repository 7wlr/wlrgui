package bot.seven.wlrgui.components;

import bot.seven.wlrgui.theme.GuiColors;
import static bot.seven.wlrgui.theme.GuiDimensions.MODERN_CHECKBOX_SIZE;
import static bot.seven.wlrgui.theme.GuiDimensions.MODERN_BORDER_THICKNESS;
import static bot.seven.wlrgui.theme.GuiDimensions.SHADOW_OFFSET_X;
import static bot.seven.wlrgui.theme.GuiDimensions.SHADOW_OFFSET_Y;
import bot.seven.wlrgui.utils.GuiDrawingUtils;

import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

import java.util.function.Consumer;

public class Checkbox extends GuiComponentBase {

    private final int boxSize;
    private boolean isChecked;
    private final Consumer<Boolean> onValueChanged;

    private final float visualCornerRadius = 2f;
    private final float checkmarkInsetRatio = 0.2f;

    public Checkbox(
            int id, int x, int y,
            int boxSize,
            String label,
            boolean initialValue,
            Consumer<Boolean> onValueChanged
    ) {
        super(id, x, y, boxSize, boxSize, label);
        this.boxSize = boxSize;
        this.isChecked = initialValue;
        this.onValueChanged = onValueChanged;
    }

    public Checkbox(
            int id, int x, int y,
            String label,
            boolean initialValue,
            Consumer<Boolean> onValueChanged
    ) {
        this(id, x, y, MODERN_CHECKBOX_SIZE, label, initialValue, onValueChanged);
    }

    public boolean isChecked() {
        return this.isChecked;
    }

    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        super.drawComponent(mouseX, mouseY, partialTicks);
        if (!this.visible) return;

        boolean isLabelActuallyHovered = isLabelHovered(mouseX, mouseY) && this.enabled;
        boolean visualHover = this.hovered || isLabelActuallyHovered;

        int currentBgColor;
        int currentBorderColor;
        int currentCheckColor;

        if (!this.enabled) {
            currentBgColor = GuiColors.COMPONENT_BACKGROUND_DISABLED;
            currentBorderColor = GuiColors.MODERN_UI_ELEMENT_BORDER;
            currentCheckColor = GuiColors.TEXT_DISABLED;
        } else {
            currentBgColor = visualHover ? GuiColors.CHECKBOX_BOX_HOVER : GuiColors.CHECKBOX_BOX;
            currentBorderColor = (visualHover || this.isChecked) ? GuiColors.PRIMARY_RED_BRIGHT : GuiColors.MODERN_UI_ELEMENT_BORDER;
            currentCheckColor = GuiColors.CHECKBOX_CHECK;
        }

        GuiDrawingUtils.drawRoundedRectDropShadow(
                (float) this.x, (float) this.y,
                (float) this.width, (float) this.height,
                this.visualCornerRadius,
                GuiColors.SUBTLE_SHADOW_COLOR,
                (float) SHADOW_OFFSET_X / 2f, (float) SHADOW_OFFSET_Y / 2f
        );

        GuiDrawingUtils.drawModernRoundedRect(
                (float) this.x, (float) this.y,
                (float) this.width, (float) this.height,
                this.visualCornerRadius,
                currentBgColor,
                currentBorderColor,
                (visualHover || !this.enabled) ? 0 : GuiColors.TRANSPARENT_TEXT_PRIMARY_VERY_LIGHT,
                (visualHover || !this.enabled) ? 0 : GuiColors.TRANSPARENT_BLACK_VERY_LIGHT,
                MODERN_BORDER_THICKNESS
        );

        if (this.isChecked) {
            int inset = Math.max(1, (int) (this.width * this.checkmarkInsetRatio));
            GuiDrawingUtils.drawRoundedRect(
                    (float) (this.x + inset), (float) (this.y + inset),
                    (float) (this.width - 2 * inset), (float) (this.height - 2 * inset),
                    1f,
                    currentCheckColor
            );
        }

        drawSideLabel((this.height - this.fontRenderer.FONT_HEIGHT) / 2 + 1, 8, mouseX, mouseY);
    }

    private boolean isLabelHovered(int mouseX, int mouseY) {
        if (this.label == null || this.label.isEmpty()) return false;

        int labelXOffset = 8;
        int labelActualX = this.x + this.width + labelXOffset;
        int labelActualY = this.y + (this.height - this.fontRenderer.FONT_HEIGHT) / 2 + 1;
        int labelWidth = this.fontRenderer.getStringWidth(this.label);
        int labelHeight = this.fontRenderer.FONT_HEIGHT;

        return mouseX >= labelActualX && mouseX < labelActualX + labelWidth &&
                mouseY >= labelActualY && mouseY < labelActualY + labelHeight;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!this.enabled || !this.visible || mouseButton != 0) return false;

        boolean boxClicked = this.hovered;
        boolean labelClicked = isLabelHovered(mouseX, mouseY);

        if (boxClicked || labelClicked) {
            this.isChecked = !this.isChecked;
            this.onValueChanged.accept(this.isChecked);
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 0.7F));
            return true;
        }
        return false;
    }

    protected void drawSideLabel(int labelYOffset, int xOffset, int mouseX, int mouseY) {
        if (this.label != null && !this.label.isEmpty()) {
            int labelX = this.x + this.width + xOffset;
            int labelY = this.y + labelYOffset;
            boolean isActualLabelHovered = isLabelHovered(mouseX, mouseY);
            int textColor;
            if (!this.enabled) {
                textColor = GuiColors.TEXT_DISABLED;
            } else if (isActualLabelHovered) {
                textColor = GuiColors.TEXT_HOVER;
            } else {
                textColor = GuiColors.TEXT_PRIMARY;
            }
            this.fontRenderer.drawStringWithShadow(this.label, (float)labelX, (float)labelY, textColor);
        }
    }
}