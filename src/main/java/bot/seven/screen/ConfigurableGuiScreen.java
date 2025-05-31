package bot.seven.screen;

import bot.seven.components.*;
import bot.seven.gen.GuiGen;
import bot.seven.theme.GuiColors;
import bot.seven.utils.GuiDrawingUtils;

import net.minecraft.client.gui.GuiScreen;
import net.minecraft.client.gui.ScaledResolution;
import org.lwjgl.input.Mouse;
import org.lwjgl.opengl.GL11;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class ConfigurableGuiScreen extends GuiScreen {

    private final Object settingsObject;
    private final String screenTitle;
    private final GuiGen guiGen;

    private Map<String, List<GuiComponentBase>> categorizedComponents;
    private final List<GuiComponentBase> allComponentsFlat = new ArrayList<>();

    private int panelX, panelY, panelWidth, panelHeight;
    private final int defaultPanelWidth = 300;
    private final int defaultPanelHeightMin = 200;
    private final int maxPanelHeightFractionOfScreen = 85;

    private final int padding = 10;
    private final int categoryTitleHeight;
    private final int categoryTitleSpacing = 4;
    private final int componentVSeparation = 6;
    private final int componentTopLabelOffset = -3;

    private int controlElementWidth;

    private int totalContentHeight = 0;
    private int currentScroll = 0;
    private int maxScroll = 0;
    private boolean isDraggingScrollbar = false;
    private int scrollbarX, scrollbarY, scrollbarWidth = 8, scrollbarHeightEffective;
    private float scrollbarThumbHeight, scrollbarThumbY;
    private boolean isMouseOverScrollableContent = false;

    public ConfigurableGuiScreen(Object settingsObject, String screenTitle) {
        this.settingsObject = settingsObject;
        this.screenTitle = screenTitle;
        this.guiGen = new GuiGen();
        this.fontRendererObj = mc.fontRendererObj;
        this.categoryTitleHeight = fontRendererObj.FONT_HEIGHT;
    }

    @Override
    public void initGui() {
        super.initGui();
        this.currentScroll = 0;
        this.isDraggingScrollbar = false;
        this.allComponentsFlat.clear();

        this.panelWidth = defaultPanelWidth;
        this.controlElementWidth = this.panelWidth - (padding * 2);


        this.categorizedComponents = guiGen.generateComponents(settingsObject, controlElementWidth);
        categorizedComponents.values().forEach(allComponentsFlat::addAll);

        this.totalContentHeight = calculateAndPositionComponents(0, false);

        int maxAllowedPanelHeight = (this.height * maxPanelHeightFractionOfScreen) / 100;
        this.panelHeight = Math.min(maxAllowedPanelHeight, Math.max(defaultPanelHeightMin, this.totalContentHeight + padding * 2 + fontRendererObj.FONT_HEIGHT + padding));

        this.panelX = (this.width - this.panelWidth) / 2;
        this.panelY = (this.height - this.panelHeight) / 2;

        calculateAndPositionComponents(this.currentScroll, true);

        this.scrollbarX = this.panelX + this.panelWidth - this.scrollbarWidth - 2;
        this.scrollbarHeightEffective = this.panelHeight - 4;
        this.maxScroll = Math.max(0, this.totalContentHeight - (this.panelHeight - padding * 2 - (fontRendererObj.FONT_HEIGHT + padding)));

        for (GuiComponentBase comp : allComponentsFlat) {
            if (comp instanceof Dropdown) ((Dropdown) comp).close();
            if (comp instanceof Textfield) ((Textfield) comp).unfocusIfNeeded();
        }
    }

    private int calculateAndPositionComponents(int scrollOffset, boolean applyPositionsIfTrue) {
        int currentRelativeY = padding;

        int screenTitleAreaHeight = fontRendererObj.FONT_HEIGHT + padding;

        for (Map.Entry<String, List<GuiComponentBase>> categoryEntry : categorizedComponents.entrySet()) {
            currentRelativeY += categoryTitleSpacing;
            currentRelativeY += categoryTitleHeight;
            currentRelativeY += categoryTitleSpacing;

            for (GuiComponentBase component : categoryEntry.getValue()) {
                if (applyPositionsIfTrue) {
                    component.x = this.panelX + padding;
                    component.y = this.panelY + screenTitleAreaHeight + currentRelativeY - scrollOffset;
                }
                int componentEffectiveHeight = component.height;
                if (component.label != null && !component.label.isEmpty() &&
                        !(component instanceof Button) && !(component instanceof Checkbox)) {
                    componentEffectiveHeight += fontRendererObj.FONT_HEIGHT + Math.abs(componentTopLabelOffset);
                }
                currentRelativeY += componentEffectiveHeight + componentVSeparation;
            }
        }
        if (currentRelativeY > padding) {
            currentRelativeY -= componentVSeparation;
        }
        return currentRelativeY;
    }


    @Override
    public void drawScreen(int mouseX, int mouseY, float partialTicks) {
        drawDefaultBackground();

        GuiDrawingUtils.drawModernRoundedRect(
                (float)panelX, (float)panelY, (float)panelWidth, (float)panelHeight,
                5f,
                GuiColors.MODERN_SECONDARY_BACKGROUND,
                GuiColors.MODERN_UI_ELEMENT_BORDER,
                GuiColors.TRANSPARENT_TEXT_PRIMARY_VERY_LIGHT,
                GuiColors.TRANSPARENT_BLACK_VERY_LIGHT,
                1f
        );

        int titleX = panelX + (panelWidth - fontRendererObj.getStringWidth(this.screenTitle)) / 2;
        int titleY = panelY + padding;
        fontRendererObj.drawStringWithShadow(this.screenTitle, titleX, titleY, GuiColors.TEXT_PRIMARY);
        int screenTitleAreaHeight = fontRendererObj.FONT_HEIGHT + padding;


        GL11.glEnable(GL11.GL_SCISSOR_TEST);
        ScaledResolution sr = new ScaledResolution(mc);
        int scissorX = panelX * sr.getScaleFactor();
        int scissorYDeviceSpace = (this.height - (panelY + panelHeight -1)) * sr.getScaleFactor() ;
        int scissorContentYDeviceSpace = (this.height - (panelY + screenTitleAreaHeight + (panelHeight - screenTitleAreaHeight -1))) * sr.getScaleFactor();
        int scissorWidth = panelWidth * sr.getScaleFactor();
        int scissorHeight = (panelHeight - screenTitleAreaHeight -1) * sr.getScaleFactor();

        GL11.glScissor(
                (panelX + 1) * sr.getScaleFactor(),
                (this.height - (panelY + panelHeight - 1)) * sr.getScaleFactor(),
                (panelWidth - 2 - (maxScroll > 0 ? scrollbarWidth : 0)) * sr.getScaleFactor(),
                (panelHeight - 2 - screenTitleAreaHeight) * sr.getScaleFactor()
        );
        GL11.glScissor(
                panelX * sr.getScaleFactor(),
                (this.height - (panelY + panelHeight)) * sr.getScaleFactor(),
                panelWidth * sr.getScaleFactor(),
                (panelHeight - screenTitleAreaHeight) * sr.getScaleFactor()
        );
        int contentScissorY = panelY + screenTitleAreaHeight;
        int contentScissorHeight = panelHeight - screenTitleAreaHeight -1;
        GL11.glScissor(
                (panelX +1) * sr.getScaleFactor(),
                (this.height - (contentScissorY + contentScissorHeight)) * sr.getScaleFactor(),
                (panelWidth -2 - (maxScroll > 0 ? scrollbarWidth : 0) ) * sr.getScaleFactor(),
                contentScissorHeight * sr.getScaleFactor()
        );


        int currentRelativeY = padding;

        for (Map.Entry<String, List<GuiComponentBase>> categoryEntry : categorizedComponents.entrySet()) {
            String categoryName = categoryEntry.getKey();
            List<GuiComponentBase> components = categoryEntry.getValue();

            currentRelativeY += categoryTitleSpacing;
            int categoryLabelY = panelY + screenTitleAreaHeight + currentRelativeY - currentScroll;
            if (categoryLabelY + categoryTitleHeight > panelY + screenTitleAreaHeight && categoryLabelY < panelY + panelHeight) {
                fontRendererObj.drawStringWithShadow(categoryName,
                        panelX + padding,
                        categoryLabelY,
                        GuiColors.TEXT_ACCENT);
            }
            currentRelativeY += categoryTitleHeight + categoryTitleSpacing;

            for (GuiComponentBase component : components) {
                boolean isComponentVisible = component.y + component.height > (panelY + screenTitleAreaHeight) &&
                        component.y < (panelY + panelHeight);

                if (isComponentVisible) {
                    if (component.label != null && !component.label.isEmpty() &&
                            !(component instanceof Button) && !(component instanceof Checkbox)) {
                        int topLabelY = component.y + componentTopLabelOffset - fontRendererObj.FONT_HEIGHT;
                        if (topLabelY + fontRendererObj.FONT_HEIGHT > panelY + screenTitleAreaHeight && topLabelY < panelY + panelHeight) {
                            component.drawTopLabel(componentTopLabelOffset);
                        }
                    }
                    component.drawComponent(mouseX, mouseY, partialTicks);
                }
                int componentEffectiveHeight = component.height;
                if (component.label != null && !component.label.isEmpty() &&
                        !(component instanceof Button) && !(component instanceof Checkbox)) {
                    componentEffectiveHeight += fontRendererObj.FONT_HEIGHT + Math.abs(componentTopLabelOffset);
                }
                currentRelativeY += componentEffectiveHeight + componentVSeparation;
            }
        }
        GL11.glDisable(GL11.GL_SCISSOR_TEST);


        if (this.maxScroll > 0) {
            this.scrollbarY = this.panelY + 2;
            this.scrollbarHeightEffective = this.panelHeight - 4;

            float viewableContentAreaHeight = panelHeight - screenTitleAreaHeight - (padding*2) ;
            float contentRatio = viewableContentAreaHeight / (float)this.totalContentHeight;
            if (this.totalContentHeight <= viewableContentAreaHeight) contentRatio = 1f;


            this.scrollbarThumbHeight = Math.max(20f, this.scrollbarHeightEffective * contentRatio);
            if (scrollbarThumbHeight > scrollbarHeightEffective) scrollbarThumbHeight = scrollbarHeightEffective;

            float scrollTrackSpace = this.scrollbarHeightEffective - this.scrollbarThumbHeight;

            if (this.maxScroll > 0) {
                this.scrollbarThumbY = this.scrollbarY + (this.currentScroll / (float) this.maxScroll) * scrollTrackSpace;
            } else {
                this.scrollbarThumbY = this.scrollbarY;
            }

            if (Float.isNaN(this.scrollbarThumbY) || Float.isInfinite(this.scrollbarThumbY)) {
                this.scrollbarThumbY = this.scrollbarY;
            }
            this.scrollbarThumbY = Math.max(this.scrollbarY, Math.min(this.scrollbarThumbY, this.scrollbarY + this.scrollbarHeightEffective - this.scrollbarThumbHeight));


            GuiDrawingUtils.drawRoundedRect(scrollbarX, scrollbarY, scrollbarWidth, scrollbarHeightEffective, scrollbarWidth / 2f, GuiColors.SCROLLBAR_BG);
            boolean thumbHover = mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth &&
                    mouseY >= scrollbarThumbY && mouseY <= scrollbarThumbY + scrollbarThumbHeight;
            GuiDrawingUtils.drawRoundedRect(scrollbarX + 1, scrollbarThumbY, scrollbarWidth - 2, scrollbarThumbHeight, (scrollbarWidth - 2) / 2f,
                    (isDraggingScrollbar || thumbHover) ? GuiColors.MODERN_SCROLLBAR_THUMB_HOVER : GuiColors.SCROLLBAR_THUMB);
        }

        for (GuiComponentBase component : this.allComponentsFlat) {
            if (component instanceof Dropdown) {
                Dropdown dropdown = (Dropdown) component;
                if (dropdown.isOpen && dropdown.visible && dropdown.enabled) {
                    dropdown.drawComponent(mouseX, mouseY, partialTicks);
                }
            }
        }

        for (GuiComponentBase component : this.allComponentsFlat) {
            if (component.visible && component.hovered && component.label != null) {
                String tooltip = component.label;
                if (tooltip != null && !tooltip.isEmpty()) {
                    this.fontRendererObj.drawStringWithShadow(tooltip, mouseX + 12, mouseY - 12, -1);
                }
            }
        }


        isMouseOverScrollableContent = mouseX >= panelX && mouseX < panelX + panelWidth - (maxScroll > 0 ? scrollbarWidth : 0) &&
                mouseY >= panelY + screenTitleAreaHeight && mouseY < panelY + panelHeight;

        super.drawScreen(mouseX, mouseY, partialTicks);
    }

    @Override
    protected void mouseClicked(int mouseX, int mouseY, int mouseButton) throws IOException {
        boolean clickHandled = false;

        if (maxScroll > 0 && mouseButton == 0) {
            if (mouseX >= scrollbarX && mouseX <= scrollbarX + scrollbarWidth &&
                    mouseY >= scrollbarY && mouseY <= scrollbarY + scrollbarHeightEffective) {
                isDraggingScrollbar = true;
                float relativeClickY = mouseY - scrollbarY - scrollbarThumbHeight / 2f;
                float trackSpace = scrollbarHeightEffective - scrollbarThumbHeight;
                if (trackSpace > 0) {
                    currentScroll = (int) (Math.max(0, Math.min(1, relativeClickY / trackSpace)) * maxScroll);
                    updateScrollAndComponentPositions();
                }
                clickHandled = true;
            }
        }

        List<Dropdown> openDropdowns = new ArrayList<>();
        for(GuiComponentBase c : allComponentsFlat) {
            if(c instanceof Dropdown && ((Dropdown)c).isOpen && c.visible && c.enabled) {
                openDropdowns.add((Dropdown)c);
            }
        }

        for (Dropdown dropdown : openDropdowns) {
            if (dropdown.mouseClicked(mouseX, mouseY, mouseButton)) {
                for(Dropdown otherDd : openDropdowns) {
                    if (otherDd != dropdown && !dropdown.isOpen) {
                        otherDd.close();
                    } else if (otherDd != dropdown && dropdown.isOpen) {
                        otherDd.close();
                    }
                }
                if (!dropdown.isOpen) {
                    for(GuiComponentBase otherDd : allComponentsFlat) {
                        if(otherDd instanceof Dropdown) ((Dropdown)otherDd).close();
                    }
                }
                return;
            }
        }


        if (!clickHandled && !isDraggingScrollbar &&
                mouseX >= panelX && mouseX <= panelX + panelWidth &&
                mouseY >= panelY && mouseY <= panelY + panelHeight) {

            GuiComponentBase focusedTextfield = null;
            for (GuiComponentBase component : this.allComponentsFlat) {
                if (component instanceof Textfield && ((Textfield) component).textField.isFocused()) {
                    focusedTextfield = component;
                    break;
                }
            }

            if (focusedTextfield != null && focusedTextfield.hovered) {
                if (isComponentClickable(focusedTextfield)) {
                    if (focusedTextfield.mouseClicked(mouseX, mouseY, mouseButton)) {
                        clickHandled = true;
                    }
                }
            }

            if(!clickHandled) {
                for (GuiComponentBase component : this.allComponentsFlat) {
                    if (component == focusedTextfield && component.hovered) continue;

                    if (isComponentClickable(component) && component.hovered) {
                        if (component.mouseClicked(mouseX, mouseY, mouseButton)) {
                            clickHandled = true;
                            if (component instanceof Dropdown && ((Dropdown)component).isOpen) {
                                for(GuiComponentBase otherComp : allComponentsFlat) {
                                    if (otherComp instanceof Dropdown && otherComp != component) {
                                        ((Dropdown)otherComp).close();
                                    }
                                }
                            }
                            break;
                        }
                    }
                }
            }
        }

        if (!clickHandled && !isDraggingScrollbar) {
            for (GuiComponentBase component : this.allComponentsFlat) {
                if (component instanceof Textfield) {
                    Textfield tf = (Textfield) component;
                    boolean clickedThisTf = mouseX >= tf.x && mouseX <= tf.x + tf.width &&
                            mouseY >= tf.y && mouseY <= tf.y + tf.height;
                    if (tf.textField.isFocused() && !clickedThisTf) {
                        tf.setFocused(false);
                    }
                }
                if (component instanceof Dropdown) {
                    Dropdown dd = (Dropdown) component;
                    if (dd.isOpen) {
                        boolean clickInHeader = mouseX >= dd.x && mouseX < dd.x + dd.width && mouseY >= dd.y && mouseY < dd.y + dd.height;
                        boolean clickInList = false;
                        int listTopY = dd.y + dd.height;
                        int listVisibleH = dd.getListDisplayHeight();
                        int listBottomY = listTopY + listVisibleH;
                        boolean isMouseInListArea = mouseX >= dd.x && mouseX < dd.x + dd.width &&
                                mouseY >= listTopY && mouseY < listBottomY;

                        if (!clickInHeader && !isMouseInListArea) {
                            dd.close();
                        }
                    }
                }
            }
        }
    }

    private boolean isComponentClickable(GuiComponentBase component) {
        int screenTitleAreaHeight = fontRendererObj.FONT_HEIGHT + padding;
        return component.visible && component.enabled &&
                component.y + component.height > panelY + screenTitleAreaHeight &&
                component.y < panelY + panelHeight;
    }


    @Override
    protected void mouseReleased(int mouseX, int mouseY, int state) {
        if (state == 0 && isDraggingScrollbar) {
            isDraggingScrollbar = false;
        }
        for (GuiComponentBase component : this.allComponentsFlat) {
            if (component.visible) {
                component.mouseReleased(mouseX, mouseY, state);
            }
        }
        super.mouseReleased(mouseX, mouseY, state);
    }

    @Override
    protected void mouseClickMove(int mouseX, int mouseY, int clickedMouseButton, long timeSinceLastClick) {
        if (isDraggingScrollbar && maxScroll > 0 && clickedMouseButton == 0) {
            float relativeMouseY = mouseY - scrollbarY - scrollbarThumbHeight / 2f;
            float trackSpace = scrollbarHeightEffective - scrollbarThumbHeight;
            if (trackSpace > 0) {
                currentScroll = (int) (Math.max(0, Math.min(1, relativeMouseY / trackSpace)) * maxScroll);
                updateScrollAndComponentPositions();
            }
        } else {
            if (mouseX >= panelX && mouseX <= panelX + panelWidth &&
                    mouseY >= panelY && mouseY <= panelY + panelHeight) {
                for (GuiComponentBase component : this.allComponentsFlat) {
                    if (isComponentClickable(component)) {
                        component.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
                    }
                }
            }
        }
        super.mouseClickMove(mouseX, mouseY, clickedMouseButton, timeSinceLastClick);
    }

    @Override
    public void handleMouseInput() throws IOException {
        super.handleMouseInput();
        int dWheel = Mouse.getEventDWheel();

        ScaledResolution sr = new ScaledResolution(mc);
        int currentMouseX = Mouse.getX() * sr.getScaledWidth() / this.mc.displayWidth;
        int currentMouseY = sr.getScaledHeight() - Mouse.getY() * sr.getScaledHeight() / this.mc.displayHeight - 1;


        if (dWheel != 0) {
            boolean scrollHandledByDropdown = false;
            for (GuiComponentBase comp : this.allComponentsFlat) {
                if (comp instanceof Dropdown && ((Dropdown) comp).isOpen && comp.visible && comp.enabled) {
                    if (((Dropdown) comp).handleMouseScroll(currentMouseX, currentMouseY, dWheel)) {
                        scrollHandledByDropdown = true;
                        break;
                    }
                }
            }

            isMouseOverScrollableContent = currentMouseX >= panelX &&
                    currentMouseX < panelX + panelWidth - (maxScroll > 0 ? scrollbarWidth : 0) &&
                    currentMouseY >= panelY + (fontRendererObj.FONT_HEIGHT + padding) &&
                    currentMouseY < panelY + panelHeight;

            if (!scrollHandledByDropdown && isMouseOverScrollableContent && maxScroll > 0) {
                int scrollAmountPerTick = 20;
                if (dWheel < 0) {
                    currentScroll = Math.min(maxScroll, currentScroll + scrollAmountPerTick);
                } else {
                    currentScroll = Math.max(0, currentScroll - scrollAmountPerTick);
                }
                updateScrollAndComponentPositions();
            }
        }
    }

    private void updateScrollAndComponentPositions() {
        calculateAndPositionComponents(currentScroll, true);
    }


    @Override
    protected void keyTyped(char typedChar, int keyCode) throws IOException {
        for (GuiComponentBase component : this.allComponentsFlat) {
            if (component instanceof Textfield && ((Textfield) component).textField.isFocused() &&
                    component.visible && component.enabled) {
                if (component.keyTyped(typedChar, keyCode)) return;
            }
        }

        if (keyCode == 1) {
            this.mc.displayGuiScreen(null);
            if (this.mc.currentScreen == null) {
                this.mc.setIngameFocus();
            }
        }
    }

    @Override
    public void onGuiClosed() {
        super.onGuiClosed();
        for (GuiComponentBase component : this.allComponentsFlat) {
            if (component instanceof Textfield) ((Textfield) component).unfocusIfNeeded();
            if (component instanceof Dropdown) ((Dropdown) component).close();
        }
    }

    @Override
    public boolean doesGuiPauseGame() {
        return false;
    }
}