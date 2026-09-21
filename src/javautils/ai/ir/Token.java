package javautils.ai.ir;

import java.io.Serializable;

public class Token implements Serializable {
    public final String text;

    public Token(final String text) {
        assert text != null;
        this.text = text.toLowerCase();
    }

    @Override
    public int hashCode() {
        return text.hashCode();
    }

    @Override
    public boolean equals(Object other) {
        if (other instanceof Token) {
            return text.equalsIgnoreCase(((Token) other).text);
        } else {
            return false;
        }
    }
}
