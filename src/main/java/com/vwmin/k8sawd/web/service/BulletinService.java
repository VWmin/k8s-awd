package com.vwmin.k8sawd.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vwmin.k8sawd.web.entity.Bulletin;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/13 18:34
 */
public interface BulletinService extends IService<Bulletin> {
    void removeAll();
}
