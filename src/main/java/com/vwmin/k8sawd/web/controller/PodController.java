package com.vwmin.k8sawd.web.controller;

import com.vwmin.k8sawd.web.model.Response;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.PodList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.dsl.MixedOperation;
import io.fabric8.kubernetes.client.dsl.PodResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/31 12:08
 */
@RestController
@RequestMapping("/manager")
public class PodController {

    private final KubernetesClient kubernetesClient;

    public PodController(KubernetesClient kubernetesClient) {
        this.kubernetesClient = kubernetesClient;
    }

    @GetMapping("/pods")
    public ResponseEntity<Response> pods(){

        return Response.success(kubernetesClient.pods().list());
    }

}
