package javautils.extractor;

import javautils.math.Range;
import javautils.parser.Parser;

import java.net.URL;
import java.util.List;

public class URLExtractor implements Extractor<URL> {
    @Override
    public Parser<URL> getParser() {
        return new Parser<URL>() {

            @Override
            public URL parse(String input) {
                try {
                    return new URL(input);
                } catch (Exception e) {
                    return null;
                }
            }

            @Override
            public String describe(URL element) {
                return element.toString();
            }
        };
    }

    @Override
    public List<Range> findAll(String content) {
        return List.of();
    }
}
