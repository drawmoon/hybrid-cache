package org.hybridcache;

import lombok.Getter;
import lombok.Setter;

/**
 * Redis 缓存选项。
 */
public class RedisCacheOptions {
    /**
     * 用于连接到 Redis 的配置。
     */
    @Getter
    @Setter
    private String configuration;

    /**
     * Redis 实例名称。
     */
    @Getter
    @Setter
    private String instanceName;
}
