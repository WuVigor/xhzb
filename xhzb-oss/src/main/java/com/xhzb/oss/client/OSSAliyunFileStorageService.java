package com.xhzb.oss.client;

import com.aliyun.oss.OSS;
import com.aliyun.oss.model.DeleteObjectsRequest;
import com.aliyun.oss.model.PutObjectRequest;
import com.xhzb.common.config.RuoYiConfig;
import com.xhzb.oss.properties.AliOssConfigProperties;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * 文件存储服务。
 * <p>oss.enabled=true 时使用阿里云 OSS；默认使用若依 profile 目录下的本地存储。</p>
 */
@Component
public class OSSAliyunFileStorageService {

    @Autowired(required = false)
    private OSS ossClient;

    @Autowired
    private AliOssConfigProperties properties;

    public InputStream download(String pathUrl) {
        try {
            if (useOss()) {
                String prefix = "https://" + properties.getBucketName() + "." + properties.getEndpoint() + "/";
                String key = pathUrl.replace(prefix, "");
                return ossClient.getObject(properties.getBucketName(), key).getObjectContent();
            }
            return Files.newInputStream(resolveLocalPath(pathUrl));
        } catch (Exception e) {
            throw new IllegalStateException("文件读取失败: " + pathUrl, e);
        }
    }

    public String store(String objectName, InputStream inputStream) {
        if (inputStream == null) {
            throw new IllegalArgumentException("上传文件流不能为空");
        }
        try {
            if (useOss()) {
                ossClient.putObject(new PutObjectRequest(properties.getBucketName(), objectName, inputStream));
                return "https://" + properties.getBucketName() + "." + properties.getEndpoint() + "/" + objectName;
            }

            Path uploadDirectory = Paths.get(RuoYiConfig.getUploadPath()).toAbsolutePath().normalize();
            Files.createDirectories(uploadDirectory);
            Path target = uploadDirectory.resolve(objectName).normalize();
            if (!target.startsWith(uploadDirectory)) {
                throw new IllegalArgumentException("非法文件名: " + objectName);
            }
            Files.copy(inputStream, target, StandardCopyOption.REPLACE_EXISTING);
            return "/profile/upload/" + objectName.replace('\\', '/');
        } catch (IOException e) {
            throw new IllegalStateException("文件保存失败: " + objectName, e);
        }
    }

    public void delete(String pathUrl) {
        try {
            if (useOss()) {
                String prefix = "https://" + properties.getBucketName() + "." + properties.getEndpoint() + "/";
                String key = pathUrl.replace(prefix, "");
                ossClient.deleteObjects(new DeleteObjectsRequest(properties.getBucketName()).withKeys(List.of(key)));
                return;
            }
            Files.deleteIfExists(resolveLocalPath(pathUrl));
        } catch (Exception e) {
            throw new IllegalStateException("文件删除失败: " + pathUrl, e);
        }
    }

    private boolean useOss() {
        return properties.isEnabled() && ossClient != null;
    }

    private Path resolveLocalPath(String pathUrl) {
        String path = pathUrl;
        if (path.startsWith("http://") || path.startsWith("https://")) {
            path = URI.create(path).getPath();
        }
        path = path.replace('\\', '/');
        if (path.startsWith("/profile/")) {
            path = path.substring("/profile/".length());
        } else {
            path = path.replaceFirst("^/+", "");
        }

        Path profileDirectory = Paths.get(RuoYiConfig.getProfile()).toAbsolutePath().normalize();
        Path target = profileDirectory.resolve(path).normalize();
        if (!target.startsWith(profileDirectory)) {
            throw new IllegalArgumentException("非法文件路径: " + pathUrl);
        }
        return target;
    }
}
