# hybrid-cache

A hybrid caching library.

## Example

```java
try (HybridCache hybridCache = new HybridCache()) {
    hybridCache.set("key", "value");

    String val = hybridCache.get("key", String.class);
}
```
