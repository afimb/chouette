/**
 * Copyright (c) 2012 scireum GmbH - Andreas Haufler - aha@scireum.de
 *
 * Permission is hereby granted, free of charge, to any person obtaining a 
 * copy of this software and associated documentation files (the "Software"),
 * to deal in the Software without restriction, including without limitation
 * the rights to use, copy, modify, merge, publish, distribute, sublicense,
 * and/or sell copies of the Software, and to permit persons to whom the
 * Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included
 * in all copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS
 * OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL
 * THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING
 * FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER
 * DEALINGS IN THE SOFTWARE.
 */
package fr.certu.chouette.exchange.netex.xml;

import java.math.BigDecimal;
import java.math.MathContext;
import java.util.regex.Pattern;

/**
 * Provides a simple wrapper for a value which is read from an untyped context
 * like HTTP parameters. It supports elegant <code>null</code> handling and type
 * conversions.
 */
public class Value {

    private Object data;

    /**
     * Determines if the wrapped value is null.
     */
    public boolean isNull() {
        return data == null;
    }

    /**
     * Determines if the wrapped value is an empty string.
     */
    public boolean isEmptyString() {
        return data == null || "".equals(data);
    }

    /**
     * Determines if the wrapped value is NOT null.
     */
    public boolean isFilled() {
        return !isEmptyString();
    }

    /**
     * Returns a {@link Value} which will be empty its value equals one of the
     * given ignored values.
     */
    public Value ignore(String... ignoredValues) {
        if (isEmptyString()) {
            return this;
        }
        for (String val : ignoredValues) {
            if (data.equals(val)) {
                return Value.of(null);
            }
        }
        return this;
    }
    private static final Pattern NUMBER = Pattern.compile("\\d+(\\.\\d+)?");

    /**
     * Checks if the current value is numeric (integer or double).
     */
    public boolean isNumeric() {
        return data != null && data instanceof Number
                || NUMBER.matcher(asString("")).matches();
    }

    /**
     * Returns the raw data.
     */
    public Object get() {
        return data;
    }

    /**
     * Returns the internal data or defaultValue if <code>null</code>.
     */
    public Object get(Object defaultValue) {
        return data == null ? defaultValue : data;
    }

    @SuppressWarnings({"unchecked", "rawtypes"})
    public <T> T coerce(Class<?> targetClazz, T defaultValue) {
        if (data == null) {
            return null;
        }
        if (targetClazz.isAssignableFrom(data.getClass())) {
            return (T) data;
        }
        if (String.class.equals(targetClazz)) {
            return (T) getString();
        }
        if (Integer.class.equals(targetClazz) || int.class.equals(targetClazz)) {
            return (T) getInteger();
        }
        if (Long.class.equals(targetClazz) || long.class.equals(targetClazz)) {
            return (T) getLong();
        }
        if (Boolean.class.equals(targetClazz)
                || boolean.class.equals(targetClazz)) {
            return (T) (Boolean) Boolean.parseBoolean(String.valueOf(data));
        }
        if (BigDecimal.class.equals(targetClazz)) {
            return (T) getBigDecimal(null);
        }
        if (targetClazz.isEnum()) {
            try {
                return (T) Enum.valueOf((Class<Enum>) targetClazz, asString(""));
            } catch (Exception e) {
                return (T) Enum.valueOf((Class<Enum>) targetClazz, asString("").toUpperCase());
            }
        }
        throw new IllegalArgumentException("Cannot convert to: " + targetClazz);
    }

    /**
     * Reads a value with a given type.
     */
    @SuppressWarnings("unchecked")
    public <V> V get(Class<V> clazz, V defaultValue) {
        Object result = get(defaultValue);
        if (result == null || !clazz.isAssignableFrom(result.getClass())) {
            return defaultValue;
        }
        return (V) result;
    }

    /**
     * Returns the data casted to a string, or <code>null</code> if the original
     * data was null.
     */
    public String getString() {
        return isNull() ? null : asString();
    }

    /**
     * Returns the converted string. Returns "" instead of <code>null</code>.
     */
    public String asString() {
        return data == null ? "" : data.toString();
    }

    /**
     * Returns the value as string, or the default value if the given one was
     * null.
     */
    public String asString(String defaultValue) {
        return isNull() ? defaultValue : asString();
    }

    /**
     * Returns the value as boolean variable, or the default value if the given
     * one was null,
     */
    public boolean asBoolean(boolean defaultValue) {
        if (isNull()) {
            return defaultValue;
        }
        if (data instanceof Boolean) {
            return (Boolean) data;
        }
        return Boolean.parseBoolean(String.valueOf(data));
    }

