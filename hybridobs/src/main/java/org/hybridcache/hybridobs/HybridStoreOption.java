package org.hybridcache.hybridobs;

import lombok.Getter;
import lombok.Setter;

public class HybridStoreOption {
    /**
     * 存储的位置。
     */
    @Getter
    @Setter
    private HybridStorePlace storePlace = HybridStorePlace.AUTO;

    /**
     * 用于连接到 Minio 的配置。
     */
    @Getter
    @Setter
    private String configuration;
    
    /**
     * 表示认证信息。
     */
    @Getter
    @Setter
    private String auth;
    
    /**
     * 表示认证密码。
     */
    @Getter
    @Setter
    private String password;
    
    /**
     * 表示存储区域。
     */
    @Getter
    @Setter
    private String region = "us-east-1";
    
    /**
     * 表示存储桶名称。
     * <p>
     * 在本地存储模式中，存储桶用于当作存储的根目录。
     */
    @Getter
    @Setter
    private String bucket;

    /**
     * 表示键前缀。
     */
    @Getter
    @Setter
    private String keyPrefix;
}
