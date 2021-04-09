package com.vwmin.k8sawd.web.controller;

import com.vwmin.k8sawd.web.entity.Manager;
import com.vwmin.k8sawd.web.enums.LogKind;
import com.vwmin.k8sawd.web.enums.LogLevel;
import com.vwmin.k8sawd.web.model.Response;
import com.vwmin.k8sawd.web.service.LogService;
import com.vwmin.k8sawd.web.service.ManagerService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;


/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/20 12:37
 */
@Slf4j
@RestController
@RequestMapping("/manager")
public class ManagerController {

    private final ManagerService managerService;

    private final LogService logService;

    public ManagerController(ManagerService managerService, LogService logService) {
        this.managerService = managerService;
        this.logService = logService;
    }

    @GetMapping("/managers")
    public ResponseEntity<Response> list(){
        return Response.success(managerService.list());
    }

    @PostMapping("/manager")
    public ResponseEntity<Response> add(@RequestBody Manager manager){

        managerService.addManager(manager);

        logService.log(LogLevel.NORMAL, LogKind.MANAGER_OPERATE,
                "新管理员账户[%s]创建成功", manager.getName());

        return Response.success("管理员账号添加成功");
    }

    @DeleteMapping("/manager")
    public ResponseEntity<Response> delete(@RequestParam Integer id){

        Manager toDelete = managerService.getById(id);
        managerService.removeById(id);

        logService.log(LogLevel.NORMAL, LogKind.MANAGER_OPERATE,
                "管理员账号[%s(%d)]删除成功", toDelete.getName(), toDelete.getId());

        return Response.success("删除账号成功");
    }

    @PutMapping("/manager/changePassword")
    public ResponseEntity<Response> changePassword(@RequestBody Manager manager){

        managerService.updatePassword(manager);

        logService.log(LogLevel.NORMAL, LogKind.MANAGER_OPERATE,
                "管理员账户[%s(%d)]密码已被修改", manager.getName(), manager.getId());

        return Response.success(manager.getPassword());
    }


    @PutMapping("/manager/token")
    public ResponseEntity<Response> refreshToken(@RequestBody Manager manager){

        String token = managerService.refreshToken(manager);

        logService.log(LogLevel.NORMAL, LogKind.MANAGER_OPERATE,
                "管理员[%s(%d)]Token已刷新", manager.getName(), manager.getId());

        return Response.success(token);
    }



}
