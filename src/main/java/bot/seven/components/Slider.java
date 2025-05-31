package bot.seven.components;

import bot.seven.theme.GuiColors;
import static bot.seven.theme.GuiDimensions.MODERN_SLIDER_HEIGHT;
import static bot.seven.theme.GuiDimensions.MODERN_SLIDER_KNOB_RADIUS;
import static bot.seven.theme.GuiDimensions.MODERN_SLIDER_TRACK_HEIGHT;

import bot.seven.utils.GuiDrawingUtils;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;
import net.minecraft.util.ResourceLocation;
import org.lwjgl.opengl.GL11;

import java.util.function.Consumer;
import java.util.function.Function;

public class Slider extends GuiComponentBase {

    private float currentValue;
    private boolean isDragging = false;

    private final float minValue;
    private final float maxValue;
    private final float step;
    private final Function<Float, String> displayFormat;
    private final Consumer<Float> onValueChanged;

    private final float knobVisualRadius;
    private final float trackHeightToUse;
    private final float trackCornerRadius;

    private float visualKnobCenterX;
    private float targetKnobCenterX;
    private static final float KNOB_SMOOTH_FACTOR = 0.25f;

    private static final ResourceLocation KNOB_TEXTURE = new ResourceLocation("wlrgui", "textures/gui/white_knob.png");

    public Slider(
            int id, int x, int y, int width, int height,
            String label,
            float initialValue,
            float minValue,
            float maxValue,
            float step,
            Function<Float, String> displayFormat,
            Consumer<Float> onValueChanged
    ) {
        super(id, x, y, width, height, label);
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.step = step;
        this.displayFormat = displayFormat;
        this.onValueChanged = onValueChanged;

        this.knobVisualRadius = MODERN_SLIDER_KNOB_RADIUS;
        this.trackHeightToUse = MODERN_SLIDER_TRACK_HEIGHT;
        this.trackCornerRadius = this.trackHeightToUse / 2f;

        internalSetValue(initialValue, false);
        this.targetKnobCenterX = calculateKnobCenterX(this.currentValue);
        this.visualKnobCenterX = this.targetKnobCenterX;
    }

    public Slider(
            int id, int x, int y, int width,
            String label,
            float initialValue,
            float minValue,
            float maxValue,
            Consumer<Float> onValueChanged
    ) {
        this(id, x, y, width, MODERN_SLIDER_HEIGHT, label, initialValue, minValue, maxValue, 0.1f, val -> String.format("%.2f", val), onValueChanged);
    }

    public Slider(
            int id, int x, int y, int width,
            String label,
            float initialValue,
            float minValue,
            float maxValue,
            float step,
            Consumer<Float> onValueChanged
    ) {
        this(id, x, y, width, MODERN_SLIDER_HEIGHT, label, initialValue, minValue, maxValue, step, val -> String.format("%.2f", val), onValueChanged);
    }

    public Slider(
            int id, int x, int y, int width,
            String label,
            float initialValue,
            float minValue,
            float maxValue,
            float step,
            Function<Float, String> displayFormat,
            Consumer<Float> onValueChanged
    ) {
        this(id, x, y, width, MODERN_SLIDER_HEIGHT, label, initialValue, minValue, maxValue, step, displayFormat, onValueChanged);
    }

    private float calculateKnobCenterX(float value) {
        float progress = (maxValue - minValue == 0f) ? 0f : (value - minValue) / (maxValue - minValue);
        float actualKnobRadius = (this.knobVisualRadius > 0f) ? this.knobVisualRadius : 0.1f;
        float travelWidth = this.width - (2 * actualKnobRadius);
        return this.x + actualKnobRadius + ( (travelWidth > 0) ? travelWidth * progress : 0f );
    }

    private float getKnobClickableRadius() {
        return ((this.knobVisualRadius > 0f) ? this.knobVisualRadius : 2f) + 3f;
    }

    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        super.drawComponent(mouseX, mouseY, partialTicks);
        if (!this.visible) return;

        float diff = this.targetKnobCenterX - this.visualKnobCenterX;
        this.visualKnobCenterX = (Math.abs(diff) > 0.001f) ? this.visualKnobCenterX + diff * KNOB_SMOOTH_FACTOR : this.targetKnobCenterX;

        float actualKnobRadius = (this.knobVisualRadius > 0f) ? this.knobVisualRadius : 0.1f;
        float currentKnobRenderCenterX = Math.max(this.x + actualKnobRadius, Math.min(this.visualKnobCenterX, this.x + this.width - actualKnobRadius));
        float knobRenderY = this.y + this.height / 2f;

        drawTopLabel(-3);
        String valueText = this.displayFormat.apply(this.currentValue);
        int valueColor = this.enabled ? GuiColors.TEXT_ACCENT : GuiColors.TEXT_DISABLED;
        int valueTextWidth = this.fontRenderer.getStringWidth(valueText);
        this.fontRenderer.drawStringWithShadow(
                valueText,
                (float) (this.x + this.width - valueTextWidth),
                (float) (this.y - this.fontRenderer.FONT_HEIGHT - 7),
                valueColor
        );

        float trackActualY = this.y + (this.height - this.trackHeightToUse) / 2f;

        int trackColorToUse = this.enabled ? GuiColors.SLIDER_TRACK : GuiColors.COMPONENT_BACKGROUND_DISABLED;
        GuiDrawingUtils.drawRoundedRect(
                (float) this.x, trackActualY, (float) this.width, this.trackHeightToUse,
                this.trackCornerRadius, trackColorToUse
        );

