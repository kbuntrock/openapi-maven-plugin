package com.github.kbuntrock.configuration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Kevin Buntrock
 */
public class OperationIdHelper {

    private static Map<String, TokenType> idToTokenType = new HashMap<>();

    static {
        for (TokenType type : TokenType.values()) {
            idToTokenType.put(type.id, type);
        }
    }

    private static String REGEX = "(\\{)(.*?)(})";

    private static Pattern pattern = Pattern.compile(REGEX);

    private List<Token> tokens = new ArrayList<>();

    public OperationIdHelper(final String configuration) {
        Matcher matcher = pattern.matcher(configuration);
        List<TokenType> contenu = new ArrayList<>();
        while (matcher.find()) {
            TokenType tokenType = idToTokenType.get(matcher.group(2));
            if (tokenType != null) {
                contenu.add(tokenType);
            } else {
                throw new RuntimeException("Token type \"" + matcher.group(2) + "\" does not exist.");
            }
        }
        String[] splitted = configuration.split(REGEX);
        int index;
        for (index = 0; index < splitted.length; index++) {
            if (!splitted[index].isEmpty()) {
                tokens.add(new Token(TokenType.STRING, splitted[index]));
            }
            if (index < contenu.size()) {
                tokens.add(new Token(contenu.get(index)));
            }
        }
        if (splitted.length == 0) {
            for (TokenType c : contenu) {
                tokens.add(new Token(c));
            }
        }
    }

    public String toOperationId(String className, String tagName, String methodName) {
        StringBuilder sb = new StringBuilder();
        for (Token token : tokens) {
            switch (token.type) {
                case STRING:
                    sb.append(token.value);
                    break;
                case TAG_NAME:
                    sb.append(tagName);
                    break;
                case METHOD_NAME:
                    sb.append(methodName);
                    break;
                case CLASS_NAME:
                    sb.append(className);
                    break;
            }
        }
        return sb.toString();
    }

    private class Token {
        TokenType type;
        String value;

        public Token(TokenType type) {
            this.type = type;
        }

        public Token(TokenType type, String value) {
            this.type = type;
            this.value = value;
        }

        public TokenType getType() {
            return type;
        }

        public String getValue() {
            return value;
        }
    }

    private enum TokenType {
        STRING(null),
        CLASS_NAME("class_name"),
        METHOD_NAME("method_name"),
        TAG_NAME("tag_name");

        private final String id;

        TokenType(String id) {
            this.id = id;
        }

        public String getId() {
            return id;
        }
    }
}
