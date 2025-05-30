package bot.seven.wlrgui.utils;

import net.minecraft.client.gui.Gui;
import net.minecraft.client.renderer.GlStateManager;

public final class GuiDrawingUtils {

    private GuiDrawingUtils() {}

    private enum CornerType { TOP_LEFT, TOP_RIGHT, BOTTOM_LEFT, BOTTOM_RIGHT }

    public static void drawRoundedRect(float x, float y, float width, float height, float radius, int color) {
        float r = Math.min(Math.min(width / 2f, height / 2f), Math.max(0f, radius));

        if (r == 0f) {
            Gui.drawRect((int) x, (int) y, (int) (x + width), (int) (y + height), color);
            return;
        }

        Gui.drawRect(
                Math.round(x + r),
                Math.round(y),
                Math.round(x + width - r),
                Math.round(y + height),
                color
        );
        Gui.drawRect(
                Math.round(x),
                Math.round(y + r),
                Math.round(x + r),
                Math.round(y + height - r),
                color
        );
        Gui.drawRect(
                Math.round(x + width - r),
                Math.round(y + r),
                Math.round(x + width),
                Math.round(y + height - r),
                color
        );

        drawFilledQuarterCircle(x, y, r, color, CornerType.TOP_LEFT);
        drawFilledQuarterCircle(x + width - r, y, r, color, CornerType.TOP_RIGHT);
        drawFilledQuarterCircle(x, y + height - r, r, color, CornerType.BOTTOM_LEFT);
        drawFilledQuarterCircle(x + width - r, y + height - r, r, color, CornerType.BOTTOM_RIGHT);
    }


    private static void drawFilledQuarterCircle(
            float cornerX,
            float cornerY,
            float radius,
            int color,
            CornerType type
    ) {
        if (radius <= 0f) return;

        float circleCenterX;
        float circleCenterY;

        switch (type) {
            case TOP_LEFT:
                circleCenterX = cornerX + radius;
                circleCenterY = cornerY + radius;
                break;
            case TOP_RIGHT:
                circleCenterX = cornerX;
                circleCenterY = cornerY + radius;
                break;
            case BOTTOM_LEFT:
                circleCenterX = cornerX + radius;
                circleCenterY = cornerY;
                break;
            case BOTTOM_RIGHT:
                circleCenterX = cornerX;
                circleCenterY = cornerY;
                break;
            default:
                return;
        }

        float rSq = radius * radius;
        int rInt = Math.round(radius);

        for (int dxBox = 0; dxBox < rInt; dxBox++) {
            for (int dyBox = 0; dyBox < rInt; dyBox++) {

                int currentPixelX = Math.round(cornerX + dxBox);
                int currentPixelY = Math.round(cornerY + dyBox);

                float distFromCircleCenterX = (currentPixelX + 0.5f) - circleCenterX;
                float distFromCircleCenterY = (currentPixelY + 0.5f) - circleCenterY;

                if (distFromCircleCenterX * distFromCircleCenterX + distFromCircleCenterY * distFromCircleCenterY <= rSq) {
                    boolean isInQuadrant = false;
                    switch (type) {
                        case TOP_LEFT:
                            if (currentPixelX < circleCenterX && currentPixelY < circleCenterY) isInQuadrant = true;
                            break;
                        case TOP_RIGHT:
                            if (currentPixelX >= circleCenterX && currentPixelY < circleCenterY) isInQuadrant = true;
                            break;
                        case BOTTOM_LEFT:
                            if (currentPixelX < circleCenterX && currentPixelY >= circleCenterY) isInQuadrant = true;
                            break;
                        case BOTTOM_RIGHT:
                            if (currentPixelX >= circleCenterX && currentPixelY >= circleCenterY) isInQuadrant = true;
                            break;
                    }

                    if (isInQuadrant) {
                        Gui.drawRect(currentPixelX, currentPixelY, currentPixelX + 1, currentPixelY + 1, color);
                    }
                }
            }
        }
    }

