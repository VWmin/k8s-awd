package com.vwmin.k8sawd.web.conf;

import io.fabric8.kubernetes.client.Config;
import io.fabric8.kubernetes.client.ConfigBuilder;
import io.fabric8.kubernetes.client.DefaultKubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClient;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/31 12:04
 */
@Setter
@Getter
@Configuration
@EnableConfigurationProperties(KubernetesConfig.class)
@ConfigurationProperties(prefix = "k8s-awd")
public class KubernetesConfig {
    private String masterUrl;
    private String caCertFile;
    private String clientCertFile;
    private String clientKeyFile;

    @Bean
    public KubernetesClient kubernetesClient() {
        Config config = new ConfigBuilder()
                .withMasterUrl(masterUrl)
                .withCaCertFile(caCertFile)
                .withClientCertFile(clientCertFile)
                .withClientKeyFile(clientKeyFile)
                .build();
        config.setTrustCerts(true);
        return new DefaultKubernetesClient(config);
    }
}
