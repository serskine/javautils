package javautils.parser;

import javautils.math.Vector;
import javautils.math.VectorImpl;

import java.util.HashMap;
import java.util.Map;

public class VectorParser implements Parser<Vector> {
    public static final String OPEN_TAG = "[";
    public static final String CLOSE_TAG = "]";
    public static final String DELIM = ", ";
    public static final String TOKEN_ELEMENTS = "elements";

    @Override
    public String getFormat() {
        return String.format("[%s]", paramToken(TOKEN_ELEMENTS));
    }

    @Override
    public Map<String, String> getTokens(Vector v) {
        final Map<String, String> tokenMap = new HashMap<>();
        final StringBuilder sb = new StringBuilder();
        sb.append(OPEN_TAG);
        for(int i=0; i<v.numDimensions(); i++) {
            if (i>0) {
                sb.append(DELIM);
            }
            sb.append(v.get(i));
        }
        sb.append(CLOSE_TAG);
        tokenMap.put(TOKEN_ELEMENTS, sb.toString());
        return tokenMap;
    }

    @Override
    public Vector createNewItem() {
        return new VectorImpl(0);
    }

    @Override
    public void setTokens(Vector item, Map<String, String> tokens) {
        final String elements = tokens.get(TOKEN_ELEMENTS);
        final String[] elementTokens = elements.split(DELIM);
        final Double[] values = new Double[elementTokens.length];
        for(int i=0; i< elementTokens.length; i++) {
            values[i] = parseDoubleOrDefault(elementTokens[i], 0D);
        }
        item.init(values);
    }
}
