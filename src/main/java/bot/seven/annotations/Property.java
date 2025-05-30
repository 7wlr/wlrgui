package bot.seven.wlrgui.annotations;

import bot.seven.wlrgui.components.GuiComponentBase;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Annotation to mark fields that should be turned into GUI components.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface Property {

    /**
     * The type of GUI component to generate.
     * e.g., Button.class, Checkbox.class, Dropdown.class, Slider.class, Textfield.class
     */
    Class<? extends GuiComponentBase> type();

    /**
     * The display name/label for this component in the GUI.
     */
    String name();

    /**
     * (Optional) The category this component belongs to for grouping in the GUI.
     */
    String category() default "General";

    /**
     * (Optional) For ordering components within the same category. Lower numbers appear first.
     */
    int order() default 0;

    /**
     * (For Dropdown type) Array of string options for the dropdown list.
     * The annotated field should be an 'int' representing the selected index.
     */
    String[] options() default {};

    /**
     * (For Textfield type) Placeholder text to display when the text field is empty.
     * Your Textfield component will need custom logic to draw this.
     */
    String placeholder() default "";

    /**
     * (For Textfield type) Maximum number of characters allowed in the text field.
     */
    int maxLength() default 256;

    /**
     * (For Slider type) The minimum value of the slider.
     * The annotated field should be a numeric type (int, float, double).
     */
    double minValue() default 0.0;

    /**
     * (For Slider type) The maximum value of the slider.
     */
    double maxValue() default 100.0;

    /**
     * (For Slider type) The step increment/decrement value for the slider.
     * Use 0 for continuous slider if your Slider component supports it.
     */
    double step() default 1.0;

    /**
     * (For Slider type) A format string (like "%.2f") for displaying the slider's current value.
     * If empty, a default format will be used.
     */
    String displayFormat() default "%.2f";

    /**
     * (For Button type) The name of a public, no-argument method in the annotated class
     * that should be called when the button is clicked.
     */
    String onClickMethod() default "";
}