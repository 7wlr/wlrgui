package bot.seven.wlrgui.components;

import bot.seven.wlrgui.theme.GuiColors;
import static bot.seven.wlrgui.theme.GuiDimensions.MODERN_ELEMENT_PADDING_X;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;

public abstract class GuiComponentBase {
    public int id;
    public int x;
    public int y;
    public int width;
    public int height;
    public String label;

    protected final Minecraft mc;
    protected final FontRenderer fontRenderer;

    public boolean enabled = true;
    public boolean visible = true;
    public boolean hovered = false;

    public GuiComponentBase(
            int id,
            int x,
            int y,
            int width,
            int height,
            String label
    ) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;
        this.label = label;

        this.mc = Minecraft.getMinecraft();
        this.fontRenderer = this.mc.fontRendererObj;
    }

    public GuiComponentBase(
            int id,
            int x,
            int y,
            int width,
            int height
    ) {
        this(id, x, y, width, height, "");
    }

    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) {
            this.hovered = false;
            return;
        }
        this.hovered = this.enabled &&
                mouseX >= this.x && mouseY >= this.y &&
                mouseX < this.x + this.width && mouseY < this.y + this.height;
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!this.enabled || !this.visible) {
            return false;
        }
        return mouseX >= this.x && mouseY >= this.y &&
                mouseX < this.x + this.width && mouseY < this.y + this.height;
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
    }

    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
    }

    public boolean keyTyped(char typedChar, int keyCode) {
        return false;
    }

    protected void drawSideLabel(int labelYOffset, int xOffset) {
        if (this.label != null && !this.label.isEmpty()) {
            int labelColor = this.enabled ? GuiColors.TEXT_PRIMARY : GuiColors.TEXT_DISABLED;
            this.fontRenderer.drawStringWithShadow(this.label,
                    (float) (this.x + this.width + xOffset),
                    (float) (this.y + labelYOffset),
                    labelColor);
        }
    }

    protected void drawSideLabel() {
        int defaultLabelYOffset = (this.height - this.fontRenderer.FONT_HEIGHT) / 2 + 1;
        int defaultXOffset = MODERN_ELEMENT_PADDING_X / 2;
        drawSideLabel(defaultLabelYOffset, defaultXOffset);
    }

    protected void drawTopLabel(int yTextOffset) {
        if (this.label != null && !this.label.isEmpty()) {
            int labelColor = this.enabled ? GuiColors.TEXT_PRIMARY : GuiColors.TEXT_DISABLED;
            this.fontRenderer.drawStringWithShadow(this.label,
                    (float) this.x,
                    (float) (this.y + yTextOffset),
                    labelColor);
        }
    }

    protected void drawTopLabel() {
        int defaultYTextOffset = -this.fontRenderer.FONT_HEIGHT - 3;
        drawTopLabel(defaultYTextOffset);
    }
}