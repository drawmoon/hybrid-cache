package org.hybridcache.hybridobs;

import org.junit.Test;

import static org.junit.Assert.*;

import java.io.File;

public class HybridStoreTest {
    @Test
     public void testStroeText() {
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
     public void testMinioStroeText() {
        HybridStore hybridStore = new HybridStore(x -> {
            x.setStorePlace(HybridStorePlace.DISTRIBUTED);
            x.setConfiguration("http://127.0.0.1:9000");
            x.setAuth("AKIAIOSFODNN7EXAMPLE");
            x.setPassword("wJalrXUtnFEMI/K7MDENG/bPxRfiCYEXAMPLEKEY");
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
}
