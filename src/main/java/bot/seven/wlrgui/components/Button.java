package bot.seven.wlrgui.components;

import bot.seven.wlrgui.theme.GuiColors;
import static bot.seven.wlrgui.theme.GuiDimensions.MODERN_BUTTON_HEIGHT;
import static bot.seven.wlrgui.theme.GuiDimensions.MODERN_CORNER_RADIUS;
import static bot.seven.wlrgui.theme.GuiDimensions.MODERN_BORDER_THICKNESS;
import bot.seven.wlrgui.utils.GuiDrawingUtils;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;

public class Button extends GuiComponentBase {

    private final float cornerRadius = MODERN_CORNER_RADIUS;
    private final Runnable onClick;

    public Button(
            int id,
            int x,
            int y,
            int width,
            int height,
            String buttonText,
            Runnable onClick
    ) {
        super(id, x, y, width, height, buttonText);
        this.onClick = onClick;
    }

    public Button(
            int id,
            int x,
            int y,
            int width,
            String buttonText,
            Runnable onClick
    ) {
        this(id, x, y, width, MODERN_BUTTON_HEIGHT, buttonText, onClick);
    }


    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        super.drawComponent(mouseX, mouseY, partialTicks);
        if (!this.visible) return;

        int currentBgColor;
        int currentTextColor;
        int currentBorderColor;
        int glowColor = 0;

        if (!this.enabled) {
            currentBgColor = GuiColors.COMPONENT_BACKGROUND_DISABLED;
            currentTextColor = GuiColors.TEXT_DISABLED;
            currentBorderColor = GuiColors.MODERN_UI_ELEMENT_BORDER;
        } else if (this.hovered) {
            currentBgColor = GuiColors.BUTTON_MODERN_BACKGROUND_HOVER;
            currentTextColor = GuiColors.BUTTON_MODERN_TEXT;
            currentBorderColor = GuiColors.PRIMARY_RED_DARK;
            glowColor = GuiColors.PRIMARY_RED_BRIGHT_GLOW_EFFECT;
        } else {
            currentBgColor = GuiColors.BUTTON_MODERN_BACKGROUND;
            currentTextColor = GuiColors.BUTTON_MODERN_TEXT;
            currentBorderColor = GuiColors.PRIMARY_RED_DARK;
        }

        if (glowColor != 0) {
            GuiDrawingUtils.drawRoundedRect(
                    (float) this.x - 1f, (float) this.y - 1f,
                    (float) this.width + 2f, (float) this.height + 2f,
                    this.cornerRadius + 1f,
                    glowColor
            );
        }

        GuiDrawingUtils.drawModernRoundedRect(
                (float) this.x, (float) this.y,
                (float) this.width, (float) this.height,
                this.cornerRadius,
                currentBgColor,
                currentBorderColor,
                GuiColors.TRANSPARENT_RED_VERY_LIGHT_HIGHLIGHT,
                GuiColors.TRANSPARENT_BLACK_VERY_LIGHT,
                MODERN_BORDER_THICKNESS
        );

        int textY = this.y + (this.height - this.fontRenderer.FONT_HEIGHT) / 2 + 1;
        drawCenteredString(
                this.fontRenderer,
                this.label,
                this.x + this.width / 2,
                textY,
                currentTextColor
        );
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (this.enabled && this.visible && this.hovered && mouseButton == 0) {
            this.mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            this.onClick.run();
            return true;
        }
        return false;
    }

    private void drawCenteredString(FontRenderer fontRenderer, String text, int x, int y, int color) {
        fontRenderer.drawStringWithShadow(text, (float) (x - fontRenderer.getStringWidth(text) / 2), (float) y, color);
    }

}