package com.zad8_bd;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

public class Utils {
    public static String stringTrimLastCharacters(String s, int n) {
        if (s.length() <= n) {
            return "";
        }
        return s.substring(0, s.length() - n);
    }

    static private final String COMMA = ", ";
    static public <T> String repeatAndAddCommas(ArrayList<T> items, Function<T, String> f) {
        StringBuilder builder = new StringBuilder();
        for (T item : items) {
            String str = f.apply(item);
            if (str == null) {
                continue;
            }
            builder.append(str).append(COMMA);
        }
        return Utils.stringTrimLastCharacters(builder.toString(), COMMA.length());
    }

    public static final int MAX_IDENTIFIER_LENGTH = 64;
    public static boolean isAlphaNumeric(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || (c >= '0' && c <= '9');
    }
    // https://mariadb.com/kb/en/identifier-names/
    public static boolean isAValidSQLIdentifier(String s) {
        if (s.length() >= MAX_IDENTIFIER_LENGTH) {
            return false;
        }
        if (isAReservedMariaDBIdentifier(s)) {
            return false;
        }
        if (s.isEmpty()) return false;
        if (s.toCharArray()[0] == '`') {
            // Quoted identifiers
            if (s.length() == 1) return false; // The identifier is an unclosed quote
            char lastChar = 0;
            int i = 0;
            Character nextExpect = null;
            for (char c : s.toCharArray()) {
                if (i == s.length() - 1) {
                    if (lastChar == ' ') {
                        return false; // Quoted identifiers cannot end with a space
                    }
                    lastChar = c;
                    break;
                }
                lastChar = c;
                if (nextExpect != null && c != nextExpect) {
                    return false;
                }
                if (c == '`' && i != 0) {
                    nextExpect = '`'; // Needs to escape the quote
                }
                i += 1;
            }
            if (lastChar != '`') {
                return false;
            }
        } else {
            // Note: does not support extended characters, only ASCII (Extended: U+0080 .. U+FFFF)
            for (char c : s.toCharArray()) {
                if (!Utils.isAlphaNumeric(c) && c != '$' && c != '_' && c != '.') {
                    return false;
                }
            }
            // Cannot end with a qualifier
            if (List.of(s.toCharArray()).getLast()[0] == '.') {
                return false;
            }
        }
        return true;
    }

    // https://mariadb.com/kb/en/reserved-words/
    // Not included in the file:
    // `VECTOR`	From MariaDB 11.7
    // `ROW_NUMBER` From MariaDB 10.7
    // `OFFSET` Added in MariaDB 10.6.0
    private static final String RESERVED_KEYWORD_FILE_PATH = "mariadb_reserved_keywords.txt";
    private static ArrayList<String> reservedKeywords;
    public static boolean isAReservedMariaDBIdentifier(String s) {
        if (reservedKeywords == null) {
            try (BufferedReader reader = new BufferedReader(new FileReader(RESERVED_KEYWORD_FILE_PATH))) {
                reservedKeywords = reader.lines().collect(Collectors.toCollection(ArrayList::new));
            } catch (IOException e) {
                System.err.println("Could NOT read " + RESERVED_KEYWORD_FILE_PATH + ". Will be returning false. [" + e.getMessage() + "]");
                return false;
            }
        }
        return reservedKeywords.contains(s.toUpperCase());
    }
}
