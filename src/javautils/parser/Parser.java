package javautils.parser;

import javautils.Logger;
import javautils.Text;
import javautils.math.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public interface Parser<T> {

    default String openTokenParam() { return "{";   }
    default String closeTokenParam() { return "}";  }
    default String paramToken(final String name) { return openTokenParam() + name + closeTokenParam();}

    T parse(final String input);
    String describe(T element);

    static Parser<Integer> getIntegerParser(final Integer dValue) {
        return new Parser<Integer>() {

            @Override
            public Integer parse(String string) {
                try {
                    return Integer.parseInt(string);
                } catch (NumberFormatException e) {
                    Logger.warn(e.getMessage(), e);
                    return dValue;
                }
            }

            @Override
            public String describe(Integer element) {
                return Integer.toString(element);
            }
        };
    }

    static Parser<Float> getFloatParser(final Float dValue) {
        return new Parser<Float>() {

            @Override
            public Float parse(String string) {
                try {
                    return Float.parseFloat(string);
                } catch (NumberFormatException e) {
                    Logger.warn(e.getMessage(), e);
                    return dValue;
                }
            }

            @Override
            public String describe(Float element) {
                return Float.toString(element);
            }
        };
    }

    static Parser<Double> getDoubleParser(final Double dValue) {
        return new Parser<Double>() {

            @Override
            public Double parse(String string) {
                try {
                    return Double.parseDouble(string);
                } catch (NumberFormatException e) {
                    Logger.warn(e.getMessage(), e);
                    return dValue;
                }
            }

            @Override
            public String describe(Double element) {
                return Double.toString(element);
            }
        };
    }

    static Parser<Boolean> getBooleanParser(final Boolean dValue) {
        return new Parser<Boolean>() {

            @Override
            public Boolean parse(String string) {
                try {
                    return Boolean.parseBoolean(string);
                } catch (NumberFormatException e) {
                    Logger.warn(e.getMessage(), e);
                    return dValue;
                }
            }

            @Override
            public String describe(Boolean element) {
                return Boolean.toString(element);
            }
        };
    }

    static Parser<Byte> getByteParser(final Byte dValue) {
        return new Parser<Byte>() {

            @Override
            public Byte parse(String string) {
                try {
                    return Byte.parseByte(string);
                } catch (NumberFormatException e) {
                    Logger.warn(e.getMessage(), e);
                    return dValue;
                }
            }

            @Override
            public String describe(Byte element) {
                return Byte.toString(element);
            }
        };
    }

    static Parser<Short> getShortParser(final Short dValue) {
        return new Parser<Short>() {

            @Override
            public Short parse(String string) {
                try {
                    return Short.parseShort(string);
                } catch (NumberFormatException e) {
                    Logger.warn(e.getMessage(), e);
                    return dValue;
                }
            }

            @Override
            public String describe(Short element) {
                return Short.toString(element);
            }
        };
    }

    static Parser<Long> getLongParser(final Long dValue) {
        return new Parser<Long>() {

            @Override
            public Long parse(String string) {
                try {
                    return Long.parseLong(string);
                } catch (NumberFormatException e) {
                    Logger.warn(e.getMessage(), e);
                    return dValue;
                }
            }

            @Override
            public String describe(Long element) {
                return Long.toString(element);
            }
        };
    }

    static Parser<Character> getCharacterParser(final Character dValue) {
        return new Parser<Character>() {

            @Override
            public Character parse(String string) {
                try {
                    return string.charAt(0);
                } catch (NumberFormatException e) {
                    Logger.warn(e.getMessage(), e);
                    return dValue;
                }
            }

            @Override
            public String describe(Character element) {
                return Character.toString(element);
            }
        };
    }



}
