package com.vwmin.k8sawd.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vwmin.k8sawd.web.entity.Image;
import com.vwmin.k8sawd.web.mapper.ImageMapper;
import com.vwmin.k8sawd.web.service.ImageService;
import org.springframework.stereotype.Service;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/13 20:19
 */
@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements ImageService {

}
