package javautils.parser;

import javautils.Text;

import java.util.*;

import static javautils.Text.extractTokens;

public class ObjectParser implements Parser<Object> {
    public static final String TYPE_NULL = "null";
    public static final String VALUE_NULL = "null";
    public static final String FORMAT_DEFAULT = "{type} {value}";

    public String getFormat() {
        return FORMAT_DEFAULT;
    }

    public Map<String, String> getTokens(Object item) {
        return getTokens(item, (item==null) ? null : item.getClass());
    }

    public Map<String, String> getTokens(Object item, Class<?> typeCls) {

        final Map<String, String> map = new HashMap<>();
        String type = (typeCls==null) ? TYPE_NULL : typeCls.getSimpleName();
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
                value = describe(item);
            }
        }

        map.put("type", type);
        map.put("value", value);

        return map;
    }

    public Object createNewItem() {
        return null;    // This is fine for now. I might change this later.
    }

    public Object parseTokens(Map<String, String> tokens) {
        final String type = tokens.get("type");
        final String value = tokens.get("value");

        if (value==null) {
            throw new RuntimeException("value token was not populated.");
        }

        if (type==null) {
            throw new RuntimeException("type token was not populated");
        }

        Object item = null;

        if (String.class.getSimpleName().equalsIgnoreCase(type)) {
            item = value;
        } else if (VALUE_NULL.equalsIgnoreCase(value)) {
            item = null;
        } else if (Byte.class.getSimpleName().equalsIgnoreCase(type)) {
            item = Parser.getByteParser((byte) 0).parse(value);
        } else if (Short.class.getSimpleName().equalsIgnoreCase(type)) {
            item = Parser.getShortParser((short) 0).parse(value);
        } else if (Character.class.getSimpleName().equalsIgnoreCase(type)) {
            item = Parser.getCharacterParser(null).parse(value);
        } else if (Integer.class.getSimpleName().equalsIgnoreCase(type)) {
            item = Parser.getIntegerParser(0).parse(value);
        } else if (Long.class.getSimpleName().equalsIgnoreCase(type)) {
            item = Parser.getLongParser(0L).parse(value);
        } else if (Float.class.getSimpleName().equalsIgnoreCase(type)) {
            item = Parser.getFloatParser(0F).parse(value);
        } else if (Double.class.getSimpleName().equalsIgnoreCase(type)) {
            item = Parser.getDoubleParser(0D).parse(value);
        } else if (Boolean.class.getSimpleName().equalsIgnoreCase(type)) {
            item = Parser.getBooleanParser(false).parse(value);
        } else {
            throw new RuntimeException("Failed to parse tokens of type " + type);
        }
        return item;
    }

    @Override
    public Object parse(String input) {
        final String format = getFormat();
        final Map<String, String> tokens = extractTokens(format, input);
        return parseTokens(tokens);
    }

    @Override
    public String describe(Object element) {
        final String format = getFormat();
        final Map<String, String> tokens = getTokens(element);
        return Text.substituteTokens(tokens, format);
    }
}
