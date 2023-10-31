/*
 * MIT License
 *
 * Copyright (c) 2023 drsh
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
 * documentation files (the “Software”), to deal in the Software without restriction, including without limitation the
 * rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
 * permit persons to whom the Software is furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
 * Software.
 *
 * THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
 * WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
 * COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
 * OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 */
package io.github.drawmoon.hybridcache;

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
