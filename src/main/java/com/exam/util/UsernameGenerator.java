package com.exam.util;

import java.security.SecureRandom;

public class UsernameGenerator {

    private static final String UPPER = "ABCDEFGHIJKLMNOPQRSTUVWXYZ";
    private static final String LOWER = "abcdefghijklmnopqrstuvwxyz";
    private static final String DIGITS = "0123456789";
    private static final String SYMBOLS = "@#$%&*";
    private static final String ALL = UPPER + LOWER + DIGITS + SYMBOLS;
    private static final SecureRandom RANDOM = new SecureRandom();

    public static String generate(int length) {
        StringBuilder sb = new StringBuilder();
        sb.append(UPPER.charAt(RANDOM.nextInt(UPPER.length())));
        sb.append(LOWER.charAt(RANDOM.nextInt(LOWER.length())));
        sb.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        sb.append(SYMBOLS.charAt(RANDOM.nextInt(SYMBOLS.length())));
        for (int i = sb.length(); i < length; i++) {
            sb.append(ALL.charAt(RANDOM.nextInt(ALL.length())));
        }
        char[] chars = sb.toString().toCharArray();
        for (int i = chars.length - 1; i > 0; i--) {
            int j = RANDOM.nextInt(i + 1);
            char t = chars[i]; chars[i] = chars[j]; chars[j] = t;
        }
        return new String(chars);
    }
}
