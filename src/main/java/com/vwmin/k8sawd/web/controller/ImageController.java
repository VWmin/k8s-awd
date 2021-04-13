package com.vwmin.k8sawd.web.controller;

import com.vwmin.k8sawd.web.entity.Image;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.service.ImageService;
import com.vwmin.k8sawd.web.service.KubernetesService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/13 20:21
 */
@Slf4j
@RestController
@RequestMapping("/manager")
public class ImageController {
    private final ImageService imageService;
    private final KubernetesService kubernetesService;
    private int runningDemo;

    public ImageController(ImageService imageService, KubernetesService kubernetesService) {
        this.imageService = imageService;
        this.kubernetesService = kubernetesService;
        stopDemo();
    }

    @PostMapping("/image")
    public ResponseEntity<Response> create(@RequestBody Image image) {
        imageService.save(image);
        return Response.success();
    }

    @GetMapping("/images")
    public ResponseEntity<Response> retrieve() {
        return Response.success(imageService.list());
    }

    @PutMapping("/image")
    public ResponseEntity<Response> update(@RequestBody Image image) {
        imageService.updateById(image);
        return Response.success();
    }

    @DeleteMapping("/image")
    public ResponseEntity<Response> delete(@RequestParam int id) {
        imageService.removeById(id);
        return Response.success();
    }

    @GetMapping("/image/demo")
    public ResponseEntity<Response> demo(@RequestParam int id) {
        Image image = imageService.getById(id);
        kubernetesService.demo(image.getName(), image.getPort());
        runningDemo = id;
        return Response.success();
    }

    @DeleteMapping("/image/demo")
    public ResponseEntity<Response> stopDemo() {
        kubernetesService.stopDemo();
        runningDemo = -1;
        return Response.success();
    }

    @GetMapping("/image/runningDemo")
    public ResponseEntity<Response> runningDemo(){
        return Response.success(runningDemo);
    }
}
