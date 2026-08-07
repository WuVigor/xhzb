package com.xhzb.oss.properties;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.ToString;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

/**
 * @ClassName AliOssConfigProperties.java
 * 
 */
@Setter
@Getter
@NoArgsConstructor
@ToString
@Configuration
@ConfigurationProperties(prefix = "oss")
public class AliOssConfigProperties {

    /** 是否启用阿里云 OSS；关闭时文件服务自动使用本地存储。 */
    private boolean enabled;

    /**
     * 域名站点
     */
    private String endpoint ;

    /**
     * 秘钥Id
     */
    private String accessKeyId ;

    /**
     * 秘钥
     */
    private String accessKeySecret ;

    /**
     * 桶名称
     */
    private String bucketName ;

}

