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

import org.junit.jupiter.api.Test;

public class HybridCacheTest {
    @Test
    public void testCacheString() {
        try (HybridCache hybridCache = new HybridCache()) {
            hybridCache.set("key", "value");
            assertEquals("value", hybridCache.get("key", String.class));
        }
    }

    @Test
    public void testRedisCacheString() {
        try (HybridCache hybridCache =
                new HybridCache(option -> option.getRedisCacheOptions().setConfiguration("127.0.0.1:6379"))) {
            hybridCache.set("key", "value");
            assertEquals("value", hybridCache.get("key", String.class));
        }
    }

    @Test
    public void testCacheBytes() {
        try (HybridCache hybridCache = new HybridCache()) {
            byte[] bytes = new byte[] {1, 2, 3};
            hybridCache.set("key", bytes);
            assertEquals(bytes, hybridCache.get("key", byte[].class));
        }
    }

    @Test
    public void testCacheBytesRedis() {
        try (HybridCache hybridCache =
                new HybridCache(option -> option.getRedisCacheOptions().setConfiguration("127.0.0.1:6379"))) {
            byte[] bytes = new byte[] {1, 2, 3};
            hybridCache.set("key", bytes);
            assertArrayEquals(bytes, hybridCache.get("key", byte[].class));
        }
    }
}
