package com.almasb.common.encryption;

/**
 * A very simple encryption based on OTP, modified
 * to take user keys into account (e.g. user-generated password)
 *
 * @author Almas Baimagambetov (ab607@uni.brighton.ac.uk)
 * @version 1.0
 */
public final class Encryptor {

    private static final String CIPHER_STRING = "?abcdefghijklmnopqrstuvwxyz,.<>[]{}'ABCDEFGHIJKLMNOPQRSTUVWXYZ:;()-+=/*%!1234567890\"~ ";

    /**
     * DO not instantiate
     */
    private Encryptor() {}

    /**
     * Checks if all chars in the text are convertable into values.
     *
     * @param str
     *            message/text
     * @return
     *         {@code true} if string doesn't contain invalid/unsupported characters,
     *         {@code false} otherwise
     */
    public static boolean isValid(String str) {
        for (int i = 0; i < str.length(); i++) {
            if (charToInt(str.charAt(i)) < 0)
                return false;
        }

        return true;
    }

    /**
     * Looks up an int value of the char from the cipher string
     *
     * @param c
     *          char which value is being searched
     * @return
     *          an int value of the char
     */
    private static int charToInt(char c) {
        for (int i = 0; i < CIPHER_STRING.length(); i++) {
            if (CIPHER_STRING.charAt(i) == c)
                return i;
        }

        return -1;
    }

    /**
     * Returns a char from the cipher string with the passed index
     *
     * If index is invalid, converts it to a valid one
     *
     * @param a
     *          index
     * @return
     *         char at that index
     */
    private static char intToChar(int a) {
        if (a >= CIPHER_STRING.length())
            a %= CIPHER_STRING.length();
        else if (a < 0)
            a += CIPHER_STRING.length();

        return CIPHER_STRING.charAt(a);
    }

    /**
     * Combines two chars
     *
     * @param a
     *          first char
     * @param b
     *          second char
     * @return
     *         combined char
     */
    private static char combine(char a, char b) {
        int char1 = charToInt(a);
        int char2 = charToInt(b);

        return intToChar(char1 + char2);
    }

    /**
     * Generates a random string key with given length
     *
     * @param length
     *               key length
     * @return
     *          generated key
     */
    public static String generateKey(int length) {
        String key = "";

        while (key.length() < length) {
            key += intToChar((int) (Math.random() * (CIPHER_STRING.length() + 1)));
        }

        return key;
    }

    public static String encrypt(final String text, final String key) throws IllegalArgumentException {
        if (!isValid(text))
            throw new IllegalArgumentException("String " + text + " contains unsupported characters");

        if (!isValid(key))
            throw new IllegalArgumentException("String " + key + " contains unsupported characters");

        if (key.length() != text.length())
            throw new IllegalArgumentException("Key length: " + key.length() + " != text length: " + text.length());

        String encrypted = "";

        for (int i = 0; i < text.length(); i++) {
            encrypted += combine(text.charAt(i), key.charAt(i));
        }

        return encrypted;
    }
}
