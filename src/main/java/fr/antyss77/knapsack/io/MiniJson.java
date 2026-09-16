package fr.antyss77.knapsack.io;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * A minimal JSON reader, just capable enough to parse an item catalogue.
 *
 * <p>Not a general-purpose JSON library and not public API: it exists so
 * {@link CatalogLoader} needs no third-party dependency, in keeping with the
 * rest of Knapsack. If your project already depends on Jackson, Gson or
 * org.json, feel free to bypass this class entirely and build
 * {@link fr.antyss77.knapsack.core.ItemDefinition}s from your own parser —
 * {@link CatalogLoader} only needs the same {@code Map}/{@code List}/scalar
 * shape this class produces.
 *
 * <p>Values map to plain Java types: JSON object → {@code Map<String,Object>}
 * (insertion order preserved), array → {@code List<Object>}, string →
 * {@code String}, boolean → {@code Boolean}, {@code null} → {@code null}, and
 * a number → {@code Integer} when it has no fractional part or exponent and
 * fits in an {@code int}, {@code Double} otherwise. That split matters: it is
 * what lets a loaded {@code "damage": 35} match a hand-declared
 * {@code Attribute.of("damage", Integer.class)} constant.
 */
final class MiniJson {

    private final String text;
    private int pos;

    private MiniJson(String text) {
        this.text = text;
    }

    static Object parse(String text) {
        MiniJson parser = new MiniJson(text);
        parser.skipWhitespace();
        Object value = parser.readValue();
        parser.skipWhitespace();
        if (parser.pos != text.length()) {
            throw parser.error("unexpected trailing content");
        }
        return value;
    }

    private Object readValue() {
        char c = peek();
        return switch (c) {
            case '{' -> readObject();
            case '[' -> readArray();
            case '"' -> readString();
            case 't', 'f' -> readBoolean();
            case 'n' -> readNull();
            default -> readNumber();
        };
    }

    private Map<String, Object> readObject() {
        expect('{');
        Map<String, Object> result = new LinkedHashMap<>();
        skipWhitespace();
        if (peek() == '}') {
            pos++;
            return result;
        }
        while (true) {
            skipWhitespace();
            String key = readString();
            skipWhitespace();
            expect(':');
            skipWhitespace();
            result.put(key, readValue());
            skipWhitespace();
            char c = next();
            if (c == '}') {
                return result;
            }
            if (c != ',') {
                throw error("expected ',' or '}' in object");
            }
        }
    }

    private List<Object> readArray() {
        expect('[');
        List<Object> result = new ArrayList<>();
        skipWhitespace();
        if (peek() == ']') {
            pos++;
            return result;
        }
        while (true) {
            skipWhitespace();
            result.add(readValue());
            skipWhitespace();
            char c = next();
            if (c == ']') {
                return result;
            }
            if (c != ',') {
                throw error("expected ',' or ']' in array");
            }
        }
    }

    private String readString() {
        expect('"');
        StringBuilder sb = new StringBuilder();
        while (true) {
            char c = next();
            if (c == '"') {
                return sb.toString();
            }
            if (c == '\\') {
                char escaped = next();
                switch (escaped) {
                    case '"' -> sb.append('"');
                    case '\\' -> sb.append('\\');
                    case '/' -> sb.append('/');
                    case 'n' -> sb.append('\n');
                    case 't' -> sb.append('\t');
                    case 'r' -> sb.append('\r');
                    case 'b' -> sb.append('\b');
                    case 'f' -> sb.append('\f');
                    case 'u' -> {
                        String hex = text.substring(pos, pos + 4);
                        pos += 4;
                        sb.append((char) Integer.parseInt(hex, 16));
                    }
                    default -> throw error("invalid escape '\\" + escaped + "'");
                }
            } else {
                sb.append(c);
            }
        }
    }

    private Boolean readBoolean() {
        if (text.startsWith("true", pos)) {
            pos += 4;
            return Boolean.TRUE;
        }
        if (text.startsWith("false", pos)) {
            pos += 5;
            return Boolean.FALSE;
        }
        throw error("invalid literal");
    }

    private Object readNull() {
        if (text.startsWith("null", pos)) {
            pos += 4;
            return null;
        }
        throw error("invalid literal");
    }

    private Object readNumber() {
        int start = pos;
        boolean isDouble = false;
        if (peek() == '-') {
            pos++;
        }
        while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
            pos++;
        }
        if (pos < text.length() && text.charAt(pos) == '.') {
            isDouble = true;
            pos++;
            while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
                pos++;
            }
        }
        if (pos < text.length() && (text.charAt(pos) == 'e' || text.charAt(pos) == 'E')) {
            isDouble = true;
            pos++;
            if (pos < text.length() && (text.charAt(pos) == '+' || text.charAt(pos) == '-')) {
                pos++;
            }
            while (pos < text.length() && Character.isDigit(text.charAt(pos))) {
                pos++;
            }
        }
        String number = text.substring(start, pos);
        if (number.isEmpty() || number.equals("-")) {
            throw error("invalid number");
        }
        if (isDouble) {
            return Double.parseDouble(number);
        }
        try {
            return Integer.parseInt(number);
        } catch (NumberFormatException tooBig) {
            return Double.parseDouble(number);
        }
    }

    private void skipWhitespace() {
        while (pos < text.length() && Character.isWhitespace(text.charAt(pos))) {
            pos++;
        }
    }

    private char peek() {
        if (pos >= text.length()) {
            throw error("unexpected end of input");
        }
        return text.charAt(pos);
    }

    private char next() {
        char c = peek();
        pos++;
        return c;
    }

    private void expect(char c) {
        if (next() != c) {
            throw error("expected '" + c + "'");
        }
    }

    private IllegalArgumentException error(String message) {
        return new IllegalArgumentException("JSON parse error at offset " + pos + ": " + message);
    }
}
