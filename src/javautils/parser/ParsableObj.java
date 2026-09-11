package javautils.parser;

import javautils.Text;

import java.util.*;

public class ParsableObj implements Parsable {
    private Object item;
    private Class<?> type;

    public static final String TYPE_NULL = "null";
    public static final String VALUE_NULL = "null";
    public static final String FORMAT_DEFAULT = "{type} {value}";

    private static final Map<Class<?>, String> FORMAT_MAP = new HashMap<>();
    static {
        FORMAT_MAP.put(Byte.class,FORMAT_DEFAULT);
        FORMAT_MAP.put(Short.class, FORMAT_DEFAULT);
        FORMAT_MAP.put(Character.class, FORMAT_DEFAULT);
        FORMAT_MAP.put(Integer.class, FORMAT_DEFAULT);
        FORMAT_MAP.put(Long.class, FORMAT_DEFAULT);
        FORMAT_MAP.put(Float.class, FORMAT_DEFAULT);
        FORMAT_MAP.put(Double.class,FORMAT_DEFAULT);
        FORMAT_MAP.put(String.class, FORMAT_DEFAULT);
        FORMAT_MAP.put(Boolean.class, FORMAT_DEFAULT);
    }

    public ParsableObj(Class<?> type, Object item) {
        this.type = type;
        this.item = item;
    }

    public static <T> ParsableObj create(Class<?> type, T value) {
        return new ParsableObj(type, value);
    }

    public static ParsableObj create(final Object item) {
        return  (item==null)
                ?   new ParsableObj(null, null)
                :   new ParsableObj(item.getClass(), item);
    }

    @Override
    public String getFormat() {
        return FORMAT_MAP.getOrDefault(type, FORMAT_DEFAULT);
    }

    @Override
    public Map<String, String> getTokens() {
        final Map<String, String> map = new HashMap<>();
        String type = (this.type==null) ? TYPE_NULL : this.type.getSimpleName();
        String value = VALUE_NULL;
        if (item!=null) {
            final Class<?> cls = item.getClass();
            if (Byte.class.equals(cls)) {
                value = Byte.toString((byte) item);
            } else if (Short.class.equals(cls)) {
                value = Short.toString((short) item);
            } else if (Character.class.equals(cls)) {
                value = Character.toString((Character) item);
            } else if (Integer.class.equals(cls)) {
                value = Integer.toString((Integer) item);
            } else if (Long.class.equals(cls)) {
                value = Long.toString((Long) item);
            } else if (Float.class.equals(cls)) {
                value = Float.toString((Float) item);
            } else if (Double.class.equals(cls)) {
                value = Double.toString((Double) item);
            } else if (String.class.equals(cls)) {
                value = (String) item;
            } else if (Boolean.class.equals(cls)) {
                value = Boolean.toString((Boolean) item);
            } else if (item instanceof Parsable) {
                value = ((Parsable) item).describe();
            }
        }

        map.put("type", type);
        map.put("value", value);

        return map;
    }

    @Override
    public void setTokens(Map<String, String> tokens) {
        final String type = tokens.get("type");
        final String value = tokens.get("value");

        if (value==null) {
            throw new RuntimeException("value token was not populated.");
        }

        if (type==null) {
            throw new RuntimeException("type token was not populated");
        }

        this.item = null;
        if (String.class.getSimpleName().equalsIgnoreCase(type)) {
            item = value;
        } else if (VALUE_NULL.equalsIgnoreCase(value)) {
            item = null;
        } else if (Byte.class.getSimpleName().equalsIgnoreCase(type)) {
            item = Byte.parseByte(value);
        } else if (Short.class.getSimpleName().equalsIgnoreCase(type)) {
            item = Short.parseShort(value);
        } else if (Character.class.getSimpleName().equalsIgnoreCase(type)) {
            item = value.charAt(0);
        } else if (Integer.class.getSimpleName().equalsIgnoreCase(type)) {
            item = Integer.parseInt(value);
        } else if (Long.class.getSimpleName().equalsIgnoreCase(type)) {
            item = Long.parseLong(value);
        } else if (Float.class.getSimpleName().equalsIgnoreCase(type)) {
            item = Float.parseFloat(value);
        } else if (Double.class.getSimpleName().equalsIgnoreCase(type)) {
            item = Double.parseDouble(value);
        } else if (Boolean.class.getSimpleName().equalsIgnoreCase(type)) {
            item = Boolean.parseBoolean(value);
        } else if ( this.type.getSimpleName().equalsIgnoreCase(type) && item instanceof Parsable) {
            final Parsable p = (Parsable) item;
            p.parseFromText(value);
        } else {
            throw new RuntimeException("Expected type " + this.type.getSimpleName() + " but was told " + type);
        }
    }

    @Override
    public String toString() {
        return Objects.toString(item);
    }

    public <T> T getItem() {
        return (T) item;
    }
}
