package bot.seven.wlrgui.components;

import bot.seven.wlrgui.theme.GuiColors;
import bot.seven.wlrgui.utils.GuiDrawingUtils;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.opengl.GL11;
import net.minecraft.client.audio.PositionedSoundRecord;
import net.minecraft.util.ResourceLocation;
import java.util.List;
import java.util.ArrayList;
import java.util.function.BiConsumer;
import static bot.seven.wlrgui.theme.GuiDimensions.*;

public class Dropdown extends GuiComponentBase {

    private int selectedIndex;
    public boolean isOpen = false;

    private final List<String> options;
    private final BiConsumer<Integer, String> onSelectionChanged;

    private final int optionHeight = 20;
    private final int maxDisplayableOptions = 5;
    private final int scrollbarWidth = 8;
    private final float listCornerRadius = 2f;

    private float scrollYOptions = 0f;
    private float maxScrollYOptions = 0f;
    private boolean isDraggingScrollbar = false;
    private boolean needsScrollbar = false;
    private int lastMouseYForScrollDrag = 0;

    private final float mainBoxCornerRadius = MODERN_CORNER_RADIUS;
    private final int textPaddingX;
    private final int textPaddingY;

    public Dropdown(
            int id, int x, int y, int width, int height,
            String label,
            List<String> options,
            int initialSelectedIndex,
            BiConsumer<Integer, String> onSelectionChanged
    ) {
        super(id, x, y, width, height, label);
        this.options = options == null ? new ArrayList<>() : options;
        this.onSelectionChanged = onSelectionChanged;
        this.textPaddingX = MODERN_ELEMENT_PADDING_X / 2;
        this.textPaddingY = (this.height - this.fontRenderer.FONT_HEIGHT) / 2;

        int validInitialIndex = (this.options.isEmpty()) ? -1 : Math.max(0, Math.min(initialSelectedIndex, this.options.size() - 1));
        internalSetSelected(validInitialIndex, false);
    }

    public Dropdown(
            int id, int x, int y, int width,
            String label,
            List<String> options,
            int initialSelectedIndex,
            BiConsumer<Integer, String> onSelectionChanged
    ) {
        this(id, x, y, width, MODERN_DROPDOWN_HEIGHT, label, options, initialSelectedIndex, onSelectionChanged);
    }


    private void internalSetSelected(int index, boolean notify) {
        int oldIndex = this.selectedIndex;
        if (options.isEmpty() || index < 0 || index >= options.size()) {
            this.selectedIndex = -1;
        } else {
            this.selectedIndex = index;
        }

        if (notify && oldIndex != this.selectedIndex && this.selectedIndex != -1 && this.onSelectionChanged != null) {
            this.onSelectionChanged.accept(this.selectedIndex, this.options.get(this.selectedIndex));
        }
    }

    public void setSelected(int index, boolean notify) {
        int newIndex = (options.isEmpty()) ? -1 : Math.max(0, Math.min(index, options.size() - 1));
        internalSetSelected(newIndex, notify);
    }

    public void setSelected(int index) {
        setSelected(index, true);
    }

    public String getSelectedOption() {
        if (selectedIndex >= 0 && selectedIndex < options.size()) {
            return options.get(selectedIndex);
        }
        return null;
    }

    public int getSelectedIndex() {
        return selectedIndex;
    }

    private int getListDisplayHeight() {
        return Math.min(options.size(), maxDisplayableOptions) * optionHeight;
    }

    private int getTotalOptionsContentHeight() {
        return options.size() * optionHeight;
    }

