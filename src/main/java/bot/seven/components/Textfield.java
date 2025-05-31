package bot.seven.components;

import bot.seven.theme.GuiColors;
import static bot.seven.theme.GuiDimensions.MODERN_TEXT_INPUT_HEIGHT;
import static bot.seven.theme.GuiDimensions.MODERN_ELEMENT_PADDING_X;
import static bot.seven.theme.GuiDimensions.MODERN_CORNER_RADIUS;

import bot.seven.utils.GuiDrawingUtils;

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

    private boolean wasFocused = false;

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

    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        if (!this.visible) return;

        this.updateHoverState(mouseX, mouseY);

        int borderColor = this.enabled ? (this.textField.isFocused() ? GuiColors.TEXTFIELD_BORDER_FOCUSED : GuiColors.TEXTFIELD_BORDER) : GuiColors.COMPONENT_BACKGROUND_DISABLED;
        int backgroundColor = this.enabled ? GuiColors.TEXTFIELD_BACKGROUND : GuiColors.COMPONENT_BACKGROUND_DISABLED;

        GuiDrawingUtils.drawRoundedRect(
                (float) this.x, (float) this.y,
                (float) this.width, (float) this.height,
                4f, backgroundColor
        );

        GuiDrawingUtils.drawRoundedRect(
                (float) this.x, (float) this.y,
                (float) this.width, (float) this.height,
                4f, borderColor
        );

        this.textField.drawTextBox();

        if (this.label != null && !this.label.isEmpty()) {
            int textColor = this.enabled ? GuiColors.TEXT_PRIMARY : GuiColors.TEXT_DISABLED;
            this.fontRenderer.drawStringWithShadow(
                    this.label,
                    (float) this.x,
                    (float) (this.y - this.fontRenderer.FONT_HEIGHT - 4),
                    textColor
            );
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!this.enabled || !this.visible) return false;

        boolean wasFocused = this.textField.isFocused();
        this.textField.mouseClicked(mouseX, mouseY, mouseButton);
        boolean isFocused = this.textField.isFocused();

        if (wasFocused != isFocused) {
            this.wasFocused = wasFocused;
        }

        if (this.enabled && this.textField.isFocused() != wasFocused && this.onFocusChanged != null) {
            this.onFocusChanged.accept(this.textField.isFocused());
        }
        return this.textField.isFocused();
    }

    @Override
    public boolean keyTyped(char typedChar, int keyCode) {
        if (!this.enabled || !this.visible) return false;

        String oldText = this.textField.getText();
        this.textField.textboxKeyTyped(typedChar, keyCode);
        String newText = this.textField.getText();

        if (!oldText.equals(newText) && this.validator.test(newText) && this.onTextChanged != null) {
            this.onTextChanged.accept(newText);
        }
        return false;
    }

    @Override
    public void updateScreen() {
        if (!this.visible) return;
        this.textField.updateCursorCounter();
    }

    public void setText(String text) {
        if (this.validator.test(text)) {
            this.textField.setText(text);
            if (this.onTextChanged != null) {
                this.onTextChanged.accept(text);
            }
        }
    }

    public String getText() {
        return this.textField.getText();
    }

    public void setEnabled(boolean enabled) {
        super.setEnabled(enabled);
        this.textField.setEnabled(enabled);
    }

    public void setVisible(boolean visible) {
        super.setVisible(visible);
        if (!visible) {
            this.textField.setFocused(false);
        }
    }

    public void setPosition(int x, int y) {
        super.setPosition(x, y);
        this.textField.xPosition = x;
        this.textField.yPosition = y;
    }

    public void setSize(int width, int height) {
        super.setSize(width, height);
        this.textField.width = width;
        this.textField.height = height;
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