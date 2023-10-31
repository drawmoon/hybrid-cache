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
