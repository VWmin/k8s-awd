package com.vwmin.k8sawd.web.conf;

import cn.hutool.core.util.StrUtil;
import io.fabric8.kubernetes.api.model.Namespace;
import io.fabric8.kubernetes.api.model.NamespaceBuilder;
import io.fabric8.kubernetes.api.model.NamespaceList;
import io.fabric8.kubernetes.client.*;
import lombok.Getter;
import lombok.Setter;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;
import java.util.stream.Stream;

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

    private static final String DEFAULT_NAMESPACE = "awd";

    private String masterUrl;
    private String caCertFile;
    private String clientCertFile;
    private String clientKeyFile;
    private String namespace;

    @Bean
    public KubernetesClient kubernetesClient() {

        if (StrUtil.isEmpty(namespace)){
            namespace = DEFAULT_NAMESPACE;
        }

        Config config = new ConfigBuilder()
                .withMasterUrl(masterUrl)
                .withCaCertFile(caCertFile)
                .withClientCertFile(clientCertFile)
                .withClientKeyFile(clientKeyFile)
                .build();
        config.setTrustCerts(true);
        DefaultKubernetesClient client = new DefaultKubernetesClient(config);


        // 检查命名空间
        if (client.namespaces().list().getItems().stream().noneMatch(e -> namespace.equals(e.getMetadata().getName()))) {
            client.namespaces().create(new NamespaceBuilder()
                    .withNewMetadata()
                    .withName(namespace)
//                    .addToLabels("a", "label")
                    .endMetadata()
                    .build());
        }


        return client.inNamespace(namespace);
    }
}
