package javautils.parser;

import java.util.List;

public class ArrayParserImpl<T> implements ArrayParser<T> {

    protected final Parser<T> elementParser;
    protected final String openTag, closeTag, delim;

    public ArrayParserImpl(final Parser<T> elementParser) {
        this(elementParser, "[", "]", ", ");
    }

    public ArrayParserImpl(final Parser<T> elementParser, final String openTag, final String closeTag, final String delim) {
        this.elementParser = elementParser;
        this.openTag = openTag;
        this.closeTag = closeTag;
        this.delim = delim;
    }

    @Override
    public Parser<T> getElementParser() {
        return this.elementParser;
    }

    @Override
    public String getOpenTag() {
        return this.openTag;
    }

    @Override
    public String getCloseTag() {
        return this.closeTag;
    }

    @Override
    public String getDelim() {
        return this.delim;
    }
}
