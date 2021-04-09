package com.vwmin.k8sawd.web.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.vwmin.k8sawd.web.entity.Log;
import com.vwmin.k8sawd.web.enums.LogKind;
import com.vwmin.k8sawd.web.enums.LogLevel;


import java.util.List;

/**
 * <p>
 * 日志表 服务类
 * </p>
 *
 * @author vwmin
 * @since 2021-03-15
 */
public interface LogService extends IService<Log> {

    List<Log> logs();


    void log(LogLevel level, LogKind kind, String format, Object... args);

}
