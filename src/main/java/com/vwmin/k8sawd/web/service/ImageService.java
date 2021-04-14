package com.vwmin.k8sawd.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vwmin.k8sawd.web.entity.Image;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/13 20:19
 */
public interface ImageService extends IService<Image> {
    Image image();

    void setImage(Image image);

    void reset();

}
