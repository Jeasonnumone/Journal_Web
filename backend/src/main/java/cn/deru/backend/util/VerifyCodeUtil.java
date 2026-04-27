package cn.deru.backend.util;

import java.security.SecureRandom;

public class VerifyCodeUtil {

    private static final SecureRandom RANDOM = new SecureRandom();
    private static final String DIGITS = "0123456789";

    public static String generateCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        }
        return sb.toString();
    }
}
