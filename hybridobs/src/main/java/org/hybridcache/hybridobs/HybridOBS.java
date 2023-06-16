package org.hybridcache.hybridobs;

/**
 * 
 */
public class HybridOBS {
    /**
     * 
     */
    public HybridOBS() {
        this(new HybridStoreOption());
    }

    /**
     * 
     * @param options
     */
    public HybridOBS(HybridStoreOption options) {

    }

    /**
     * 获取文件
     */
    public byte[] get(String key) {
        return null;
    }

    /**
     * 推送文件
     *
     * <p>
     * 默认的 contentType 为 application/octet-stream
     */
    public String put(String filename, byte[] data) {
        return null;
    }

    /**
     * 推送文件
     *
     * <p>
     * 默认的 contentType 为 application/octet-stream
     */
    public String put(String filename, byte[] data, String area) {
        return null;
    }

    /**
     * 推送文件
     */
    public String put(String filename, byte[] data, String area, String contentType) {
        return null;
    }

    /**
     * 拷贝文件
     */
    public String copy(String filename, String target) {
        return null;
    }

    /**
     * 移除文件
     */
    public void remove(String name) {
    }
}
