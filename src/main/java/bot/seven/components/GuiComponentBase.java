package bot.seven.components;

import bot.seven.wlrgui.theme.GuiColors;
import bot.seven.wlrgui.theme.GuiDimensions;
import bot.seven.wlrgui.utils.GuiDrawingUtils;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.FontRenderer;
import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.renderer.GlStateManager;
import org.lwjgl.opengl.GL11;

public abstract class GuiComponentBase {
    public int id;
    public int x;
    public int y;
    public int width;
    public int height;
    public String label;
    public boolean visible = true;
    public boolean enabled = true;
    public boolean hovered = false;

    protected final Minecraft mc;
    protected final FontRenderer fontRenderer;

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
    }

    public void drawTopLabel(int yOffset) {
        if (this.label != null && !this.label.isEmpty()) {
            int labelColor = this.enabled ? GuiColors.TEXT_PRIMARY : GuiColors.TEXT_DISABLED;
            this.fontRenderer.drawStringWithShadow(
                    this.label,
                    (float) this.x,
                    (float) (this.y + yOffset),
                    labelColor
            );
        }
    }

    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        return false;
    }

    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
    }

    public void mouseReleased(int mouseX, int mouseY, int state) {
    }

    public boolean keyTyped(char typedChar, int keyCode) {
        return false;
    }

    public void handleMouseInput() {
    }

    public void updateScreen() {
    }

    public void onGuiClosed() {
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public void setVisible(boolean visible) {
        this.visible = visible;
    }

    public void setPosition(int x, int y) {
        this.x = x;
        this.y = y;
    }

    public void setSize(int width, int height) {
        this.width = width;
        this.height = height;
    }

    public void setLabel(String label) {
        this.label = label;
    }

    public boolean isMouseOver(int mouseX, int mouseY) {
        return mouseX >= this.x && mouseX <= this.x + this.width &&
                mouseY >= this.y && mouseY <= this.y + this.height;
    }

    public void updateHoverState(int mouseX, int mouseY) {
        this.hovered = this.visible && this.enabled && this.isMouseOver(mouseX, mouseY);
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
        int defaultXOffset = GuiDimensions.MODERN_ELEMENT_PADDING_X / 2;
        drawSideLabel(defaultLabelYOffset, defaultXOffset);
    }

    protected void drawTopLabel() {
        int defaultYTextOffset = -this.fontRenderer.FONT_HEIGHT - 3;
        drawTopLabel(defaultYTextOffset);
    }
}