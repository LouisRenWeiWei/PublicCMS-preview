package com.publiccms.logic.service.cms;

// Generated 2016-3-3 17:43:26 by com.publiccms.common.source.SourceGenerator

import java.util.Date;

import com.publiccms.entities.cms.CmsVote;
import com.publiccms.logic.dao.cms.CmsVoteDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.publiccms.common.base.BaseService;
import com.publiccms.common.handler.PageHandler;

/**
 *
 * CmsVoteService
 * 
 */
@Service
@Transactional
public class CmsVoteService extends BaseService<CmsVote> {

    /**
     * @param siteId
     * @param startStartDate
     * @param endStartDate
     * @param startEndDate
     * @param endEndDate
     * @param disabled
     * @param orderField
     * @param orderType
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageHandler getPage(Short siteId, Date startStartDate, Date endStartDate, 
                Date startEndDate, Date endEndDate, Boolean disabled, 
                String orderField, String orderType, Integer pageIndex, Integer pageSize) {
        return dao.getPage(siteId, startStartDate, endStartDate, 
                startEndDate, endEndDate, disabled, 
                orderField, orderType, pageIndex, pageSize);
    }
    
    @Autowired
    private CmsVoteDao dao;
    
}