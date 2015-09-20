package com.almasb.java.framework.demo;

public class CompressionDemo {
    /*Encoder encoder = new SevenZip.Compression.LZMA.Encoder();
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(data);
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    encoder.Code(byteArrayInputStream, byteArrayOutputStream, data.length, 0, null);

    System.out.println("Lzma\t" + byteArrayOutputStream.size());*/

    /*Encoder encoder = new SevenZip.Compression.LZMA.Encoder();
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    ObjectOutputStream oos = new ObjectOutputStream(byteArrayOutputStream);
    oos.writeObject(data);
    oos.close();
    ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
    byteArrayOutputStream = new ByteArrayOutputStream();
    encoder.Code(byteArrayInputStream, byteArrayOutputStream, 0, 0, null);
    System.out.println("Lzma\t" + byteArrayOutputStream.size());*/
}
