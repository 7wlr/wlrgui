package bot.seven.gen;

import bot.seven.wlrgui.annotations.Property;
import bot.seven.components.*;


import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Comparator;
import java.util.stream.Collectors;

/**
 * Generates GUI components based on @Property annotations on fields of a settings object.
 */
public class GuiGen {

    private int nextComponentId = 0; // Simple ID generator for components

    /**
     * Generates GUI components from a settings object.
     *
     * @param settingsObject The object containing fields annotated with @Property.
     * @param componentWidth The default width for components like Textfields, Sliders, Dropdowns.
     * @return A map where keys are category names (String) and values are lists of GuiComponentBase
     *         for that category. Categories and components are ordered by 'order' in @Property.
     */
    public Map<String, List<GuiComponentBase>> generateComponents(Object settingsObject, int componentWidth) {
        Map<String, List<GuiComponentBase>> categorizedComponents = new LinkedHashMap<>();
        Field[] allFields = settingsObject.getClass().getDeclaredFields();

        List<Field> annotatedFields = Arrays.stream(allFields)
                .filter(f -> f.isAnnotationPresent(Property.class))
                .sorted(Comparator.comparingInt(f -> f.getAnnotation(Property.class).order()))
                .collect(Collectors.toList());

        for (Field field : annotatedFields) {
            field.setAccessible(true);
            Property annotation = field.getAnnotation(Property.class);
            GuiComponentBase component = null;
            Class<?> fieldType = field.getType();
            int currentId = nextComponentId++;

            int tempX = 0; // Placeholder X, actual position set by ConfigurableGuiScreen
            int tempY = 0; // Placeholder Y

            try {
                if (annotation.type() == Button.class) {
                    Runnable onClickAction = () -> {};
                    if (!annotation.onClickMethod().isEmpty()) {
                        try {
                            Method method = settingsObject.getClass().getMethod(annotation.onClickMethod());
                            method.setAccessible(true);
                            onClickAction = () -> {
                                try {
                                    method.invoke(settingsObject);
                                } catch (IllegalAccessException | InvocationTargetException e) {
                                    e.printStackTrace();
                                }
                            };
                        } catch (NoSuchMethodException e) {
                            e.printStackTrace();
                        }
                    }
                    component = new Button(currentId, tempX, tempY, componentWidth, annotation.name(), onClickAction);
                }

                else if (annotation.type() == Checkbox.class) {
                    if (fieldType != boolean.class && fieldType != Boolean.class) {
                        continue;
                    }
                    boolean initialValue = field.getBoolean(settingsObject);
                    component = new Checkbox(currentId, tempX, tempY, annotation.name(), initialValue,
                            (Boolean newValue) -> {
                                try {
                                    field.setBoolean(settingsObject, newValue);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            });
                }

                else if (annotation.type() == Dropdown.class) {
                    if (fieldType != int.class && fieldType != Integer.class) {
                        continue;
                    }
                    int initialIndex = field.getInt(settingsObject);
                    List<String> options = new ArrayList<>(Arrays.asList(annotation.options()));
                    component = new Dropdown(currentId, tempX, tempY, componentWidth, annotation.name(), options, initialIndex,
                            (Integer index, String selectedValueText) -> {
                                try {
                                    field.setInt(settingsObject, index);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            });
                }

                else if (annotation.type() == Textfield.class) {
                    if (fieldType != String.class) {
                        continue;
                    }
                    String initialText = (String) field.get(settingsObject);
                    if (initialText == null) initialText = "";

                    Textfield tf = new Textfield(currentId, tempX, tempY, componentWidth, annotation.name(), initialText,
                            s -> true,
                            (String newText) -> {
                                try {
                                    field.set(settingsObject, newText);
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            });
                    tf.textField.setMaxStringLength(annotation.maxLength());
                    component = tf;
                }

                else if (annotation.type() == Slider.class) {
                    float initialValue = 0f;
                    if (fieldType == float.class || fieldType == Float.class) {
                        initialValue = field.getFloat(settingsObject);
                    } else if (fieldType == double.class || fieldType == Double.class) {
                        initialValue = (float) field.getDouble(settingsObject);
                    } else if (fieldType == int.class || fieldType == Integer.class) {
                        initialValue = (float) field.getInt(settingsObject);
                    } else if (fieldType == long.class || fieldType == Long.class) {
                        initialValue = (float) field.getLong(settingsObject);
                    } else {
                        continue;
                    }

                    final String displayFormatString = annotation.displayFormat().isEmpty() ? "%.2f" : annotation.displayFormat();

                    component = new Slider(currentId, tempX, tempY, componentWidth, annotation.name(),
                            initialValue,
                            (float) annotation.minValue(),
                            (float) annotation.maxValue(),
                            (float) annotation.step(),
                            (Float val) -> String.format(displayFormatString, val.floatValue()),
                            (Float newValue) -> {
                                try {
                                    if (fieldType == float.class || fieldType == Float.class) {
                                        field.setFloat(settingsObject, newValue.floatValue());
                                    } else if (fieldType == double.class || fieldType == Double.class) {
                                        field.setDouble(settingsObject, newValue.doubleValue());
                                    } else if (fieldType == int.class || fieldType == Integer.class) {
                                        field.setInt(settingsObject, Math.round(newValue.floatValue()));
                                    } else if (fieldType == long.class || fieldType == Long.class) {
                                        field.setLong(settingsObject, Math.round(newValue.floatValue()));
                                    }
                                } catch (IllegalAccessException e) {
                                    e.printStackTrace();
                                }
                            });
                }

                if (component != null) {
                    categorizedComponents.computeIfAbsent(annotation.category(), k -> new ArrayList<>()).add(component);
                }

            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return categorizedComponents;
    }
}