package com.github.drawmoon.hybridcache;

import org.junit.Test;

import static org.junit.Assert.*;

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
        try (HybridCache hybridCache = new HybridCache(option -> option.getRedisCacheOptions().setConfiguration("127.0.0.1:6379"))) {
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
        try (HybridCache hybridCache = new HybridCache(option -> option.getRedisCacheOptions().setConfiguration("127.0.0.1:6379"))) {
            byte[] bytes = new byte[] {1, 2, 3};
            hybridCache.set("key", bytes);
            assertArrayEquals(bytes, hybridCache.get("key", byte[].class));
        }
     }
}
