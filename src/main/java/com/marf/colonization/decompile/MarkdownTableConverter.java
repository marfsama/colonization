package com.marf.colonization.decompile;

import java.lang.reflect.Method;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

public class MarkdownTableConverter {
    private List<String> includedProperties;
    private Map<String, String> customHeaders = new LinkedHashMap<>();
    private Map<String, Function<Object, String>> customFormatter = new HashMap<>();

    public <T> String toMarkdownTable(List<T> objects) {
        if (objects == null || objects.isEmpty()) {
            return "";
        }

        Class<?> clazz = objects.get(0).getClass();
        List<Method> getters = getFilteredGetters(clazz, includedProperties);

        if (getters.isEmpty()) {
            return "No getters found";
        }

        // Calculate column widths
        int[] columnWidths = calculateColumnWidths(objects, getters, customHeaders);

        StringBuilder markdown = new StringBuilder();

        // Create header
        markdown.append("|");
        for (int i = 0; i < getters.size(); i++) {
            Method getter = getters.get(i);
            String propertyName = getPropertyName(getter.getName());
            String header = customHeaders != null ?
                    customHeaders.getOrDefault(propertyName, propertyName) : propertyName;
            markdown.append(" ").append(padToWidth(header, columnWidths[i])).append(" |");
        }
        markdown.append("\n");

        // Create separator
        markdown.append("|");
        for (int i = 0; i < getters.size(); i++) {
            markdown.append(" ").append("-".repeat(columnWidths[i])).append(" |");
        }
        markdown.append("\n");

        // Create rows
        for (T obj : objects) {
            markdown.append("|");
            for (int i = 0; i < getters.size(); i++) {
                Method getter = getters.get(i);
                try {
                    String propertyName = getPropertyName(getter.getName());
                    Object value = getter.invoke(obj);
                    String valueStr = formatValue(propertyName, value);
                    markdown.append(" ").append(padToWidth(valueStr, columnWidths[i])).append(" |");
                } catch (Exception e) {
                    String error = "ERROR";
                    markdown.append(" ").append(padToWidth(error, columnWidths[i])).append(" |");
                }
            }
            markdown.append("\n");
        }

        return markdown.toString();
    }

    public <E extends Object> MarkdownTableConverter withFormatter(String propertyName, Function<E, String> function) {
        customFormatter.put(propertyName, (value) -> function.apply((E)value));
        return this;
    }

    private <T> int[] calculateColumnWidths(List<T> objects, List<Method> getters,
                                            Map<String, String> customHeaders) {
        int[] widths = new int[getters.size()];

        // Initialize with header widths
        for (int i = 0; i < getters.size(); i++) {
            Method getter = getters.get(i);
            String propertyName = getPropertyName(getter.getName());
            String header = customHeaders != null ?
                    customHeaders.getOrDefault(propertyName, propertyName) : propertyName;
            widths[i] = Math.max(widths[i], header.length());
        }

        // Check data widths
        for (T obj : objects) {
            for (int i = 0; i < getters.size(); i++) {
                Method getter = getters.get(i);
                try {
                    String propertyName = getPropertyName(getter.getName());
                    Object value = getter.invoke(obj);
                    String valueStr = formatValue(propertyName, value);
                    widths[i] = Math.max(widths[i], valueStr.length());
                } catch (Exception e) {
                    widths[i] = Math.max(widths[i], "ERROR".length());
                }
            }
        }

        return widths;
    }

    private String padToWidth(String text, int width) {
        if (text.length() > width) {
            // Truncate if too long (add ellipsis for very long values)
            return text.substring(0, Math.max(width - 3, 1)) + "...";
        }

        // Pad with spaces to align
        return text + " ".repeat(width - text.length());
    }

    private List<Method> getFilteredGetters(Class<?> clazz, List<String> includedProperties) {
        List<Method> allGetters = getGetters(clazz);

        if (includedProperties == null || includedProperties.isEmpty()) {
            return allGetters;
        }

        return allGetters.stream()
                .filter(method -> {
                    String propertyName = getPropertyName(method.getName());
                    return includedProperties.contains(propertyName);
                })
                .sorted(Comparator.comparing(method -> {
                    String propertyName = getPropertyName(method.getName());
                    return includedProperties.indexOf(propertyName);
                }))
                .collect(Collectors.toList());
    }

    private List<Method> getGetters(Class<?> clazz) {
        return Arrays.stream(clazz.getMethods())
                .filter(this::isGetter)
                .filter(method -> method.getDeclaringClass() == clazz)
                .collect(Collectors.toList());
    }

    private boolean isGetter(Method method) {
        return method.getParameterCount() == 0 &&
                method.getReturnType() != void.class &&
                (method.getName().startsWith("get") ||
                        method.getName().startsWith("is")) &&
                !method.getName().equals("getClass");
    }

    private String getPropertyName(String methodName) {
        if (methodName.startsWith("get")) {
            return methodName.substring(3, 4).toLowerCase() + methodName.substring(4);
        } else if (methodName.startsWith("is")) {
            return methodName.substring(2, 3).toLowerCase() + methodName.substring(3);
        }
        return methodName;
    }

    private String formatValue(String propertyName, Object value) {
        if (customFormatter.containsKey(propertyName)) {
            return customFormatter.get(propertyName).apply(value);
        }
        if (value == null) {
            return "null";
        }
        if (value instanceof Boolean) {
            return (Boolean) value ? "✓" : "✗";
        }
        if (value instanceof Date) {
            return value.toString();
        }
        if (value instanceof Double || value instanceof Float) {
            return String.format("%.2f", value);
        }
        return value.toString();
    }
}
