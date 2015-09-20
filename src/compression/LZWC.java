package com.almasb.common.compression;

import java.io.ByteArrayOutputStream;
import java.util.HashMap;

public class LZWC {

    private HashMap<String, Byte> dictionaryEnc = new HashMap<String, Byte>();

    private byte ref = -128;

    public byte[] compress(byte[] data) {
        String w = "";

        ByteArrayOutputStream os = new ByteArrayOutputStream();

        for (byte b : data) {
            String wb = w + b;

            if (dictionaryEnc.containsKey(wb)) {
                w = wb;
            }
            else {
                if (dictionaryEnc.containsKey(w)) {
                    os.write(dictionaryEnc.get(w));
                }

                if (ref < 128)
                    dictionaryEnc.put(wb, ref++);
                else
                    os.write(b);

                w = "" + b;
            }
        }

        // Output the code for w.
        if (!w.equals("")) {
            if (dictionaryEnc.containsKey(w)) {
                os.write(dictionaryEnc.get(w));
            }
            else {
                return new byte[] {};
            }
        }

        return os.toByteArray();
    }

    public byte[] decompress(byte[] data) {
        return new byte[] {};
    }
}
