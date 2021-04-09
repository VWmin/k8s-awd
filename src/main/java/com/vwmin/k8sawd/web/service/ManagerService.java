package com.vwmin.k8sawd.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vwmin.k8sawd.web.entity.Manager;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/9 13:14
 */
public interface ManagerService extends IService<Manager> {
    boolean existByName(String managerName);

    boolean existByToken(String token);

    void addManager(Manager manager);

    void updatePassword(Manager manager);

    String refreshToken(Manager manager);

    boolean login(Manager manager);

    void clearToken(String token);
}
