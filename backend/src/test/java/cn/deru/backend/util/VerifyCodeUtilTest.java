package cn.deru.backend.util;

import java.security.SecureRandom;

import static org.junit.jupiter.api.Assertions.*;

class VerifyCodeUtilTest {
    static final SecureRandom RANDOM = new SecureRandom();
    static final String DIGITS = "0123456789";

    public static String generateCode(int length) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(DIGITS.charAt(RANDOM.nextInt(DIGITS.length())));
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        VerifyCodeUtilTest verifyCodeUtil = new VerifyCodeUtilTest();
//        String code = verifyCodeUtil.generateCode(4);
        int a = verifyCodeUtil.RANDOM.nextInt(5);
        System.out.println(a);
    }
}