        float filledWidth = currentKnobRenderCenterX - this.x;
        if (filledWidth > 0f) {
            int filledTrackColorToUse = this.enabled ? GuiColors.SLIDER_TRACK_FILLED : GuiColors.PRIMARY_RED_DARK;
            GuiDrawingUtils.drawRoundedRect(
                    (float) this.x, trackActualY, Math.min(filledWidth, (float)this.width), this.trackHeightToUse,
                    this.trackCornerRadius, filledTrackColorToUse
            );
        }

        if (this.knobVisualRadius <= 0f) return;
        float knobDiameter = this.knobVisualRadius * 2;
        if (knobDiameter <= 0) return;

        boolean isHoveringKnob = mouseX >= currentKnobRenderCenterX - this.knobVisualRadius &&
                mouseX <= currentKnobRenderCenterX + this.knobVisualRadius &&
                mouseY >= knobRenderY - this.knobVisualRadius &&
                mouseY <= knobRenderY + this.knobVisualRadius;

        float scale = 0.85f;

        GlStateManager.pushMatrix();
        GlStateManager.enableTexture2D();
        GlStateManager.enableBlend();
        GlStateManager.tryBlendFuncSeparate(GL11.GL_SRC_ALPHA, GL11.GL_ONE_MINUS_SRC_ALPHA, GL11.GL_ONE, GL11.GL_ZERO);

        int knobColor;
        if (!this.enabled) {
            knobColor = GuiColors.TEXT_DISABLED;
        } else if (this.isDragging) {
            knobColor = GuiColors.SLIDER_KNOB_HOVER;
        } else if (isHoveringKnob || this.hovered) {
            knobColor = GuiColors.SLIDER_KNOB_HOVER;
        } else {
            knobColor = GuiColors.SLIDER_KNOB;
        }
        GuiDrawingUtils.setColor(knobColor);

        this.mc.getTextureManager().bindTexture(KNOB_TEXTURE);

        GlStateManager.translate(currentKnobRenderCenterX, knobRenderY, 0f);
        GlStateManager.scale(scale, scale, 1f);

        Gui.drawModalRectWithCustomSizedTexture(
                (int) (-knobDiameter / 2f), (int) (-knobDiameter / 2f),
                0f, 0f,
                (int) knobDiameter, (int) knobDiameter,
                knobDiameter, knobDiameter
        );

        GlStateManager.popMatrix();
        GlStateManager.resetColor();
    }

    private void internalSetValue(float newValue, boolean notify) {
        float oldValue = this.currentValue;
        float tempValue = Math.max(this.minValue, Math.min(newValue, this.maxValue));

        if (this.step > 0f) {
            float precisionFactor = 10000f;
            tempValue = Math.round(tempValue / this.step) * this.step;
            tempValue = Math.round(tempValue * precisionFactor) / precisionFactor;
        }
        this.currentValue = Math.max(this.minValue, Math.min(tempValue, this.maxValue));
        this.targetKnobCenterX = calculateKnobCenterX(this.currentValue);

        if (notify && Math.abs(oldValue - this.currentValue) > Math.min(this.step / 2.0f, 0.00001f) && this.onValueChanged != null) {
            this.onValueChanged.accept(this.currentValue);
        }
    }

    public void setValue(float newValue) {
        internalSetValue(newValue, true);
        this.visualKnobCenterX = calculateKnobCenterX(this.currentValue);
        this.targetKnobCenterX = this.visualKnobCenterX;
    }

    public float getCurrentValue() {
        return this.currentValue;
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!this.enabled || !this.visible || mouseButton != 0) return false;

        float knobRenderY = this.y + this.height / 2f;
        float dx = mouseX - this.visualKnobCenterX;
        float dy = mouseY - knobRenderY;
        float clickableRadius = getKnobClickableRadius();
        boolean clickedKnob = dx * dx + dy * dy <= clickableRadius * clickableRadius;

        float trackActualY = this.y + (this.height - this.trackHeightToUse) / 2f;
        boolean clickedTrack = mouseX >= this.x && mouseX < this.x + this.width &&
                mouseY >= trackActualY && mouseY < trackActualY + this.trackHeightToUse;

        if (clickedKnob || clickedTrack) {
            this.isDragging = true;
            updateValueFromMouse(mouseX, true);
            this.visualKnobCenterX = calculateKnobCenterX(this.currentValue);
            this.targetKnobCenterX = this.visualKnobCenterX;
            mc.getSoundHandler().playSound(net.minecraft.client.audio.PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 0.7F));
            return true;
        }
        return false;
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if (this.isDragging && clickedMouseButton == 0 && this.enabled) {
            updateValueFromMouse(mouseX, true);
        }
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (state == 0 && this.isDragging) {
            this.isDragging = false;
        }
    }

    private void updateValueFromMouse(int mouseX, boolean notify) {
        if (!this.enabled) return;

        float actualKnobRadius = (this.knobVisualRadius > 0f) ? this.knobVisualRadius : 0.1f;
        float travelWidth = this.width - (2 * actualKnobRadius);

        if (travelWidth <= 0f) {
            if (this.maxValue > this.minValue) {
                internalSetValue((mouseX < this.x + this.width / 2f) ? this.minValue : this.maxValue, notify);
            }
            return;
        }

        float relativeMouseX = mouseX - (this.x + actualKnobRadius);
        float ratio = Math.max(0f, Math.min(1f, relativeMouseX / travelWidth));
        float newValue = this.minValue + (this.maxValue - this.minValue) * ratio;
        internalSetValue(newValue, notify);
    }
}