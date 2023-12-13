<!--
MIT License

Copyright (c) 2023 drsh

Permission is hereby granted, free of charge, to any person obtaining a copy of this software and associated
documentation files (the “Software”), to deal in the Software without restriction, including without limitation the
rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell copies of the Software, and to
permit persons to whom the Software is furnished to do so, subject to the following conditions:

The above copyright notice and this permission notice shall be included in all copies or substantial portions of the
Software.

THE SOFTWARE IS PROVIDED “AS IS”, WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT LIMITED TO THE
WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR
COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR
OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
-->

hybrid-cache
============

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.drawmoon.hybridcache/hybridcache/badge.svg)](https://maven-badges.herokuapp.com/maven-central/io.github.drawmoon.hybridcache/hybridcache)
[![JavaDoc](https://www.javadoc.io/badge/io.github.drawmoon.hybridcache/hybridcache.svg)](http://www.javadoc.io/doc/io.github.drawmoon.hybridcache/hybridcache)

A hybrid caching library.

Cache
-------

```java
try (HybridCache hybridCache = new HybridCache()) {
    hybridCache.set("key", "value");

    String val = hybridCache.get("key", String.class);
}
```

Using redis, caches to redis by default, or in-memory if redis is not available:

```java
try (HybridCache hybridCache =
        new HybridCache(option -> option.getRedisCacheOptions().setConfiguration("127.0.0.1:6379"))) {
    hybridCache.set("key", "value");

    String val = hybridCache.get("key", String.class);
}
```

If you don't want `hybrid-cache` to automatically select a cache location, you can specify it manually:

```java
hybridCache.set("key", "value", options -> options.setCachePlace(HybridCachePlace.MEMORY));
```

Get the latest release
----------------------

Download from [Maven Central](https://maven-badges.herokuapp.com/maven-central/io.github.drawmoon.hybridcache/hybridcache).

Alternatively, you can pull it from the central Maven repositories:

```xml
<dependency>
    <groupId>io.github.drawmoon.hybridcache</groupId>
    <artifactId>hybridcache</artifactId>
    <version>0.0.1</version>
</dependency>
```
