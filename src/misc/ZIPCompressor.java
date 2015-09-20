package com.almasb.common.util;

import java.io.ByteArrayOutputStream;
import java.util.zip.DataFormatException;
import java.util.zip.Deflater;
import java.util.zip.Inflater;

import com.almasb.common.util.Out;

/**
 * Convenience class wrapper for java.util.zip.Deflater / Inflater
 *
 * Uses GZIP compatible compression
 *
 * @author Almas Baimagambetov (ab607@uni.brighton.ac.uk)
 * @version 1.0
 *
 */
public class ZIPCompressor {
    /**
     * Actual compressor
     */
    private Deflater compressor = new Deflater(Deflater.BEST_COMPRESSION, true);

    /**
     * Actual decompressor
     */
    private Inflater decompressor = new Inflater(true);

    /**
     *
     *
     * @param data
     *              original uncompressed bytes
     * @return
     *          compressed bytes
     */
    public byte[] compress(byte[] data) {
        compressor.setInput(data);
        compressor.finish();

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        while (!compressor.finished()) {
            int byteCount = compressor.deflate(buf);
            baos.write(buf, 0, byteCount);
        }

        compressor.reset();

        return baos.toByteArray();
    }

    /**
     *
     * @param data
     *              compressed bytes
     * @return
     *          decompressed original data
     */
    public byte[] decompress(byte[] data) {
        decompressor.setInput(data);

        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        byte[] buf = new byte[8192];
        while (!decompressor.finished()) {
            int byteCount = 0;
            try {
                byteCount = decompressor.inflate(buf);
            }
            catch (DataFormatException e) {
                Out.e("decompress()", "Failed to inflate data", this, e);
                return new byte[] {};
            }
            baos.write(buf, 0, byteCount);
        }

        decompressor.reset();

        return baos.toByteArray();
    }
}
