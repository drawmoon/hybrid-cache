package org.hybridcache.utils;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TypeUtils {
    /**
     * 将对象转换为字节数组。
     * @param value 待转换的对象。
     * @return 转换后的字节数组。
     * @throws IOException 可能会引发 {@link IOException} 异常。
     */
    public static byte[] toBytes(Object value) throws IOException {
        ByteArrayOutputStream byteOutputStream = new ByteArrayOutputStream();
        ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteOutputStream);
        objectOutputStream.writeObject(value);
        return byteOutputStream.toByteArray();
    }

    /**
     * 将字节数组转换为对象。
     * @param bytes 待转换的字节数组。
     * @return 转换后的对象。
     * @throws IOException 可能会引发 {@link IOException} 异常。
     * @throws ClassNotFoundException 可能会引发 {@link ClassNotFoundException} 异常。
     */
    public static Object fromBytes(byte[] bytes) throws ClassNotFoundException, IOException {
        ByteArrayInputStream byteInputStream = new ByteArrayInputStream(bytes);
        ObjectInputStream objectInputStream = new ObjectInputStream(byteInputStream);
        return objectInputStream.readObject();
    }
}
