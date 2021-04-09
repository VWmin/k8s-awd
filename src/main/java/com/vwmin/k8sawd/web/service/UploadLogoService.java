package com.vwmin.k8sawd.web.service;

import org.springframework.web.multipart.MultipartFile;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/9 14:09
 */
public interface UploadLogoService {
    /**
     *
     * @param file 上传的文件
     * @return 返回存储的文件名
     */
    String checkAndSave(MultipartFile file);

}
