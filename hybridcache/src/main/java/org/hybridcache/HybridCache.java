package org.hybridcache;

import java.nio.charset.StandardCharsets;
import java.util.Optional;
import java.util.concurrent.CompletableFuture;
import java.util.function.Consumer;

import org.hybridcache.utils.TypeUtils;

import com.github.benmanes.caffeine.cache.AsyncCache;
import com.github.benmanes.caffeine.cache.Caffeine;

import io.lettuce.core.RedisClient;
import io.lettuce.core.RedisFuture;
import io.lettuce.core.SetArgs;
import io.lettuce.core.api.StatefulRedisConnection;
import io.lettuce.core.api.async.RedisAsyncCommands;
import io.lettuce.core.codec.ByteArrayCodec;

/**
 * 一个混合缓存，它将数据存储在内存、磁盘、分布式对象存储和分布式缓存上。
 */
public class HybridCache implements AutoCloseable {
    // 用于在内存中存储数据的异步缓存对象
    private final AsyncCache<String, byte[]> memoryCache;

    // Redis 客户端。
    private RedisClient redisClient;
    // Redis 连接。
    private StatefulRedisConnection<byte[], byte[]> statefulRedisConnection;
    // Redis 是可用的。
    private boolean redisAvailabled = false;

    /**
     * 创建一个新的混合缓存实例。
     */
    public HybridCache() {
        this(new HybridCacheOptions());
    }

    /**
     * 创建一个新的混合缓存实例。
     * @param optionsAction 用于配置混合缓存。
     */
    public HybridCache(Consumer<HybridCacheOptions> optionsAction) {
        this(Optional.ofNullable(optionsAction).map(x -> {
            HybridCacheOptions options = new HybridCacheOptions();
            if (optionsAction != null) {
                optionsAction.accept(options);
            }

            return options;
        }).get());
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

        try {
            RedisCacheOptions redisCacheOptions = options.getRedisCacheOptions();
            this.redisClient = RedisClient.create("redis://" + redisCacheOptions.getConfiguration());
            this.statefulRedisConnection = redisClient.connect(new ByteArrayCodec());
            this.redisAvailabled = this.statefulRedisConnection.isOpen();
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * 获取一个具有给定键的值。
     * @param key 一个字符串，用于识别所处位置的值。
     * @return 所处位置的值或 {@code null}。
     */
    public byte[] get(String key) {
        CompletableFuture<byte[]> completableFuture = this.memoryCache.getIfPresent(key);
        if (completableFuture != null) {
            return completableFuture.join();
        }

        if (redisAvailabled) {
            RedisFuture<byte[]> redisFuture = this.statefulRedisConnection.async().get(key.getBytes(StandardCharsets.UTF_8));
            if (redisFuture != null) {
                try {
                    return redisFuture.get();
                } catch (Exception e) {
                    // ignore
                }
            }
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
     * @param entryOptionsAction 值的缓存选项。
     */
    public void set(String key, Object value, Consumer<HybridCacheEntryOptions> entryOptionsAction) {
        HybridCacheEntryOptions entryOptions = new HybridCacheEntryOptions();
        if (entryOptionsAction != null) {
            entryOptionsAction.accept(entryOptions);
        }

        this.set(key, value, entryOptions);
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

        if (place.equals(HybridCachePlace.DISTRIBUTED)) {
            if (redisAvailabled) {
                RedisAsyncCommands<byte[], byte[]> asyncCommands = this.statefulRedisConnection.async();
                SetArgs setArgs = SetArgs.Builder
                    .ex(entryOptions.getAbsoluteExpiration());
                asyncCommands.set(key.getBytes(StandardCharsets.UTF_8), bytes, setArgs);
                return;
            }
        }

        this.memoryCache.put(key, CompletableFuture.completedFuture(bytes));
    }

    /**
     * 根据键值刷新缓存中的一个值，重新设置其滑动过期时间（如果有的话）。
     * @param key 一个字符串，用于识别所处位置的值。
     */
    public void refresh(String key) {
        if (this.memoryCache.synchronous().getIfPresent(key) != null) {
            return;
        }

        if (redisAvailabled) {
            RedisAsyncCommands<byte[], byte[]> asyncCommands = this.statefulRedisConnection.async();
            asyncCommands.expire(key.getBytes(StandardCharsets.UTF_8), 60);
        }
    }

    /**
     * 根据键值移除缓存中的一个值（如果有的话）。
     * @param key 一个字符串，用于识别所处位置的值。
     */
    public void remove(String key) {
        this.memoryCache.synchronous().invalidate(key);

        if (redisAvailabled) {
            RedisAsyncCommands<byte[], byte[]> asyncCommands = this.statefulRedisConnection.async();
            asyncCommands.del(key.getBytes(StandardCharsets.UTF_8));
        }
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
                : clazz.cast(TypeUtils.fromBytes(bytes, clazz));
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

        if (value == null || options.getPriority().equals(CacheItemPriority.HIGH)) {
            return HybridCachePlace.MEMORY;
        }

        return HybridCachePlace.DISTRIBUTED;
    }

    // Closes this resource, relinquishing any underlying resources.
    @Override
    public void close() {
        if (this.statefulRedisConnection != null && redisAvailabled) {
            this.statefulRedisConnection.close();
        }
        if (this.redisClient != null) {
            this.redisClient.shutdown();
        }
    }
}