    @Override
    public void drawComponent(int mouseX, int mouseY, float partialTicks) {
        super.drawComponent(mouseX, mouseY, partialTicks);
        if (!this.visible) return;

        int mainBoxBg;
        int mainBoxBorder;
        int currentTextColor = this.enabled ? GuiColors.TEXT_PRIMARY : GuiColors.TEXT_DISABLED;
        int arrowColor = this.enabled ? GuiColors.DROPDOWN_ARROW : GuiColors.TEXT_DISABLED;

        if (!this.enabled) {
            mainBoxBg = GuiColors.COMPONENT_BACKGROUND_DISABLED;
            mainBoxBorder = GuiColors.MODERN_UI_ELEMENT_BORDER;
        } else if (this.isOpen) {
            mainBoxBg = GuiColors.COMPONENT_BACKGROUND;
            mainBoxBorder = GuiColors.PRIMARY_RED_BRIGHT;
        } else if (this.hovered) {
            mainBoxBg = GuiColors.COMPONENT_BACKGROUND_HOVER;
            mainBoxBorder = GuiColors.PRIMARY_RED;
        } else {
            mainBoxBg = GuiColors.COMPONENT_BACKGROUND;
            mainBoxBorder = GuiColors.MODERN_UI_ELEMENT_BORDER;
        }

        GuiDrawingUtils.drawRoundedRectDropShadow(
                (float)x, (float)y, (float)width, (float)height, mainBoxCornerRadius,
                GuiColors.SUBTLE_SHADOW_COLOR,
                (float)SHADOW_OFFSET_X, (float)SHADOW_OFFSET_Y
        );

        GuiDrawingUtils.drawModernRoundedRect(
                (float)x, (float)y, (float)width, (float)height, mainBoxCornerRadius,
                mainBoxBg, mainBoxBorder,
                GuiColors.TRANSPARENT_TEXT_PRIMARY_VERY_LIGHT,
                GuiColors.TRANSPARENT_BLACK_VERY_LIGHT,
                MODERN_BORDER_THICKNESS
        );

        String selectedText = getSelectedOption();
        if (selectedText == null) selectedText = "Select...";
        this.fontRenderer.drawStringWithShadow(selectedText, (float)(x + textPaddingX), (float)(y + textPaddingY), currentTextColor);
        String arrow = this.isOpen ? "▲" : "▼";
        this.fontRenderer.drawStringWithShadow(arrow, (float)(x + width - this.fontRenderer.getStringWidth(arrow) - textPaddingX), (float)(y + textPaddingY), arrowColor);

        drawTopLabel(-3);

        if (this.isOpen && this.enabled && !this.options.isEmpty()) {
            int listActualDisplayHeight = getListDisplayHeight();
            int totalContentHeight = getTotalOptionsContentHeight();
            this.needsScrollbar = totalContentHeight > listActualDisplayHeight;

            int listTopY = this.y + this.height;
            int listDrawWidth = this.width;
            float borderThickness = MODERN_BORDER_THICKNESS;

            GuiDrawingUtils.drawRoundedRectDropShadow(
                    (float)x, (float)listTopY, (float)listDrawWidth, (float)listActualDisplayHeight,
                    this.listCornerRadius, GuiColors.SUBTLE_SHADOW_COLOR,
                    (float)SHADOW_OFFSET_X, (float)SHADOW_OFFSET_Y, 1f
            );

            GuiDrawingUtils.drawModernRoundedRect(
                    (float)x, (float)listTopY, (float)listDrawWidth, (float)listActualDisplayHeight,
                    this.listCornerRadius, GuiColors.DROPDOWN_BACKGROUND_OPEN, GuiColors.MODERN_UI_ELEMENT_BORDER,
                    0, 0,
                    borderThickness
            );

            ScaledResolution sr = new ScaledResolution(mc);
            int scissorX = (int) (this.x + borderThickness);
            int scissorYDevice = sr.getScaledHeight() - (listTopY + listActualDisplayHeight - (int) borderThickness);
            int scissorWidthDevice = (int) (( (this.needsScrollbar ? listDrawWidth - this.scrollbarWidth : listDrawWidth) - 2 * borderThickness));
            int scissorHeightDevice = (int) (listActualDisplayHeight - 2 * borderThickness);


            GL11.glEnable(GL11.GL_SCISSOR_TEST);
            GL11.glScissor(
                    scissorX * sr.getScaleFactor(), scissorYDevice * sr.getScaleFactor(),
                    scissorWidthDevice * sr.getScaleFactor(), scissorHeightDevice * sr.getScaleFactor()
            );

            for (int i = 0; i < this.options.size(); i++) {
                int optionTopAbsoluteY = i * this.optionHeight;
                int optionTopOnScreenY = listTopY + optionTopAbsoluteY - (int) this.scrollYOptions;

                if (optionTopOnScreenY + this.optionHeight < listTopY || optionTopOnScreenY > listTopY + listActualDisplayHeight) continue;

                int optionTextVisualY = optionTopOnScreenY + (this.optionHeight - this.fontRenderer.FONT_HEIGHT) / 2;
                boolean isOptionHovered = mouseX >= scissorX && mouseX < scissorX + scissorWidthDevice &&
                        mouseY >= Math.max(listTopY + (int)borderThickness, optionTopOnScreenY) &&
                        mouseY < Math.min(listTopY + listActualDisplayHeight - (int)borderThickness, optionTopOnScreenY + this.optionHeight);

                int optionBgColor = 0;
                if (isOptionHovered) {
                    optionBgColor = GuiColors.DROPDOWN_ITEM_HOVER_BG;
                } else if (i == this.selectedIndex) {
                    optionBgColor = GuiColors.DROPDOWN_ITEM_SELECTED_BG;
                }

                if (optionBgColor != 0) {
                    GuiDrawingUtils.drawRoundedRect(
                            (float)(scissorX + 1f), (float)optionTopOnScreenY,
                            (float)(scissorWidthDevice - 2f), (float)this.optionHeight,
                            1f,
                            optionBgColor
                    );
                }
                this.fontRenderer.drawStringWithShadow(this.options.get(i), (float)(this.x + textPaddingX), (float)optionTextVisualY, GuiColors.DROPDOWN_ITEM_TEXT);
            }
            GL11.glDisable(GL11.GL_SCISSOR_TEST);

            if (this.needsScrollbar) {
                this.maxScrollYOptions = Math.max(0f, (float)(totalContentHeight - listActualDisplayHeight));
                this.scrollYOptions = Math.max(0f, Math.min(this.scrollYOptions, this.maxScrollYOptions));

                int scrollbarX = this.x + listDrawWidth - this.scrollbarWidth - (int)borderThickness;
                int scrollbarTrackY = listTopY + (int)borderThickness;
                int scrollbarTrackHeight = listActualDisplayHeight - 2 * (int)borderThickness;

                GuiDrawingUtils.drawRoundedRect(
                        (float)scrollbarX, (float)scrollbarTrackY, (float)this.scrollbarWidth, (float)scrollbarTrackHeight,
                        this.scrollbarWidth / 2f, GuiColors.SCROLLBAR_BG
                );

                if (this.maxScrollYOptions > 0) {
                    float thumbHeightRatio = Math.max(0.1f, Math.min(1f, (float)listActualDisplayHeight / (float)totalContentHeight));
                    float thumbHeight = Math.max(15f, scrollbarTrackHeight * thumbHeightRatio);
                    float thumbTravelDistance = scrollbarTrackHeight - thumbHeight;
                    float thumbY = scrollbarTrackY + (thumbTravelDistance * (this.scrollYOptions / this.maxScrollYOptions));
                    if (Float.isNaN(thumbY) || Float.isInfinite(thumbY)) thumbY = (float)scrollbarTrackY;

                    boolean isThumbHovered = mouseX >= scrollbarX && mouseX < scrollbarX + this.scrollbarWidth &&
                            mouseY >= thumbY && mouseY < thumbY + thumbHeight;
                    GuiDrawingUtils.drawRoundedRect(
                            (float)(scrollbarX + 1f), thumbY, (float)(this.scrollbarWidth - 2f), thumbHeight,
                            (this.scrollbarWidth - 2f) / 2f,
                            (isThumbHovered || this.isDraggingScrollbar) ? GuiColors.MODERN_SCROLLBAR_THUMB_HOVER : GuiColors.SCROLLBAR_THUMB
                    );
                }
            } else {
                this.scrollYOptions = 0f;
                this.maxScrollYOptions = 0f;
            }
        }
    }

