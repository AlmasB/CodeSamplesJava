package com.almasb.common.compression;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class LZWCompressor {

    private static final int CODER_RANGE_MIN = 20;
    private static final int CODER_RANGE_MAX = 127;

    private final Map<String, Integer> DICTIONARY_ENC = new HashMap<String, Integer>();
    private final Map<Integer, String> DICTIONARY_DEC = new HashMap<Integer, String>();

    private int acc = CODER_RANGE_MIN;

    public LZWCompressor() {
        // build dictionaries
        for (int i = CODER_RANGE_MIN; i < CODER_RANGE_MAX; i++)
            DICTIONARY_ENC.put("" + (char)i, i);

        for (int i = CODER_RANGE_MIN; i < CODER_RANGE_MAX; i++)
            DICTIONARY_DEC.put(i, "" + (char)i);
    }

    public byte[] compress(byte[] data) {
        return compress(new String(data).toCharArray());
    }

    public byte[] compress(char[] data) {
        acc = CODER_RANGE_MIN;

        String w = "";

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        for (char c : data) {
            String wc = w + c;
            if (DICTIONARY_ENC.containsKey(wc))
                w = wc;
            else {
                if (DICTIONARY_ENC.containsKey(w)) {
                    os.write(DICTIONARY_ENC.get(w));
                }
                else {
                    return new byte[] {};
                }

                // Add wc to the dictionary.
                if (acc > -128) {
                    DICTIONARY_ENC.put(wc, --acc);
                }

                w = "" + c;
            }
        }

        // Output the code for w.
        if (!w.equals("")) {
            if (DICTIONARY_ENC.containsKey(w)) {
                os.write(DICTIONARY_ENC.get(w));
            }
            else {
                return new byte[] {};
            }
        }

        //DICTIONARY_ENC.forEach((str, in) -> System.out.println(str + "," + in));

        return os.toByteArray();
    }

    public byte[] decompress(byte[] data) {
        acc = CODER_RANGE_MIN;

        if (data.length == 0)
            return new byte[] {};

        String w = "" + (char)data[0];
        StringBuffer result = new StringBuffer(w);
        for (int i = 1; i < data.length; i++) {
            int k = data[i];

            String entry;
            if (DICTIONARY_DEC.containsKey(k))
                entry = DICTIONARY_DEC.get(k);
            else if (k == acc - 1)
                entry = w + w.charAt(0);
            else
                throw new IllegalArgumentException("Bad compressed k: " + k);

            //System.out.println("k " + k + "entry " + entry);

            result.append(entry);

            // Add w+entry[0] to the dictionary.
            if (acc > -128) {
                DICTIONARY_DEC.put(--acc, w + entry.charAt(0));
                //DICTIONARY_DEC.forEach((str, in) -> System.out.println(str + "," + in));
            }

            w = entry;
        }
        return result.toString().getBytes();
    }

    //    public static List<Integer> compress(String data) {
    //        String w = "";
    //        List<Integer> result = new ArrayList<Integer>();
    //        for (char c : data.toCharArray()) {
    //            String wc = w + c;
    //            if (DICTIONARY_ENC.containsKey(wc))
    //                w = wc;
    //            else {
    //                result.add(DICTIONARY_ENC.get(w));
    //                // Add wc to the dictionary.
    //                if (acc > -128) {
    //                    DICTIONARY_ENC.put(wc, --acc);
    //                }
    //
    //                w = "" + c;
    //            }
    //        }
    //
    //        // Output the code for w.
    //        if (!w.equals(""))
    //            result.add(DICTIONARY_ENC.get(w));
    //
    //        //DICTIONARY_ENC.forEach((str, in) -> System.out.println(str + "," + in));
    //
    //        return result;
    //    }
    //
    //    public static String decompress(List<Integer> compressed) {
    //        acc = 33;
    //
    //        String w = "" + (char)(int)compressed.remove(0);
    //        StringBuffer result = new StringBuffer(w);
    //        for (int k : compressed) {
    //            String entry;
    //            if (DICTIONARY_DEC.containsKey(k))
    //                entry = DICTIONARY_DEC.get(k);
    //            else if (k == acc - 1)
    //                entry = w + w.charAt(0);
    //            else
    //                throw new IllegalArgumentException("Bad compressed k: " + k);
    //
    //            //System.out.println("k " + k + "entry " + entry);
    //
    //            result.append(entry);
    //
    //            // Add w+entry[0] to the dictionary.
    //            if (acc > -128)
    //                DICTIONARY_DEC.put(--acc, w + entry.charAt(0));
    //
    //            w = entry;
    //        }
    //        return result.toString();
    //    }
}
