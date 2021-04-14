package com.vwmin.k8sawd.web.controller;

import com.vwmin.k8sawd.web.entity.Image;
import com.vwmin.k8sawd.web.enums.LogKind;
import com.vwmin.k8sawd.web.enums.LogLevel;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.service.ImageService;
import com.vwmin.k8sawd.web.service.KubernetesService;
import com.vwmin.k8sawd.web.service.LogService;
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
    private final LogService logService;
    private final KubernetesService kubernetesService;
    private int runningDemo;

    public ImageController(ImageService imageService, LogService logService, KubernetesService kubernetesService) {
        this.imageService = imageService;
        this.logService = logService;
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
        kubernetesService.demo(image);
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

    @GetMapping("/image/current")
    public ResponseEntity<Response> currentImage(){
        return Response.success(imageService.image().getId());
    }

    @GetMapping("/image/select")
    public ResponseEntity<Response> selectImage(@RequestParam int id){
        Image image = imageService.getById(id);
        imageService.setImage(image);
        logService.log(LogLevel.IMPORTANT, LogKind.MANAGER_OPERATE,
                "已选择镜像[ %s ], 将在下一次比赛启动时生效", image.getName());
        return Response.success("已选择镜像[ " + image.getName() + " ], 将在下一次比赛启动时生效");
    }

    @GetMapping("/image/reset")
    public ResponseEntity<Response> reset2Default(){
        imageService.reset();
        return Response.success("已选择默认镜像, 将在下一次比赛启动时生效");
    }
}
