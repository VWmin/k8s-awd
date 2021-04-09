package com.vwmin.k8sawd.web.service.impl;

import cn.hutool.core.io.FileTypeUtil;
import cn.hutool.core.util.RandomUtil;
import com.vwmin.k8sawd.web.exception.RoutineException;
import com.vwmin.k8sawd.web.model.ResponseCode;
import com.vwmin.k8sawd.web.service.UploadLogoService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.PostConstruct;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.Set;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/19 18:55
 */
@Slf4j
@Service
public class UploadLogoServiceImpl implements UploadLogoService {
    private static final String PATH = "uploads/";
    private static final Set<String> EXPECTED_FILE_TYPE = new HashSet<String>(){{
        add("jpg");
        add("png");
        add("gif");
    }};


    // 1.检查upload文件夹是否存在

    // 2.限制文件大小 、 检查文件类型是否合格

    // 3.检查是否存在相同的文件


    @PostConstruct
    public void checkUploadFilePath(){
        File folder = new File(PATH);
        if (!folder.exists() || !folder.isDirectory()){
            if (!folder.mkdir()){
                log.error("上传文件夹创建失败.");
            }
        }
    }

    @Override
    public String checkAndSave(MultipartFile file){
        if (file == null){
            throw new RoutineException("文件为空");
        }

        String fileName;
        try{
            String type = FileTypeUtil.getType(file.getInputStream());
            if (! EXPECTED_FILE_TYPE.contains(type)){
                throw new RoutineException("不支持的文件类型");
            }

            fileName = RandomUtil.randomString(32) + "." + type;
            log.info("即将存储的文件名 {}", fileName);

            // 写入文件系统
            FileCopyUtils.copy(file.getInputStream(), Files.newOutputStream(Paths.get(PATH.concat(fileName))));

        }catch (IOException e){
            throw new RoutineException(ResponseCode.FAIL, "\t文件服务出错: " + e.getMessage(), e);
        }

        return fileName;

    }


}
