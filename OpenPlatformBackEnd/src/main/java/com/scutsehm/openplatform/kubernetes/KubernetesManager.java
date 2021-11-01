package com.scutsehm.openplatform.kubernetes;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Base64;


@Slf4j
public class KubernetesManager {

    /**
     * 客户端
     */
    @Getter
    private KubernetesClient client;
    /**
     * k8s配置
     */
    @Getter
    private Config config;


    private static final String HTTPS_PREFIX = "https";
    public KubernetesManager(KubernetesConfig kubernetesConfig) {
        try {
            //使用证书验证
//            final String k8sUrl = kubernetesConfig.getUrl();
//            log.info("k8sUrl : {}", k8sUrl);
//            if(k8sUrl.startsWith(HTTPS_PREFIX)) {
//
//                final String caCrtData = IOUtils.toString(kubernetesConfig.getCaCrt().getInputStream());
//                final String clientKeyData = IOUtils.toString(kubernetesConfig.getClientKey().getInputStream());
//                // 注意：此处必须先用 Base64 对证书内容加密，否则会提示 input null
//                final String clientCrtData = Base64.getEncoder().encodeToString(IOUtils.toByteArray(kubernetesConfig.getClientCrt().getInputStream()));
//                log.info("caCrtFile data: {} ", caCrtData);
//                log.info("clientKeyFile data: {} ", clientKeyData);
//                log.info("clientCrtFile data: {} ", clientCrtData);
//
//                config = new ConfigBuilder().withMasterUrl(k8sUrl)
//                        .withTrustCerts(true)
//                        .withCaCertData(caCrtData)
//                        .withClientCertData(clientCrtData)
//                        .withClientKeyData(clientKeyData)
//                        // 需将 Namespace 初始化为 null
//                        .withNamespace(null)
//                        .build();
//            }else {
//                config = new ConfigBuilder().withMasterUrl(k8sUrl).build();
//            }

            //使用token验证
            config = new ConfigBuilder().withMasterUrl(kubernetesConfig.getUrl())
                    .withTrustCerts(true)
                    .withOauthToken(kubernetesConfig.getToken())
                    .build();
            client = new DefaultKubernetesClient(config);
        }catch (Exception e){
            client = null;
            log.error("初始化 K8sUtils 失败！", e);
        }
    }
}
