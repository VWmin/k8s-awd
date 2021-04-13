package com.vwmin.k8sawd.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vwmin.k8sawd.web.entity.Bulletin;
import com.vwmin.k8sawd.web.mapper.BulletinMapper;
import com.vwmin.k8sawd.web.service.BulletinService;
import org.springframework.stereotype.Service;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/13 18:35
 */
@Service
public class BulletinServiceImpl extends ServiceImpl<BulletinMapper, Bulletin> implements BulletinService {
    @Override
    public void removeAll() {
        remove(new QueryWrapper<>());
    }
}
