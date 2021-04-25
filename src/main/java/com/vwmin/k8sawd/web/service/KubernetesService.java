package com.vwmin.k8sawd.web.service;

import com.vwmin.k8sawd.web.entity.Image;
import com.vwmin.k8sawd.web.model.CompetitionHandler;

/**
 * @author vwmin
 * @version 1.0
 * @date 2021/4/8 16:34
 */
public interface KubernetesService {

    boolean clearResource();
    String serviceEntry(int competitionId, int teamId);
    String sshEntry(int competitionId, int teamId);
    void deploy(int competitionId, int teamId, Image image);
    void writeFlag(CompetitionHandler competitionHandler, int competitionId, int teamId) throws InterruptedException;
    void demo(Image image);
    void stopDemo();
}
