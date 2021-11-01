package com.scutsehm.openplatform.config;

import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;

/**
 * 文件路径相关配置
 */
@Getter
@Configuration
//@ConfigurationProperties(prefix = "open-platform.filepath")
public class FilePathConfig {

    /**
     * base路径
     */
    @Value("${open-platform.filepath.base}")
    private String base;

    /**
     * 私有空间路径
     */
    @Value("${open-platform.filepath.private-space}")
    private String privateSpace;

    /**
     * 数据空间路径
     */
    @Value("${open-platform.filepath.data-space}")
    private String dataSpace;

    /**
     * 共享空间路径
     */
    @Value("${open-platform.filepath.share-space}")
    private String shareSpace;

    /**
     * 训练模型空间路径
     */
    @Value("${open-platform.filepath.process-model-space}")
    private String processModelSpace;

    /**
     * 处理模型空间路径
     */
    @Value("${open-platform.filepath.train-model-space}")
    private String trainModelSpace;
}
