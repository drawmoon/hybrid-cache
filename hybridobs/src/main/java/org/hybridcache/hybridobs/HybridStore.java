package org.hybridcache.hybridobs;

import java.io.*;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.util.Optional;
import java.util.function.Consumer;

import io.minio.MinioClient;

/**
 * 
 */
public class HybridStore {
    private MinioClient minioClient;

    /**
     * 
     */
    public HybridStore() {
        this(new HybridStoreOption());
    }

    /**
     * 
     * @param optionsAction 
     */
    public HybridStore(Consumer<HybridStoreOption> optionsAction) {
        this(Optional.ofNullable(optionsAction)
            .map(x -> {
                HybridStoreOption options = new HybridStoreOption();
                x.accept(options);
                return options;
            }).orElseGet(HybridStoreOption::new));
    }

    /**
     * 
     * @param options
     */
    public HybridStore(HybridStoreOption options) {
        try {
            this.minioClient = MinioClient.builder()
                .endpoint(options.getConfiguration())
                .region(options.getRegion())
                .credentials(options.getAuth(), options.getPassword())
                .build();
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * 
     * @param key
     * @return
     */
    public byte[] get(String key) {
        String realPath = this.getRealPath(key);
        File file = new File(realPath);

        byte[] bytes = new byte[(int) file.length()];
        try (BufferedInputStream inputStream = new BufferedInputStream(Files.newInputStream(file.toPath()))) {
            inputStream.read(bytes);
        } catch (IOException e) {
            // ignore
        }

        return bytes;
    }

    /**
     * 
     * <p>
     * 默认的 contentType 为 application/octet-stream
     * @param filename
     * @param data
     * @return
     */
    public String put(String filename, byte[] data) {
        return this.put(filename, data, "default");
    }

    /**
     * 
     * <p>
     * 默认的 contentType 为 application/octet-stream
     * @param filename
     * @param data
     * @param area
     * @return
     */
    public String put(String filename, byte[] data, String area) {
        return this.put(filename, data, area, "application/octet-stream");
    }

    /**
     * 
     * @param filename
     * @param data
     * @param area
     * @param contentType
     * @return
     * @throws IOException
     * @throws FileNotFoundException
     */
    public String put(String filename, byte[] data, String area, String contentType) {
        String name = this.generateKey(filename, area);

        try {
            String realPath = this.getRealPath(name);
            Path path = Paths.get(realPath);

            // 如果不存在目录，则创建该目录
            Files.createDirectories(path);

            try (BufferedOutputStream out = new BufferedOutputStream(
                    Files.newOutputStream(path, StandardOpenOption.CREATE_NEW))) {
                out.write(data);
            }
        } catch (IOException e) {
            // ignore
        }

        return name;
    }

    /**
     * 
     * @param filename
     * @param target
     * @return
     */
    public String copy(String filename, String target) {
        return null;
    }

    /**
     * 
     * @param key
     */
    public void remove(String key) {
    }

    /**
     * 生成字符串，用于识别所处位置的值。
     * @param filename
     * @return
     */
    private String generateKey(String filename) {
        return generateKey(filename, "default");
    }

    /**
     * 生成字符串，用于识别所处位置的值。
     * @param filename
     * @param area
     * @return
     */
    private String generateKey(String filename, String area) {
        return null;
    }

    private String getRealPath(String path) {
        return path.replace(SERVER_ID, "");
    }
}
