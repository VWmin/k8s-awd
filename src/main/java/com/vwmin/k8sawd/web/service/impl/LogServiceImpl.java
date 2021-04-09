package com.vwmin.k8sawd.web.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vwmin.k8sawd.web.entity.Log;
import com.vwmin.k8sawd.web.enums.LogKind;
import com.vwmin.k8sawd.web.enums.LogLevel;
import com.vwmin.k8sawd.web.mapper.LogMapper;
import com.vwmin.k8sawd.web.service.LogService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * <p>
 * 日志表 服务实现类
 * </p>
 *
 * @author vwmin
 * @since 2021-03-15
 */
@Slf4j
@Service
public class LogServiceImpl extends ServiceImpl<LogMapper, Log> implements LogService {

    @Override
    public List<Log> logs() {
        LambdaQueryWrapper<Log> condition = new LambdaQueryWrapper<>();
        condition.orderByDesc(Log::getId).last("limit 30");
        return list(condition);
    }

    @Override
    public void log(LogLevel level, LogKind kind, String format, Object... args) {
        String content = String.format(format, args);

        log.info(content);

        save(new Log(level.val(), kind.toString(), content));
    }
}
