package com.re.cinema_manager.util;
import java.security.MessageDigest;
import java.nio.charset.StandardCharsets;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;

public class PasswordUtil {
    private static final SecureRandom RANDOM = new SecureRandom();
    private static final int SALT_LENGTH = 16;
    private static final String SEPARATOR =":";

    private PasswordUtil(){};

    public static String createHash(String password) {
        validatePassword(password);
        String salt = generateSalt();
        String hash = hashPassword(password, salt);
        return salt + SEPARATOR + hash;
    }

    public static Boolean verifyPassword(String rawPassword, String storedValue){

        if(rawPassword == null ||
                storedValue == null ||
                !storedValue.contains(SEPARATOR)){

            return false;
        }

        String[] part = storedValue.split(SEPARATOR);

        if(part.length != 2){
            return false;
        }

        String salt = part[0];
        String storedHash = part[1];

        // hash password user nhập
        String actualHash = hashPassword(rawPassword, salt);

        // so sánh với hash DB
        return actualHash.equals(storedHash);
    }

    // Hàm Hash mật khẩu lõi với SHA-256
    private static String hashPassword(String password, String salt) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            String input = salt + password;
            byte[] hashedBytes = digest.digest(input.getBytes(StandardCharsets.UTF_8));

            return bytesToHex(hashedBytes);
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("Lỗi: Hệ thống không hỗ trợ thuật toán SHA-256", e);
        }
    }

    private static void validatePassword(String password){
        if(password==null || password.trim().isEmpty()){
            throw new IllegalArgumentException("Password cannot be empty");
        }
        if(password.length()<6){
            throw new IllegalArgumentException("The password must have at least 6 characters");
        }
    }

    // Sinh ngẫu nhiên Salt
    private static String generateSalt(){
        byte[] salt = new byte[SALT_LENGTH];
        RANDOM.nextBytes(salt);
        return bytesToHex(salt);
    }

    private static String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for (int i = 0; i < bytes.length; i++) {
            int v = bytes[i] & 0xFF;
            hexChars[i * 2] = Character.forDigit(v >>> 4, 16);
            hexChars[i * 2 + 1] = Character.forDigit(v & 0x0F, 16);
        }
        return new String(hexChars);
    }

}
