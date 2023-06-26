package com.github.drawmoon.hybridcache;

import lombok.Getter;
import lombok.Setter;

public class HybridCacheEntryOptions {
    /**
     * 缓存项的绝对到期日。
     */
    @Getter
    @Setter
    private long absoluteExpiration = 30;

    /**
     * 缓存项的优先级。
     */
    @Getter
    @Setter
    private CacheItemPriority priority = CacheItemPriority.NORMAL;

    /**
     * 缓存项的位置。
     */
    @Getter
    @Setter
    private HybridCachePlace cachePlace = HybridCachePlace.AUTO;
}
