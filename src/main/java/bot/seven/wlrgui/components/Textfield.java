package bot.seven.wlrgui.components;

import bot.seven.wlrgui.theme.GuiColors;
import static bot.seven.wlrgui.theme.GuiDimensions.MODERN_TEXT_INPUT_HEIGHT;
import static bot.seven.wlrgui.theme.GuiDimensions.MODERN_ELEMENT_PADDING_X;
import static bot.seven.wlrgui.theme.GuiDimensions.MODERN_CORNER_RADIUS;
import static bot.seven.wlrgui.theme.GuiDimensions.MODERN_BORDER_THICKNESS;

import bot.seven.wlrgui.utils.GuiDrawingUtils;

import net.minecraft.client.gui.GuiTextField;

import java.util.function.Consumer;
import java.util.function.Predicate;

public class Textfield extends GuiComponentBase {

    private final int horizontalTextPadding;
    private final int verticalTextPadding;

    public final GuiTextField textField;
    private boolean hasInitialFocusCallbackFired = false;
    private final float cornerRadius;

    private final Predicate<String> validator;
    private final Consumer<String> onTextChanged;
    private final Consumer<Boolean> onFocusChanged;

    public Textfield(
            int id, int x, int y, int width, int height,
            String label,
            String initialText,
            Predicate<String> validator,
            Consumer<String> onTextChanged,
            Consumer<Boolean> onFocusChanged
    ) {
        super(id, x, y, width, height, label);

        this.validator = validator != null ? validator : s -> true;
        this.onTextChanged = onTextChanged;
        this.onFocusChanged = onFocusChanged != null ? onFocusChanged : focused -> {};

        this.horizontalTextPadding = MODERN_ELEMENT_PADDING_X / 2;
        this.verticalTextPadding = (this.height - this.fontRenderer.FONT_HEIGHT) / 2;
        this.cornerRadius = MODERN_CORNER_RADIUS;

        this.textField = new GuiTextField(
                id,
                this.fontRenderer,
                this.x + this.horizontalTextPadding,
                this.y + this.verticalTextPadding,
                this.width - (2 * this.horizontalTextPadding),
                this.fontRenderer.FONT_HEIGHT + 2
        );
        this.textField.setText(initialText);
        this.textField.setMaxStringLength(256);
        this.textField.setEnableBackgroundDrawing(false);
        this.textField.setTextColor(GuiColors.TEXTFIELD_TEXT);
        this.textField.setDisabledTextColour(GuiColors.TEXT_DISABLED);
        this.textField.setFocused(false);
    }

    public Textfield(
            int id, int x, int y, int width,
            String label,
            String initialText,
            Consumer<String> onTextChanged
    ) {
        this(id, x, y, width, MODERN_TEXT_INPUT_HEIGHT, label, initialText, s -> true, onTextChanged, focused -> {});
    }

    public Textfield(
            int id, int x, int y, int width,
            String label,
            String initialText,
            Predicate<String> validator,
            Consumer<String> onTextChanged
    ) {
        this(id, x, y, width, MODERN_TEXT_INPUT_HEIGHT, label, initialText, validator, onTextChanged, focused -> {});
    }


    public String getText() {
        return this.textField.getText();
    }

    public void setText(String newText, boolean notify) {
        String oldText = this.textField.getText();
        if (this.validator.test(newText)) {
            this.textField.setText(newText);
            if (notify && !newText.equals(oldText) && this.onTextChanged != null) {
                this.onTextChanged.accept(newText);
            }
        } else {
            this.textField.setText(oldText);
        }
    }

    public void setText(String newText) {
        setText(newText, true);
    }

    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        super.drawComponent(mouseX, mouseY, partialTicks);
        if (!this.visible) return;

        this.textField.setEnabled(this.enabled);

        this.textField.xPosition = this.x + this.horizontalTextPadding;
        this.textField.yPosition = this.y + this.verticalTextPadding;
        this.textField.width = this.width - (2 * this.horizontalTextPadding);

