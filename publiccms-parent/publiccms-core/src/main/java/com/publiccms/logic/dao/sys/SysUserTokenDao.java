package com.publiccms.logic.dao.sys;

import static com.publiccms.common.tools.CommonUtils.getDate;
import static com.publiccms.common.tools.CommonUtils.notEmpty;

import java.util.Date;

import com.publiccms.entities.sys.SysUserToken;
import org.springframework.stereotype.Repository;

import com.publiccms.common.base.BaseDao;
import com.publiccms.common.handler.PageHandler;
import com.publiccms.common.handler.QueryHandler;

/**
 *
 * SysUserTokenDao
 * 
 */
@Repository
public class SysUserTokenDao extends BaseDao<SysUserToken> {

    /**
     * @param siteId
     * @param userId
     * @param channel
     * @param orderType
     * @param pageIndex
     * @param pageSize
     * @return results page
     */
    public PageHandler getPage(Integer siteId, Long userId, String channel, String orderType, Integer pageIndex,
            Integer pageSize) {
        QueryHandler queryHandler = getQueryHandler("from SysUserToken bean");
        if (notEmpty(siteId)) {
            queryHandler.condition("bean.siteId = :siteId").setParameter("siteId", siteId);
        }
        if (notEmpty(userId)) {
            queryHandler.condition("bean.userId = :userId").setParameter("userId", userId);
        }
        if (notEmpty(channel)) {
            queryHandler.condition("bean.channel = :channel").setParameter("channel", channel);
        }
        if (!ORDERTYPE_ASC.equalsIgnoreCase(orderType)) {
            orderType = ORDERTYPE_DESC;
        }
        queryHandler.order("bean.createDate " + orderType);
        return getPage(queryHandler, pageIndex, pageSize);
    }

    /**
     * @param createDate
     * @return number of data deleted
     */
    public int delete(Date createDate) {
        if (null != createDate) {
            QueryHandler queryHandler = getDeleteQueryHandler("from SysUserToken bean");
            queryHandler.condition("bean.createDate <= :createDate").setParameter("createDate", createDate);
            return delete(queryHandler);
        }
        return 0;
    }

    /**
     * @param userId
     * @return number of data deleted
     */
    public int delete(Long userId) {
        if (notEmpty(userId)) {
            QueryHandler queryHandler = getDeleteQueryHandler("from SysUserToken bean");
            queryHandler.condition("bean.userId <= :userId").setParameter("userId", userId);
            return delete(queryHandler);
        }
        return 0;
    }

    @Override
    protected SysUserToken init(SysUserToken entity) {
        if (null == entity.getCreateDate()) {
            entity.setCreateDate(getDate());
        }
        return entity;
    }

}