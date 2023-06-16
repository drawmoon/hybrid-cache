package org.hybridcache.hybridobs;

import lombok.Getter;
import lombok.Setter;

/**
 * 
 */
public class HybridStoreOption {
    /**
     * 用于连接到 Redis 的配置。
     */
    @Getter
    @Setter
    private String configuration;
    
    /**
     * 
     */
    @Getter
    @Setter
    private String auth;
    
    /**
     * 
     */
    @Getter
    @Setter
    private String password;
    
    /**
     * 
     */
    @Getter
    @Setter
    private String region;
    
    /**
     * 
     */
    @Getter
    @Setter
    private String bucket;
}