    public static void drawRoundedRectWithBorder(
            float x, float y, float width, float height,
            float radius, int bgColor, int borderColor, float borderThickness
    ) {
        if (borderThickness <= 0f) {
            drawRoundedRect(x, y, width, height, radius, bgColor);
            return;
        }
        drawRoundedRect(x, y, width, height, radius, borderColor);
        drawRoundedRect(
                x + borderThickness, y + borderThickness,
                width - (2 * borderThickness), height - (2 * borderThickness),
                Math.max(0f, radius - borderThickness),
                bgColor
        );
    }

    public static void drawModernRoundedRect(
            float x, float y, float width, float height, float radius,
            int bgColor,
            int outerBorderColor,
            int innerTopHighlightColor,
            int innerBottomShadowColor,
            float borderThickness
    ) {
        if (borderThickness > 0f && outerBorderColor != 0) {
            drawRoundedRect(x, y, width, height, radius, outerBorderColor);
        }

        float bgX = x + borderThickness;
        float bgY = y + borderThickness;
        float bgWidth = width - (2 * borderThickness);
        float bgHeight = height - (2 * borderThickness);
        float bgRadius = Math.max(0f, radius - borderThickness);

        if (bgWidth <= 0 || bgHeight <= 0) return;

        drawRoundedRect(bgX, bgY, bgWidth, bgHeight, bgRadius, bgColor);

        if (innerTopHighlightColor != 0 && bgHeight > 0) {
            float highlightHeight = 1f;
            if (bgHeight > highlightHeight) {
                drawRoundedRect(bgX, bgY, bgWidth, highlightHeight, bgRadius, innerTopHighlightColor);
            }
        }

        if (innerBottomShadowColor != 0 && bgHeight > 0) {
            float shadowHeight = 1f;
            if (bgHeight > shadowHeight) {
                drawRoundedRect(bgX, bgY + bgHeight - shadowHeight, bgWidth, shadowHeight, bgRadius, innerBottomShadowColor);
            }
        }
    }

    public static void drawModernRoundedRect(
            float x, float y, float width, float height, float radius,
            int bgColor,
            int outerBorderColor,
            int innerTopHighlightColor,
            int innerBottomShadowColor
    ) {
        drawModernRoundedRect(x,y,width,height,radius,bgColor,outerBorderColor,innerTopHighlightColor,innerBottomShadowColor, 1f);
    }


    public static void drawRoundedRectDropShadow(
            float x, float y, float width, float height, float radius,
            int shadowColor, float shadowOffsetX, float shadowOffsetY,
            float shadowRadiusPadding
    ) {
        if (shadowColor != 0) {
            drawRoundedRect(
                    x + shadowOffsetX,
                    y + shadowOffsetY,
                    width,
                    height,
                    Math.max(0f, radius + shadowRadiusPadding),
                    shadowColor
            );
        }
    }
    public static void drawRoundedRectDropShadow(
            float x, float y, float width, float height, float radius,
            int shadowColor, float shadowOffsetX, float shadowOffsetY
    ){
        drawRoundedRectDropShadow(x,y,width,height,radius,shadowColor,shadowOffsetX,shadowOffsetY,0f);
    }


    public static void drawCircle(float centerX, float centerY, float radius, int color) {
        if (radius <= 0f) return;

        float rSq = radius * radius;
        int startX = Math.round(centerX - radius);
        int startY = Math.round(centerY - radius);
        int endX = Math.round(centerX + radius);
        int endY = Math.round(centerY + radius);

        for (int px = startX; px <= endX; px++) {
            for (int py = startY; py <= endY; py++) {
                float dx = (px + 0.5f) - centerX;
                float dy = (py + 0.5f) - centerY;
                if (dx * dx + dy * dy <= rSq) {
                    Gui.drawRect(px, py, px + 1, py + 1, color);
                }
            }
        }
    }

    public static void setColor(int color) {
        float alpha = (float) (color >> 24 & 0xFF) / 255.0f;
        float red = (float) (color >> 16 & 0xFF) / 255.0f;
        float green = (float) (color >> 8 & 0xFF) / 255.0f;
        float blue = (float) (color & 0xFF) / 255.0f;
        GlStateManager.color(red, green, blue, alpha);
    }
}