/*
 * MIT License
 *
 * Copyright (c) 2023 drsh
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the “Software”), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.drawmoon.hybridcache.utils;

import com.fasterxml.jackson.databind.ObjectMapper;
import de.undercouch.bson4jackson.BsonFactory;
import java.io.IOException;

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
