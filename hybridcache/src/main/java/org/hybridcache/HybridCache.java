package org.hybridcache;

import java.nio.charset.StandardCharsets;
import java.util.concurrent.CompletableFuture;

import org.hybridcache.utils.TypeUtils;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;

/**
 * 一个混合缓存，它将数据存储在内存、磁盘、分布式对象存储和分布式缓存上。
 */
public class HybridCache {
    /**
     * 用于在内存中存储数据的异步缓存对象。
     */
    private final AsyncCache<String, byte[]> memoryCache;

    /**
     * 创建一个新的混合缓存实例。
     */
    public HybridCache() {
        this(new HybridCacheOptions());
    }

    /**
     * 创建一个新的混合缓存实例。
     * @param options 用于配置混合缓存。
     */
    public HybridCache(HybridCacheOptions options) {
        this.memoryCache = Caffeine.newBuilder()
            .maximumSize(options.getSizeLimit())
            .expireAfterWrite(options.getExpirationScanFrequency())
            .buildAsync();
    }

    /**
     * 获取一个具有给定键的值。
     * @param key 一个字符串，用于识别所处位置的值。
     * @return 所处位置的值或 {@code null}。
     */
    public byte[] get(String key) {
        CompletableFuture<byte[]> future = this.memoryCache.getIfPresent(key);
        if (future != null) {
            return future.join();
        }

        return null;
    }

    /**
     * 获取一个具有给定键的值。
     * @param key 一个字符串，用于识别所处位置的值。
     * @return 所处位置的值或 {@code null}。
     */
    public <T> T get(String key, Class<T> clazz) {
        byte[] bytes = get(key);
        return bytes != null ? this.convertFromBytes(bytes, clazz) : null;
    }

    /**
     * 用给定的键设置一个值。
     * @param key 一个字符串，用于识别所处位置的值。
     * @param value 缓存中要设置的值。
     */
    public void set(String key, Object value) {
        this.set(key, value, new HybridCacheEntryOptions());
    }

    /**
     * 用给定的键设置一个值。
     * @param key 一个字符串，用于识别所处位置的值。
     * @param value 缓存中要设置的值。
     * @param entryOptions 值的缓存选项。
     */
    public void set(String key, Object value, HybridCacheEntryOptions entryOptions) {
        byte[] bytes = convertToBytes(value);

        HybridCachePlace place = whereToStore(value, entryOptions);
        assert !place.equals(HybridCachePlace.AUTO);

        if (place.equals(HybridCachePlace.MEMORY)) {
            memoryCache.put(key, CompletableFuture.completedFuture(bytes));
            return;
        }

        // TODO: Method not implemented.
    }

    /**
     * 根据键值刷新缓存中的一个值，重新设置其滑动过期时间（如果有的话）。
     * @param key 一个字符串，用于识别所处位置的值。
     */
    public void refresh(String key) {
        this.memoryCache.synchronous().getIfPresent(key);
    }

    /**
     * 根据键值移除缓存中的一个值（如果有的话）。
     * @param key 一个字符串，用于识别所处位置的值。
     */
    public void remove(String key) {
        this.memoryCache.synchronous().invalidate(key);
    }

    /**
     * 将字节数组转换为对象。
     * @param bytes 待转换的字节数组。
     * @return 转换后的对象或 {@code null}。
     */
    private <T> T convertFromBytes(byte[] bytes, Class<T> clazz) {
        try {
            return clazz.isAssignableFrom(String.class)
                ? clazz.cast(new String(bytes, StandardCharsets.UTF_8))
                : clazz.cast(TypeUtils.fromBytes(bytes));
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 将对象转换为字节数组。
     * @param value 待转换的对象。
     * @return 转换后的字节数组。
     */
    private byte[] convertToBytes(Object value) {
        try {
            return value instanceof String
                ? ((String) value).getBytes(StandardCharsets.UTF_8)
                : TypeUtils.toBytes(value);
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 根据给定的值和选项确定缓存数据的存储位置。
     * @param value 缓存中要设置的值。
     * @param options 值的缓存选项。
     * @return 缓存的位置。
     */
    private HybridCachePlace whereToStore(Object value, HybridCacheEntryOptions options) {
        if (!options.getCachePlace().equals(HybridCachePlace.AUTO)) {
            return options.getCachePlace();
        }

        if (value == null) {
            return HybridCachePlace.MEMORY;
        }

        return options.getPriority().equals(CacheItemPriority.HIGH)
            ? HybridCachePlace.MEMORY
            : HybridCachePlace.DISTRIBUTED;
    }
}
