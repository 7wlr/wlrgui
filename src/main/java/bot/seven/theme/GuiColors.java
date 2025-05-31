package bot.seven.theme;

import java.awt.Color;

public final class GuiColors {

    private GuiColors() {}

    public static final int PRIMARY_RED = new Color(200, 30, 30).getRGB();
    public static final int PRIMARY_RED_BRIGHT = new Color(255, 80, 80).getRGB();
    public static final int PRIMARY_RED_DARK = new Color(150, 20, 20).getRGB();
    public static final int LIGHT_RED = new Color(255, 100, 100).getRGB();

    public static final int SCREEN_BACKGROUND = new Color(15, 15, 18).getRGB();
    public static final int COMPONENT_BACKGROUND = new Color(25, 25, 28).getRGB();
    public static final int COMPONENT_BACKGROUND_HOVER = new Color(35, 35, 40).getRGB();
    public static final int COMPONENT_BACKGROUND_DISABLED = new Color(20, 20, 23).getRGB();

    public static final int COMPONENT_BORDER = new Color(40, 40, 45).getRGB();
    public static final int COMPONENT_BORDER_RED = PRIMARY_RED_DARK;
    public static final int COMPONENT_BORDER_FOCUSED_RED = PRIMARY_RED_BRIGHT;

    public static final int TEXT_PRIMARY = new Color(220, 220, 225).getRGB();
    public static final int TEXT_SECONDARY = new Color(180, 180, 180).getRGB();
    public static final int TEXT_ACCENT = PRIMARY_RED_BRIGHT;
    public static final int TEXT_DISABLED = new Color(100, 100, 105).getRGB();
    public static final int TEXT_ON_RED_BACKGROUND = new Color(240, 240, 240).getRGB();
    public static final int TEXT_HOVER = new Color(240, 240, 245).getRGB();

    public static final int TRANSPARENT_BLACK_VERY_LIGHT = new Color(0, 0, 0, 20).getRGB();
    public static final int TRANSPARENT_BLACK_LIGHT = new Color(0, 0, 0, 45).getRGB();
    public static final int TRANSPARENT_BLACK_MEDIUM = new Color(0, 0, 0, 75).getRGB();

    private static final Color _TEXT_PRIMARY_AWT = new Color(TEXT_PRIMARY, true);
    public static final int TRANSPARENT_TEXT_PRIMARY_VERY_LIGHT = new Color(_TEXT_PRIMARY_AWT.getRed(), _TEXT_PRIMARY_AWT.getGreen(), _TEXT_PRIMARY_AWT.getBlue(), 20).getRGB();
    public static final int TRANSPARENT_TEXT_PRIMARY_LIGHT = new Color(_TEXT_PRIMARY_AWT.getRed(), _TEXT_PRIMARY_AWT.getGreen(), _TEXT_PRIMARY_AWT.getBlue(), 35).getRGB();

    private static final Color _PRIMARY_RED_BRIGHT_AWT_FOR_HIGHLIGHT = new Color(PRIMARY_RED_BRIGHT, true);
    public static final int TRANSPARENT_RED_VERY_LIGHT_HIGHLIGHT = new Color(_PRIMARY_RED_BRIGHT_AWT_FOR_HIGHLIGHT.getRed(), _PRIMARY_RED_BRIGHT_AWT_FOR_HIGHLIGHT.getGreen(), _PRIMARY_RED_BRIGHT_AWT_FOR_HIGHLIGHT.getBlue(), 30).getRGB();

    public static final int TAB_BAR_BACKGROUND = new Color(20, 20, 22).getRGB();
    public static final int TAB_BUTTON_BACKGROUND_INACTIVE = TAB_BAR_BACKGROUND;
    public static final int TAB_BUTTON_BACKGROUND_HOVER = COMPONENT_BACKGROUND_HOVER;
    public static final int TAB_BUTTON_BACKGROUND_ACTIVE = PRIMARY_RED;
    public static final int TAB_BUTTON_TEXT_INACTIVE = new Color(160, 160, 165).getRGB();
    public static final int TAB_BUTTON_TEXT_HOVER = TEXT_PRIMARY;
    public static final int TAB_BUTTON_TEXT_ACTIVE = TEXT_ON_RED_BACKGROUND;
    public static final int TAB_BAR_BORDER = new Color(30, 30, 33).getRGB();
    public static final int TAB_SCROLL_BUTTON_BG = new Color(35, 35, 40).getRGB();
    public static final int TAB_SCROLL_BUTTON_HOVER_BG = new Color(45, 45, 50).getRGB();
    public static final int TAB_SCROLL_BUTTON_ARROW = TEXT_PRIMARY;

    public static final int CATEGORY_TITLE_TEXT = TEXT_ON_RED_BACKGROUND;
    private static final Color _PRIMARY_RED_DARK_AWT_FOR_CAT_BG = new Color(PRIMARY_RED_DARK, true);
    public static final int CATEGORY_TITLE_BACKGROUND = new Color(_PRIMARY_RED_DARK_AWT_FOR_CAT_BG.getRed(), _PRIMARY_RED_DARK_AWT_FOR_CAT_BG.getGreen(), _PRIMARY_RED_DARK_AWT_FOR_CAT_BG.getBlue(), 200).getRGB();
    private static final Color _PRIMARY_RED_AWT_FOR_CAT_BORDER = new Color(PRIMARY_RED, true);
    public static final int CATEGORY_TITLE_BORDER = new Color(_PRIMARY_RED_AWT_FOR_CAT_BORDER.getRed(), _PRIMARY_RED_AWT_FOR_CAT_BORDER.getGreen(), _PRIMARY_RED_AWT_FOR_CAT_BORDER.getBlue(), 180).getRGB();
    public static final int CATEGORY_SEPARATOR_LINE = new Color(_PRIMARY_RED_DARK_AWT_FOR_CAT_BG.getRed(), _PRIMARY_RED_DARK_AWT_FOR_CAT_BG.getGreen(), _PRIMARY_RED_DARK_AWT_FOR_CAT_BG.getBlue(), 150).getRGB();

