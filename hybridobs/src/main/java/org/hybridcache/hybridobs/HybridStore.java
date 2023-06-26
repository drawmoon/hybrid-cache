package org.hybridcache.hybridobs;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardOpenOption;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Optional;
import java.util.UUID;
import java.util.function.Consumer;

import org.apache.commons.lang3.StringUtils;

import io.minio.BucketExistsArgs;
import io.minio.GetObjectArgs;
import io.minio.GetObjectResponse;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import io.minio.RemoveObjectArgs;

/**
 * 一个混合存储，它将数据存储在磁盘、分布式对象存储上。
 */
public class HybridStore {
    // 用于混合存储的配置选项。
    private HybridStoreOption options;

    // Minio 客户端。
    private MinioClient minioClient;

    /**
     * 创建一个新的混合存储实例。
     */
    public HybridStore() {
        this(new HybridStoreOption());
    }

    /**
     * 创建一个新的混合存储实例。
     * @param optionsAction 用于配置混合存储。
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
     * 创建一个新的混合存储实例。
     * @param options 用于配置混合存储。
     */
    public HybridStore(HybridStoreOption options) {
        this.options = options;
        if (!options.getStorePlace().equals(HybridStorePlace.LOCAL)) {
            try {
                this.minioClient = MinioClient.builder()
                    .endpoint(options.getConfiguration())
                    .region(options.getRegion())
                    .credentials(options.getAuth(), options.getPassword())
                    .build();

                if (!this.minioClient.bucketExists(BucketExistsArgs.builder()
                    .bucket(options.getBucket())
                    .region(options.getRegion())
                    .build())) {
                    this.options.setStorePlace(HybridStorePlace.LOCAL);
                }

                this.options.setStorePlace(HybridStorePlace.DISTRIBUTED);
            } catch (Exception e) {
                this.options.setStorePlace(HybridStorePlace.LOCAL);
            }
        }
    }

    /**
     * 获取一个具有给定键的值。
     * @param key 一个字符串，用于识别所处位置的值。
     * @return 所处位置的值或 {@code null}。
     */
    public byte[] get(String key) {
        String realPath = this.getRealPath(key);
        try (ByteArrayOutputStream outputStream = new ByteArrayOutputStream()) {
            InputStream inputStream;
            int byteSize = 0;
            if (this.options.getStorePlace().equals(HybridStorePlace.LOCAL)) {
                inputStream = new BufferedInputStream(Files.newInputStream(Paths.get(realPath)));
                byteSize = inputStream.available();
            } else {
                GetObjectResponse response = minioClient.getObject(GetObjectArgs.builder()
                    .bucket(this.options.getBucket())
                    .region(this.options.getRegion())
                    .object(realPath)
                    .build());

                inputStream = response;
                byteSize = Integer.parseInt(response.headers().get("Content-Length"));
            }

            byte[] bytes = new byte[byteSize];

            int length;
            while ((length = inputStream.read(bytes)) != -1) {
                outputStream.write(bytes, 0, length);
            }

            return outputStream.toByteArray();
        } catch (Exception e) {
            // ignore
        }

        return null;
    }

    /**
     * 将数据保存到磁盘或上传至一个分布式对象存储。
     * <p>
     * 默认的 contentType 为 application/octet-stream。
     * @param filename 文件名称。
     * @param data 文件的数据。
     * @return 返回一个字符串，用于识别所处位置的值。
     */
    public String put(String filename, byte[] data) {
        return this.put(filename, data, "default");
    }

    /**
     * 将数据保存到磁盘或上传至一个分布式对象存储。
     * <p>
     * 默认的 contentType 为 application/octet-stream
     * @param filename 文件名称。
     * @param data 文件的数据。
     * @param area 用于表示一个目录的分组。
     * @return 返回一个字符串，用于识别所处位置的值。
     */
    public String put(String filename, byte[] data, String area) {
        return this.put(filename, data, area, "application/octet-stream");
    }

    /**
     * 将数据保存到磁盘或上传至一个分布式对象存储。
     * @param filename 文件名称。
     * @param data 文件的数据。
     * @param area 用于表示一个目录的分组。
     * @param contentType 文件的文本类型。
     * @return 返回一个字符串，用于识别所处位置的值。
     */
    public String put(String filename, byte[] data, String area, String contentType) {
        String name = this.generateKey(filename, area);

        try {
            String realPath = this.getRealPath(name);
            if (this.options.getStorePlace().equals(HybridStorePlace.LOCAL)) {
                Path path = Paths.get(realPath);

                // 如果不存在目录，则创建该目录
                Files.createDirectories(path.getParent());

                try (BufferedOutputStream outputStream = new BufferedOutputStream(
                    Files.newOutputStream(path, StandardOpenOption.CREATE_NEW))) {
                    outputStream.write(data);
                }
            } else {
                try (InputStream inputStream = new ByteArrayInputStream(data)) {
                    this.minioClient.putObject(PutObjectArgs.builder()
                            .bucket(this.options.getBucket())
                            .region(this.options.getRegion())
                            .object(realPath)
                            .stream(inputStream, inputStream.available(), -1)
                            .contentType(contentType)
                            .build()
                    );
                }
            }
        } catch (Exception e) {
            // ignore
        }

        return name;
    }

    /**
     * 将数据从磁盘或分布式存储中复制并保存到新的位置。
     * @param filename 文件名称。
     * @param target 用于即将拷贝的目标位置的键。
     * @return 返回一个字符串，用于识别所处位置的值。
     */
    public String copy(String filename, String target) {
        byte[] bytes = this.get(target);
        return this.put(filename, bytes);
    }

    /**
     * 将数据从磁盘或分布式存储中移除。
     * @param key 一个字符串，用于识别所处位置的值。
     */
    public void remove(String key) {
        String realPath = this.getRealPath(key);
        try {
            if (this.options.getStorePlace().equals(HybridStorePlace.LOCAL)) {
                Files.delete(Paths.get(realPath));
            } else {
                this.minioClient.removeObject(RemoveObjectArgs.builder()
                    .bucket(this.options.getBucket())
                    .region(this.options.getRegion())
                    .object(realPath)
                    .build());
            }
        } catch (Exception e) {
            // ignore
        }
    }

    /**
     * 生成一个字符串，用于识别所处位置的值。
     * @param filename 文件名称。
     * @param area 用于表示一个目录的分组。
     * @return 返回一个字符串，用于识别所处位置的值。
     */
    private String generateKey(String filename, String area) {
        SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");

        // 如果没有指定目录，则获取系统的临时目录
        String baseDir = this.options.getStorePlace().equals(HybridStorePlace.LOCAL)
            ? this.options.getBucket()
            : "";
        if (StringUtils.isBlank(baseDir)) {
            baseDir = System.getProperty("java.io.tmpdir");
        }

        String key = Paths
            .get(baseDir, area, format.format(new Date()), UUID.randomUUID().toString(), filename)
            .toString();
        if (StringUtils.isNotBlank(this.options.getKeyPrefix())) {
            return this.options.getKeyPrefix() + key;
        }

        return key;
    }

    /**
     * 获取磁盘或分布式存储中真实的位置。
     * @param key 一个字符串，用于识别所处位置的值。
     * @return 返回磁盘或分布式存储中真实的位置。
     */
    private String getRealPath(String key) {
        if (StringUtils.isNotBlank(this.options.getKeyPrefix())) {
            return key.replace(this.options.getKeyPrefix(), "");
        }

        return key;
    }
}
