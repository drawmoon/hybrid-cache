package com.github.drawmoon.hybridcache;

/**
 * 指定在内存压力触发的清理中如何优先保存项目。
 */
public enum CacheItemPriority {
    /**
     * 存储在分布式缓存中。
     */
    LOW,

    /**
     * 执行 {@link HybridCachePlace} 策略。
     */
    NORMAL,

    /**
     * 存储在内存缓存中。
     */
    HIGH,

    /**
     * 存储在磁盘（本地或分布式对象存储，取决于 HybridOBS）和分布式缓存（如果可用）中。
     */
    NEVER_REMOVE,
}