    public static final int SCROLLBAR_BG = new Color(20, 20, 22).getRGB();
    public static final int SCROLLBAR_THUMB = PRIMARY_RED;
    public static final int MODERN_SCROLLBAR_THUMB_HOVER = PRIMARY_RED_BRIGHT;

    public static final int CHECKBOX_BOX = COMPONENT_BACKGROUND;
    public static final int CHECKBOX_BOX_HOVER = COMPONENT_BACKGROUND_HOVER;
    public static final int CHECKBOX_CHECK = PRIMARY_RED_BRIGHT;

    public static final int DROPDOWN_ARROW = TEXT_PRIMARY;
    public static final int DROPDOWN_BACKGROUND_OPEN = new Color(20, 20, 22, 250).getRGB();
    public static final int DROPDOWN_ITEM_TEXT = TEXT_PRIMARY;
    private static final Color _P_RED_AWT_FOR_DROPDOWN = new Color(PRIMARY_RED, true);
    public static final int DROPDOWN_ITEM_HOVER_BG = new Color(_P_RED_AWT_FOR_DROPDOWN.getRed(), _P_RED_AWT_FOR_DROPDOWN.getGreen(), _P_RED_AWT_FOR_DROPDOWN.getBlue(), 90).getRGB();
    public static final int DROPDOWN_ITEM_SELECTED_BG = new Color(_P_RED_AWT_FOR_DROPDOWN.getRed(), _P_RED_AWT_FOR_DROPDOWN.getGreen(), _P_RED_AWT_FOR_DROPDOWN.getBlue(), 130).getRGB();

    public static final int SLIDER_TRACK = new Color(35, 35, 40).getRGB();
    public static final int SLIDER_TRACK_FILLED = PRIMARY_RED;
    public static final int SLIDER_KNOB_RED_THEME = PRIMARY_RED;
    public static final int SLIDER_KNOB_HOVER_RED_THEME = PRIMARY_RED_BRIGHT;
    public static final int SLIDER_KNOB = SLIDER_KNOB_RED_THEME;
    public static final int SLIDER_KNOB_HOVER = SLIDER_KNOB_HOVER_RED_THEME;

    public static final int TEXTFIELD_BACKGROUND = new Color(20, 20, 22).getRGB();
    public static final int TEXTFIELD_BORDER = COMPONENT_BORDER;
    public static final int TEXTFIELD_BORDER_FOCUSED = PRIMARY_RED_BRIGHT;
    public static final int TEXTFIELD_TEXT = TEXT_PRIMARY;

    public static final int MODERN_PRIMARY_BACKGROUND = SCREEN_BACKGROUND;
    public static final int MODERN_SECONDARY_BACKGROUND = COMPONENT_BACKGROUND;

    public static final int MODERN_ACCENT_PRIMARY = PRIMARY_RED_BRIGHT;
    public static final int MODERN_ACCENT_SECONDARY = PRIMARY_RED;

    public static final int MODERN_UI_ELEMENT_BORDER = COMPONENT_BORDER;
    public static final int MODERN_DIVIDER_COLOR = new Color(30, 30, 33).getRGB();

    public static final int MODERN_COMPONENT_HOVER = COMPONENT_BACKGROUND_HOVER;
    public static final int MODERN_COMPONENT_ACTIVE = LIGHT_RED;
    public static final int MODERN_COMPONENT_DISABLED_BG = COMPONENT_BACKGROUND_DISABLED;

    public static final int BUTTON_MODERN_BACKGROUND = PRIMARY_RED;
    public static final int BUTTON_MODERN_BACKGROUND_HOVER = PRIMARY_RED_BRIGHT;
    public static final int BUTTON_MODERN_TEXT = TEXT_ON_RED_BACKGROUND;

    public static final int TITLE_BAR_BACKGROUND = new Color(18, 18, 20).getRGB();
    public static final int TITLE_BAR_TEXT = TEXT_PRIMARY;
    public static final int TITLE_BAR_SEPARATOR = PRIMARY_RED_DARK;

    private static final Color _P_RED_BRIGHT_AWT_FOR_GLOW = new Color(PRIMARY_RED_BRIGHT, true);
    public static final int PRIMARY_RED_BRIGHT_GLOW_EFFECT = new Color(_P_RED_BRIGHT_AWT_FOR_GLOW.getRed(), _P_RED_BRIGHT_AWT_FOR_GLOW.getGreen(), _P_RED_BRIGHT_AWT_FOR_GLOW.getBlue(), 80).getRGB();

    private static final Color _SCREEN_BG_AWT_FOR_SHADOW = new Color(SCREEN_BACKGROUND, true);
    public static final int SUBTLE_SHADOW_COLOR = new Color(
            Math.max(0, _SCREEN_BG_AWT_FOR_SHADOW.getRed() - 3),
            Math.max(0, _SCREEN_BG_AWT_FOR_SHADOW.getGreen() - 3),
            Math.max(0, _SCREEN_BG_AWT_FOR_SHADOW.getBlue() - 3),
            120
    ).getRGB();
}