package org.hybridcache.hybridobs;

/**
 * 表示 {@link HybridStore} 的缓存位置。
 */
public enum HybridStorePlace {
    /**
     * 表示 {@link HybridStore} 应该自动选择缓存位置。
     */
    AUTO,

    /**
     * 表示 {@link HybridStore} 应该将对象存储在本地磁盘中。
     */
    LOCAL,

    /**
     * 表示 {@link HybridStore} 应该将对象分布在多个节点上。
     */
    DISTRIBUTED
}
