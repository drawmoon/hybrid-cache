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
package io.github.drawmoon.hybridcache;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import java.nio.charset.StandardCharsets;
import org.apache.commons.lang3.ArrayUtils;
import org.junit.jupiter.api.Test;

public class HybridStoreTest {
    @Test
    public void testStoreText() {
        HybridStore hybridStore = new HybridStore(x -> {
            x.setKeyPrefix("disk:");
        });

        String key = hybridStore.put("tmp.txt", "abc".getBytes(StandardCharsets.UTF_8));
        assertTrue(key.startsWith("disk:"));
        assertTrue(new File(key.substring(5)).exists());

        byte[] bytes = hybridStore.get(key);
        assertEquals("abc", new String(bytes, StandardCharsets.UTF_8));

        hybridStore.remove(key);
        assertFalse(new File(key.substring(5)).exists());
    }

    @Test
    public void testStoreEmptyText() {
        HybridStore hybridStore = new HybridStore(x -> {
            x.setKeyPrefix("disk:");
        });

        String key = hybridStore.put("tmp.txt", "".getBytes(StandardCharsets.UTF_8));
        assertTrue(key.startsWith("disk:"));
        assertTrue(new File(key.substring(5)).exists());

        byte[] bytes = hybridStore.get(key);
        assertEquals("", new String(bytes, StandardCharsets.UTF_8));

        hybridStore.remove(key);
        assertFalse(new File(key.substring(5)).exists());
    }

    @Test
    public void testMinIOStoreText() {
        HybridStore hybridStore = new HybridStore(x -> {
            x.setStorePlace(HybridStorePlace.DISTRIBUTED);
            x.setConfiguration("http://127.0.0.1:9000");
            x.setAuth("minio");
            x.setPassword("minio");
            x.setBucket("default");
            x.setKeyPrefix("minio:");
        });

        String key = hybridStore.put("tmp.txt", "abc".getBytes(StandardCharsets.UTF_8));
        assertTrue(key.startsWith("minio:"));

        byte[] bytes = hybridStore.get(key);
        assertEquals("abc", new String(bytes, StandardCharsets.UTF_8));

        hybridStore.remove(key);
        assertTrue(ArrayUtils.isEmpty(hybridStore.get(key)));
    }

    @Test
    public void testMinIOStoreEmptyText() {
        HybridStore hybridStore = new HybridStore(x -> {
            x.setStorePlace(HybridStorePlace.DISTRIBUTED);
            x.setConfiguration("http://127.0.0.1:9000");
            x.setAuth("AKIAIOSFODNN7EXAMPLE");
            x.setPassword("minio");
            x.setBucket("default");
            x.setKeyPrefix("minio:");
        });

        String key = hybridStore.put("tmp.txt", "".getBytes(StandardCharsets.UTF_8));
        assertTrue(key.startsWith("minio:"));

        byte[] bytes = hybridStore.get(key);
        assertEquals("", new String(bytes, StandardCharsets.UTF_8));

        hybridStore.remove(key);
        assertTrue(ArrayUtils.isEmpty(hybridStore.get(key)));
    }
}
