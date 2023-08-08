package io.github.drawmoon.hybridcache;

import static org.junit.jupiter.api.Assertions.*;

import java.io.File;
import org.junit.jupiter.api.Test;

public class HybridStoreTest {
    @Test
    public void testStoreText() {
        HybridStore hybridStore = new HybridStore(x -> {
            x.setKeyPrefix("disk:");
        });

        String key = hybridStore.put("tmp.txt", "abc".getBytes());
        assertTrue(key.startsWith("disk:"));
        assertTrue(new File(key.substring(5)).exists());

        byte[] bytes = hybridStore.get(key);
        assertEquals("abc", new String(bytes));

        hybridStore.remove(key);
        assertFalse(new File(key.substring(5)).exists());
    }

    @Test
    public void testStoreEmptyText() {
        HybridStore hybridStore = new HybridStore(x -> {
            x.setKeyPrefix("disk:");
        });

        String key = hybridStore.put("tmp.txt", "".getBytes());
        assertTrue(key.startsWith("disk:"));
        assertTrue(new File(key.substring(5)).exists());

        byte[] bytes = hybridStore.get(key);
        assertEquals("", new String(bytes));

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

        String key = hybridStore.put("tmp.txt", "abc".getBytes());
        assertTrue(key.startsWith("minio:"));

        byte[] bytes = hybridStore.get(key);
        assertEquals("abc", new String(bytes));

        hybridStore.remove(key);
        assertNull(hybridStore.get(key));
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

        String key = hybridStore.put("tmp.txt", "".getBytes());
        assertTrue(key.startsWith("minio:"));

        byte[] bytes = hybridStore.get(key);
        assertEquals("", new String(bytes));

        hybridStore.remove(key);
        assertNull(hybridStore.get(key));
    }
}
