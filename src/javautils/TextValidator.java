package javautils;

import java.util.Arrays;
import java.util.function.Predicate;

public class TextValidator implements Predicate<String> {

    private String openRegex, middleRegex, closeRegex;

    public TextValidator() {
        this("", ".*", "");
    }

    public TextValidator(String openRegex, String middleRegex, String closeRegex) {
        this.openRegex = openRegex;
        this.middleRegex = middleRegex;
        this.closeRegex = closeRegex;
    }

    public String getRegex() {
        return "" + openRegex + middleRegex + closeRegex;
    }

    @Override
    public boolean test(String s) {
        return (s!=null)  && s.matches(getRegex());
    }

    public static TextValidator any(TextValidator... validators) {
        final String[] a = Arrays.stream(validators).sequential().map(TextValidator::getRegex).toArray(String[]::new);
        final String regex = Text.join("(", "||", ")", a);
        return new TextValidator("", regex, "");
    }

    public static TextValidator all(TextValidator... validators) {
        final String[] a = Arrays.stream(validators).sequential().map(TextValidator::getRegex).toArray(String[]::new);
        final String regex = Text.join("(", "&&", ")", a);
        return new TextValidator("", regex, "");
    }
}
