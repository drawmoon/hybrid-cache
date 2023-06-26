package com.github.drawmoon.hybridcache;

/**
 * 表示 {@link HybridCache} 的缓存位置。
 */
public enum HybridCachePlace {
    /**
     * 表示 {@link HybridCache} 应该自动选择缓存位置。
     */
    AUTO,

    /**
     * 表示 {@link HybridCache} 应该将缓存存储在内存中。
     */
    MEMORY,

    /**
     * 表示 {@link HybridCache} 应该将缓存分布在多个节点上。
     */
    DISTRIBUTED
}
