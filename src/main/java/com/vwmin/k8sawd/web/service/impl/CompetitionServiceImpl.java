package com.vwmin.k8sawd.web.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.vwmin.k8sawd.web.entity.Competition;
import com.vwmin.k8sawd.web.mapper.CompetitionMapper;
import com.vwmin.k8sawd.web.service.CompetitionService;
import org.springframework.stereotype.Service;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/3/29 11:45
 */
@Service
public class CompetitionServiceImpl extends ServiceImpl<CompetitionMapper, Competition> implements CompetitionService {
    @Override
    public void createCompetition(Competition competition) {

    }
}