    /**
     * Returns the value as boolean variable. If no value was given,
     * <code>false</code> is returned.
     */
    public boolean asBoolean() {
        return asBoolean(false);
    }

    public int asInt(int defaultValue) {
        try {
            if (isNull()) {
                return defaultValue;
            }
            if (data instanceof Integer) {
                return (Integer) data;
            }
            return Integer.parseInt(String.valueOf(data));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public Integer getInteger() {
        try {
            if (isNull()) {
                return null;
            }
            if (data instanceof Integer) {
                return (Integer) data;
            }
            return Integer.parseInt(String.valueOf(data));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public long asLong(long defaultValue) {
        try {
            if (isNull()) {
                return defaultValue;
            }
            if (data instanceof Long) {
                return (Long) data;
            }
            if (data instanceof Integer) {
                return (Integer) data;
            }
            return Long.parseLong(String.valueOf(data));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public Long getLong() {
        try {
            if (isNull()) {
                return null;
            }
            if (data instanceof Long) {
                return (Long) data;
            }
            return Long.parseLong(String.valueOf(data));
        } catch (NumberFormatException e) {
            return null;
        }
    }

    public double asDouble(double defaultValue) {
        try {
            if (isNull()) {
                return defaultValue;
            }
            if (data instanceof Double) {
                return (Double) data;
            }
            if (data instanceof Long) {
                return (Long) data;
            }
            if (data instanceof Integer) {
                return (Integer) data;
            }
            return Double.parseDouble(String.valueOf(data));
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    public BigDecimal getBigDecimal(BigDecimal defaultValue) {
        try {
            if (isNull()) {
                return defaultValue;
            }
            if (data instanceof Double) {
                return BigDecimal.valueOf((Double) data);
            }
            if (data instanceof Long) {
                return BigDecimal.valueOf((Long) data);
            }
            if (data instanceof Integer) {
                return BigDecimal.valueOf((Integer) data);
            }
            if (data instanceof Long) {
                return BigDecimal.valueOf((Long) data);
            }
            return new BigDecimal(asString("").replace(",", "."),
                    MathContext.UNLIMITED);
        } catch (NumberFormatException e) {
            return defaultValue;
        }
    }

    /**
     * Creates a new wrapper for the given data.
     */
    public static Value of(Object data) {
        Value val = new Value();
        val.data = data;
        return val;
    }

    @Override
    public String toString() {
        return asString();
    }

    @SuppressWarnings("unchecked")
    public <E extends Enum<E>> E asEnum(Class<E> clazz) {
        if (data == null) {
            return null;
        }
        if (clazz.isAssignableFrom(data.getClass())) {
            return (E) data;
        }
        try {
            return Enum.valueOf(clazz, String.valueOf(data));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * Type and null safe implementation of the "Left" function for strings.
     * This will return substring(0, length) and handle <code>null</code> values
     * and shorter strings gracefully.
     */
    public String left(int length) {
        String value = asString();
        if (value == null) {
            return null;
        }
        if (length < 0) {
            length = length * -1;
            if (value.length() < length) {
                return "";
            }
            return value.substring(length);
        } else {
            if (value.length() < length) {
                return value;
            }
            return value.substring(0, length);
        }
    }

    /**
     * Type and null safe implementation of the "right" function for strings. If
     * the given value is positive, it will return substring(strlen - length,
     * strlen). Otherwise it will cut the n right characters.
     */
    public String right(int length) {
        String value = asString();
        if (value == null) {
            return null;
        }
        if (length < 0) {
            length = length * -1;
            if (value.length() < length) {
                return value;
            }
            return value.substring(0, value.length() - length);
        } else {
            if (value.length() < length) {
                return value;
            }
            return value.substring(value.length() - length);
        }
    }

    /**
     * Type, range and null-safe implementation of the substring function.
     */
    public String substring(int start, int length) {
        String value = asString();
        if (value == null) {
            return null;
        }
        if (start > value.length()) {
            return "";
        }
        return value.substring(start, Math.min(value.length(), length));
    }

    /**
     * Type-safe method the get the length of the values' string representation.
     */
    public int length() {
        String value = asString();
        if (value == null) {
            return 0;
        }
        return value.length();
    }

    /**
     * Checks if the value implements the given class.
     */
    public boolean is(Class<?> clazz) {
        return get() != null && clazz.isAssignableFrom(get().getClass());
    }
}
