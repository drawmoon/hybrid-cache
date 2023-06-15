package org.hybridcache;

import org.junit.Test;

import static org.junit.Assert.*;

public class HybridCacheTest {
    @Test
    public void testCacheString() {
        HybridCache hybridCache = new HybridCache();
        hybridCache.set("key", "value");

        assertEquals("value", hybridCache.get("key", String.class));
    }
}
