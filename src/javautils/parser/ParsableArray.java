package javautils.parser;

import java.util.HashMap;
import java.util.Map;

public abstract class ParsableArray implements Parsable {
    Object[] elements;

    public ParsableArray(Object... elements) {
        this.elements = elements;
    }

    public static final String TOKEN_NUM_ELEMENTS = "numElements";
    public static final String TOKEN_ELEMENTS = "elements";
    public static final String FORMAT = String.format("(x{numElements}): {elements}");

    @Override
    public String getFormat() {
        return FORMAT;
    }

    @Override
    public Map<String, String> getTokens() {
        final Map<String, String> map = new HashMap<>();
        final int len = getElements().length;
        map.put(TOKEN_NUM_ELEMENTS, String.valueOf(len));
        final StringBuilder sb = new StringBuilder();
        for(int i=0; i<len; i++) {
            if (i>0) {
                sb.append(getElementDelim());
            }
            sb.append(getElementAsString(getElements()[i]));
        }
        final String elementsToken = sb.toString();
        map.put(TOKEN_ELEMENTS, elementsToken);
        return map;
    }

    @Override
    public void setTokens(Map<String, String> tokenMap) {
        final int numElements;
        final String numElementsToken = tokenMap.get(TOKEN_NUM_ELEMENTS);
        try {
            numElements = Integer.parseInt(numElementsToken);
        } catch (Exception e) {
            throw new IllegalArgumentException("Failed to parse numElements from " + numElementsToken);
        }

        final Object[] elements = new Object[numElements];
        setElements(elements);

        final String elementsToken = tokenMap.get(TOKEN_ELEMENTS);
        final String delim = getElementDelim();
        final String[] elementTokens = elementsToken.split(delim);

        assert elementTokens.length == numElements;
        for(int i=0; i< numElements; i++) {
            final String elementToken = elementTokens[i];
            try {
                final Object element = parseElementFromString(elementToken);
                elements[i] = element;
            } catch (Exception e) {
                throw new RuntimeException("Failed to parse element[" + i + "] from " + elementToken);
            }
        }
    }

    public String getNumToElementsDelim() {
        return ": ";
    }

    public String getElementDelim() {
        return ", ";
    }

    public abstract String getElementAsString(Object e);
    public abstract Object parseElementFromString(String tokenValue);

    public Object[] getElements() {
        return this.elements;
    }

    public void setElements(Object... elements) {
        assert elements != null;
        assert elements.length >= 0;
        this.elements = elements;
    }


    @Override
    public String toString() {
        return describe();
    }
}
