package com.almasb.common.compression;

public interface Compressor {
    public byte[] compress(byte[] data);
    public byte[] decompress(byte[] data);
}
