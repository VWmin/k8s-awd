package com.vwmin.k8sawd.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vwmin.k8sawd.web.entity.Flag;
import com.vwmin.k8sawd.web.mapper.FlagMapper;
import com.vwmin.k8sawd.web.service.FlagService;
import org.springframework.stereotype.Service;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 11:44
 */
@Service
public class FlagServiceImpl extends ServiceImpl<FlagMapper, Flag> implements FlagService {
}
