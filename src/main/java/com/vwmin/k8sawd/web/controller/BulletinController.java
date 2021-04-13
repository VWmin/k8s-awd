package com.vwmin.k8sawd.web.controller;

import com.vwmin.k8sawd.web.aop.ExpectedStatus;
import com.vwmin.k8sawd.web.entity.Bulletin;
import com.vwmin.k8sawd.web.enums.CompetitionStatus;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.service.BulletinService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/13 18:37
 */
@RestController
@RequestMapping("/manager")
public class BulletinController {

    private final BulletinService bulletinService;


    public BulletinController(BulletinService bulletinService) {
        this.bulletinService = bulletinService;
    }

    @PostMapping("/bulletin")
    @ExpectedStatus(expected = {CompetitionStatus.SET, CompetitionStatus.RUNNING, CompetitionStatus.FINISHED})
    public ResponseEntity<Response> create(@RequestBody Bulletin bulletin) {
        bulletinService.save(bulletin);
        return Response.success();
    }

    @GetMapping("/bulletins")
    @ExpectedStatus(expected = {CompetitionStatus.SET, CompetitionStatus.RUNNING, CompetitionStatus.FINISHED})
    public ResponseEntity<Response> retrieve() {
        return Response.success(bulletinService.list());
    }

    @PutMapping("/bulletin")
    @ExpectedStatus(expected = {CompetitionStatus.SET, CompetitionStatus.RUNNING, CompetitionStatus.FINISHED})
    public ResponseEntity<Response> update(@RequestBody Bulletin bulletin) {
        bulletinService.updateById(bulletin);
        return Response.success();
    }

    @DeleteMapping("/bulletin")
    @ExpectedStatus(expected = {CompetitionStatus.SET, CompetitionStatus.RUNNING, CompetitionStatus.FINISHED})
    public ResponseEntity<Response> delete(@RequestParam int id) {
        bulletinService.removeById(id);
        return Response.success();
    }

}
