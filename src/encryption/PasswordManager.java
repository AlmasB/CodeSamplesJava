package com.almasb.common.encryption;

public class PasswordManager {

    public static boolean isValid(final Account acc, final String pass) {
        try {
            return Encryptor.encrypt(pass, acc.getKey()).equals(acc.getEncryptedPassword());
        }
        catch (IllegalArgumentException e) {
            return false;
        }
    }
}