        int currentBgColor;
        int currentOuterBorderColor;
        int innerShadowEffect = (this.textField.isFocused() || !this.enabled) ? 0 : GuiColors.TRANSPARENT_BLACK_LIGHT;
        int innerHighlightEffect = (this.textField.isFocused() || !this.enabled) ? 0 : GuiColors.TRANSPARENT_TEXT_PRIMARY_VERY_LIGHT;

        if (!this.enabled) {
            currentBgColor = GuiColors.COMPONENT_BACKGROUND_DISABLED;
            currentOuterBorderColor = GuiColors.MODERN_UI_ELEMENT_BORDER;
        } else if (this.textField.isFocused()) {
            currentBgColor = GuiColors.TEXTFIELD_BACKGROUND;
            currentOuterBorderColor = GuiColors.TEXTFIELD_BORDER_FOCUSED;
        } else {
            currentBgColor = GuiColors.TEXTFIELD_BACKGROUND;
            currentOuterBorderColor = GuiColors.TEXTFIELD_BORDER;
        }

        if (this.textField.isFocused() && this.enabled) {
            GuiDrawingUtils.drawRoundedRect(
                    (float) this.x - 1, (float) this.y - 1,
                    (float) this.width + 2, (float) this.height + 2,
                    this.cornerRadius + 1f,
                    GuiColors.PRIMARY_RED_BRIGHT_GLOW_EFFECT
            );
        }

        GuiDrawingUtils.drawModernRoundedRect(
                (float) this.x, (float) this.y,
                (float) this.width, (float) this.height,
                this.cornerRadius,
                currentBgColor,
                currentOuterBorderColor,
                innerHighlightEffect,
                innerShadowEffect,
                MODERN_BORDER_THICKNESS
        );

        this.textField.setTextColor(this.enabled ? GuiColors.TEXTFIELD_TEXT : GuiColors.TEXT_DISABLED);
        this.textField.drawTextBox();

        drawTopLabel(-3);
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!this.visible) {
            if (this.textField.isFocused()) setFocused(false);
            return false;
        }

        boolean previousFocusState = this.textField.isFocused();
        boolean clickedThisComponent = false;

        if (mouseX >= this.x && mouseX < this.x + this.width &&
                mouseY >= this.y && mouseY < this.y + this.height) {
            clickedThisComponent = true;
            if (this.enabled) {
                this.textField.mouseClicked(mouseX, mouseY, mouseButton);
            } else {
                if (this.textField.isFocused()) setFocused(false);
            }
        } else {
            if (this.textField.isFocused()) {
                setFocused(false);
            }
        }

        if (this.enabled && this.textField.isFocused() != previousFocusState && this.onFocusChanged != null) {
            this.onFocusChanged.accept(this.textField.isFocused());
        }
        return this.enabled && clickedThisComponent && this.textField.isFocused();
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {
        if (!this.enabled || !this.visible || !this.textField.isFocused()) return false;

        String previousText = this.textField.getText();
        int previousCursorPosition = this.textField.getCursorPosition();
        int previousSelectionEnd = this.textField.getSelectionEnd();

        boolean handledByVanilla = this.textField.textboxKeyTyped(typedChar, keyCode);

        if (handledByVanilla) {
            if (!this.textField.getText().equals(previousText)) {
                if (this.validator.test(this.textField.getText())) {
                    if (this.onTextChanged != null) {
                        this.onTextChanged.accept(this.textField.getText());
                    }
                } else {
                    this.textField.setText(previousText);
                    this.textField.setCursorPosition(previousCursorPosition);
                    this.textField.setSelectionPos(previousSelectionEnd);
                }
            }
        }
        return handledByVanilla;
    }

    public void setFocused(boolean isFocused) {
        if (!this.enabled && isFocused) return;

        boolean oldFocusState = this.textField.isFocused();
        this.textField.setFocused(isFocused);

        if ((oldFocusState != isFocused || !this.hasInitialFocusCallbackFired) && this.onFocusChanged != null) {
            this.onFocusChanged.accept(isFocused);
            if (isFocused) this.hasInitialFocusCallbackFired = true;
        }
    }

    public void unfocusIfNeeded() {
        if (this.textField.isFocused()) {
            setFocused(false);
        }
    }
}