package com.github.drawmoon.hybridcache;

import java.time.Duration;

import lombok.Getter;
import lombok.Setter;
import com.github.drawmoon.hybridcache.hybridobs.HybridStoreOption;

public class HybridCacheOptions {
    /**
     * 获取或设置缓冲区的最大尺寸。
     */
    @Getter
    @Setter
    private long sizeLimit = 10_000;

    /**
     * 获取或设置过期物品连续扫描之间的最小时间长度。
     */
    @Getter
    @Setter
    private Duration expirationScanFrequency = Duration.ofMinutes(10);

    /**
     * Redis 缓存的配置选项。
     */
    @Getter
    @Setter
    private RedisCacheOptions redisCacheOptions = new RedisCacheOptions();

    /**
     * 
     */
    @Getter
    @Setter
    private HybridStoreOption hybridStoreOption = new HybridStoreOption();
}
