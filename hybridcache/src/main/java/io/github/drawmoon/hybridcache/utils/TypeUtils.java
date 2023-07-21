package io.github.drawmoon.hybridcache.utils;

import java.io.IOException;

import com.fasterxml.jackson.databind.ObjectMapper;

import de.undercouch.bson4jackson.BsonFactory;

public class TypeUtils {
    /**
     * 将对象转换为字节数组。
     * @param value 待转换的对象。
     * @return 转换后的字节数组。
     * @throws IOException 可能会引发 {@link IOException} 异常。
     */
    public static byte[] toBytes(Object value) throws IOException {
        if (value instanceof byte[]) {
            return (byte[]) value;
        }

        ObjectMapper mapper = new ObjectMapper(new BsonFactory());
        return mapper.writeValueAsBytes(value);
    }

    /**
     * 将字节数组转换为对象。
     * @param <T> 转换为对象的对象类型。
     * @param bytes 待转换的字节数组。
     * @param clazz 转换为对象的对象类型。
     * @return 转换后的对象。
     * @throws IOException 可能会引发 {@link IOException} 异常。
     * @throws ClassNotFoundException 可能会引发 {@link ClassNotFoundException} 异常。
     */
    public static <T> T fromBytes(byte[] bytes, Class<T> clazz) throws ClassNotFoundException, IOException {
        if (clazz.isAssignableFrom(byte[].class)) {
            return clazz.cast(bytes);
        }

        ObjectMapper mapper = new ObjectMapper(new BsonFactory());
        return mapper.readValue(bytes, clazz);
    }
}
