/**
 * [Copyright] 
 * @author oujinliang
 * Aug 27, 2013 2:20:25 PM
 */
package com.xiaomi.http;

import java.util.HashMap;
import java.util.Map;

/**
 *
 */
public class Converters {
    public static interface Converter {
        Object convert(String str);
    }
    
    private static Converter IntConverter = new Converter() {
        @Override public Object convert(String str) { return Integer.valueOf(str); }
    };
    
    private static Converter LongConverter = new Converter() {
        @Override public Object convert(String str) { return Long.valueOf(str); }
    };

    private static Converter FloatConverter = new Converter() {
        @Override public Object convert(String str) { return Float.valueOf(str); }
    };

    private static Converter DoubleConverter = new Converter() {
        @Override public Object convert(String str) { return Double.valueOf(str); }
    };

    private static Converter BooleanConverter = new Converter() {
        @Override public Object convert(String str) { return Boolean.valueOf(str); }
    };

    private static Converter DefaultConverter = new Converter() {
        @Override public Object convert(String str) { return str; }
    };
    
    private static Map<Class<?>, Converter> converters = buildMap();
    private static Map<Class<?>, Converter> buildMap() {
        Map<Class<?>, Converter> map = new HashMap<Class<?>, Converter>();
        map.put(int.class, IntConverter);
        map.put(Integer.class, IntConverter);
        map.put(long.class, LongConverter);
        map.put(Long.class, LongConverter);
        map.put(float.class, FloatConverter);
        map.put(Float.class, FloatConverter);
        map.put(double.class, DoubleConverter);
        map.put(Double.class, DoubleConverter);
        map.put(boolean.class, BooleanConverter);
        map.put(Boolean.class, BooleanConverter);
        return map;
    }

    public static Object convert(Class<?> cls, String value) {
        Converter converter = converters.get(cls);
        if (converter == null) {
            converter = DefaultConverter;
        }
        return converter.convert(value);
    }
}
