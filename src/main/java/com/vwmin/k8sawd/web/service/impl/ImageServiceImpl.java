package com.vwmin.k8sawd.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vwmin.k8sawd.web.entity.Image;
import com.vwmin.k8sawd.web.mapper.ImageMapper;
import com.vwmin.k8sawd.web.service.ImageService;
import com.vwmin.k8sawd.web.service.SystemService;
import org.springframework.stereotype.Service;

import java.io.Serializable;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/13 20:19
 */
@Service
public class ImageServiceImpl extends ServiceImpl<ImageMapper, Image> implements ImageService {

    private final SystemService systemService;
    private final Image defaultImage;

    public ImageServiceImpl(SystemService systemService) {
        this.systemService = systemService;
        defaultImage = new Image();
        defaultImage.setName("awd:1.0");
        defaultImage.setPort(80);
        defaultImage.setEnableSsh(true);
    }

    @Override
    public Image image() {
        int id = systemService.image();
        return id == -1 ? defaultImage : getById(id);
    }

    @Override
    public void setImage(Image image) {
        systemService.setImage(image);
    }

    @Override
    public void reset() {
        systemService.resetImage();
    }

    @Override
    public boolean removeById(Serializable id) {
        if (systemService.image() == (int) id){
            reset();
        }
        return super.removeById(id);
    }
}
