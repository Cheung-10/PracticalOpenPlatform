package com.scutsehm.openplatform.kubernetes;

import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties("kubernetes")
class KubernetesConfig {
    /**
     * master节点url
     */
    private String url;

    /**
     * crt
     */
    @Value("client-crt")
    private Resource clientCrt;

    /**
     * client-key
     */
    @Value("client-key")
    private Resource clientKey;

    /**
     * ca-crt
     */
    @Value("ca-crt")
    private Resource caCrt;

    /**
     * token
     */
    private String token;

    @Bean
    public KubernetesManager kubernetesManager(){
        return new KubernetesManager(this);
    }
}