    @Override
    public boolean mouseClicked(int mouseX, int mouseY, int mouseButton) {
        if (!this.enabled || !this.visible || mouseButton != 0) return false;

        if (mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height) {
            this.isOpen = !this.isOpen;
            if (this.isOpen && !this.options.isEmpty() && this.selectedIndex != -1) {
                int selectedItemTopY = this.selectedIndex * this.optionHeight;
                int listDisplayH = getListDisplayHeight();
                if (selectedItemTopY < this.scrollYOptions) {
                    this.scrollYOptions = (float)selectedItemTopY;
                } else if (selectedItemTopY + this.optionHeight > this.scrollYOptions + listDisplayH) {
                    this.scrollYOptions = (float)(selectedItemTopY + this.optionHeight - listDisplayH);
                }
                if (this.needsScrollbar) {
                    this.maxScrollYOptions = Math.max(0f, (float)(getTotalOptionsContentHeight() - listDisplayH));
                    this.scrollYOptions = Math.max(0f, Math.min(this.scrollYOptions, this.maxScrollYOptions));
                } else {
                    this.scrollYOptions = 0f;
                }
            } else if (!this.isOpen) {
                this.isDraggingScrollbar = false;
            }
            mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 1.0F));
            return true;
        }

        if (this.isOpen && !this.options.isEmpty()) {
            int listTopY = this.y + this.height;
            int listActualDisplayHeight = getListDisplayHeight();
            int listBottomY = listTopY + listActualDisplayHeight;
            int borderT = (int)MODERN_BORDER_THICKNESS;

            if (this.needsScrollbar) {
                int scrollbarX = this.x + this.width - this.scrollbarWidth - borderT;
                if (mouseX >= scrollbarX && mouseX < scrollbarX + this.scrollbarWidth &&
                        mouseY >= listTopY && mouseY < listBottomY) {
                    this.isDraggingScrollbar = true;
                    this.lastMouseYForScrollDrag = mouseY;
                    int scrollbarTrackY = listTopY + borderT;
                    int scrollbarTrackHeight = listActualDisplayHeight - 2 * borderT;
                    if (scrollbarTrackHeight > 0 && this.maxScrollYOptions > 0) {
                        float thumbHeightRatio = Math.max(0.1f, Math.min(1f, (float)listActualDisplayHeight / (float)getTotalOptionsContentHeight()));
                        float thumbHeight = Math.max(15f, scrollbarTrackHeight * thumbHeightRatio);
                        float relativeClickY = mouseY - scrollbarTrackY - (thumbHeight / 2f);
                        float clickRatioInTrack = Math.max(0f, Math.min(1f, relativeClickY / (scrollbarTrackHeight - thumbHeight)));
                        this.scrollYOptions = Math.max(0f, Math.min(this.maxScrollYOptions * clickRatioInTrack, this.maxScrollYOptions));
                        if (Float.isNaN(this.scrollYOptions)) this.scrollYOptions = 0;
                    }
                    return true;
                }
            }

            int itemsAreaClickableWidth = (this.needsScrollbar ? this.width - this.scrollbarWidth - borderT : this.width - 2 * borderT);
            int itemsAreaX = this.x + borderT;
            if (mouseX >= itemsAreaX && mouseX < itemsAreaX + itemsAreaClickableWidth &&
                    mouseY >= listTopY + borderT && mouseY < listBottomY - borderT) {
                int mouseYWithinListContent = mouseY - (listTopY + borderT);
                int clickedIndexAbsolute = (int)((mouseYWithinListContent + this.scrollYOptions) / this.optionHeight);

                if (clickedIndexAbsolute >= 0 && clickedIndexAbsolute < this.options.size()) {
                    internalSetSelected(clickedIndexAbsolute, true);
                    this.isOpen = false;
                    this.isDraggingScrollbar = false;
                    mc.getSoundHandler().playSound(PositionedSoundRecord.create(new ResourceLocation("gui.button.press"), 0.8F));
                    return true;
                }
            }
            if (mouseX >= this.x && mouseX < this.x + this.width && mouseY >= listTopY && mouseY < listBottomY) {
                return true;
            }
        }

        if (this.isOpen) {
            boolean clickedOutsideMainBox = !(mouseX >= this.x && mouseX < this.x + this.width && mouseY >= this.y && mouseY < this.y + this.height);
            boolean clickedOutsideListBox = true;
            if (!this.options.isEmpty()) {
                int listTopY = this.y + this.height;
                int listActualDisplayHeight = getListDisplayHeight();
                int listBottomY = listTopY + listActualDisplayHeight;
                clickedOutsideListBox = !(mouseX >= this.x && mouseX < this.x + this.width && mouseY >= listTopY && mouseY < listBottomY);
            }

            if (clickedOutsideMainBox && clickedOutsideListBox) {
                close();
            }
        }
        return false;
    }

    @Override
    public void mouseReleased(int mouseX, int mouseY, int state) {
        super.mouseReleased(mouseX, mouseY, state);
        if (state == 0) {
            this.isDraggingScrollbar = false;
        }
    }

    @Override
    public void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
        if (this.isDraggingScrollbar && clickedMouseButton == 0 && this.needsScrollbar && this.maxScrollYOptions > 0) {
            int dy = mouseY - this.lastMouseYForScrollDrag;
            this.lastMouseYForScrollDrag = mouseY;

            int listActualDisplayHeight = getListDisplayHeight();
            int scrollbarTrackHeight = listActualDisplayHeight - 2 * (int)MODERN_BORDER_THICKNESS;
            if (scrollbarTrackHeight <= 0) return;

            float thumbHeightRatio = Math.max(0.1f, Math.min(1f, (float)listActualDisplayHeight / (float)getTotalOptionsContentHeight()));
            float thumbH = Math.max(15f, scrollbarTrackHeight * thumbHeightRatio);
            float draggableTrackPixelSpace = scrollbarTrackHeight - thumbH;

            if (draggableTrackPixelSpace <= 0f) return;

            float scrollDelta = dy * (this.maxScrollYOptions / draggableTrackPixelSpace);
            this.scrollYOptions = Math.max(0f, Math.min(this.scrollYOptions + scrollDelta, this.maxScrollYOptions));
            if (Float.isNaN(this.scrollYOptions)) this.scrollYOptions = 0;
        }
    }

    public boolean handleMouseScroll(int mouseX, int mouseY, int dWheel) {
        if (!this.isOpen || !this.enabled || !this.visible || this.options.isEmpty() || !this.needsScrollbar) return false;

        int listTopY = this.y + this.height;
        int listVisibleH = getListDisplayHeight();
        int listBottomY = listTopY + listVisibleH;

        int itemsAreaX = this.x + (int)MODERN_BORDER_THICKNESS;
        int itemsAreaWidth = this.width - (this.needsScrollbar ? this.scrollbarWidth : 0) - 2 * (int)MODERN_BORDER_THICKNESS;

        if (mouseX >= itemsAreaX && mouseX < itemsAreaX + itemsAreaWidth &&
                mouseY >= listTopY && mouseY < listBottomY) {

            if (getTotalOptionsContentHeight() <= listVisibleH) return false;

            this.maxScrollYOptions = Math.max(0f, (float)(getTotalOptionsContentHeight() - listVisibleH));
            float scrollAmountPerTick = this.optionHeight * 0.5f;
            float scrollDelta = (dWheel > 0) ? -scrollAmountPerTick : scrollAmountPerTick;

            this.scrollYOptions = Math.max(0f, Math.min(this.scrollYOptions + scrollDelta, this.maxScrollYOptions));
            if (Float.isNaN(this.scrollYOptions)) this.scrollYOptions = 0;
            return true;
        }
        return false;
    }

    public void close() {
        if (this.isOpen) {
            this.isOpen = false;
            this.isDraggingScrollbar = false;
        }
    }
}