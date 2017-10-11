package com.publiccms.logic.dao.home;

import static com.publiccms.common.tools.CommonUtils.getDate;

import com.publiccms.entities.home.HomeGroupApply;
import org.springframework.stereotype.Repository;

import com.publiccms.common.base.BaseDao;
import com.publiccms.common.handler.PageHandler;
import com.publiccms.common.handler.QueryHandler;

/**
 *
 * HomeGroupApplyDao
 * 
 */
@Repository
public class HomeGroupApplyDao extends BaseDao<HomeGroupApply> {

    /**
     * @param disabled
     * @param pageIndex
     * @param pageSize
     * @return results page
     */
    public PageHandler getPage(Boolean disabled, Integer pageIndex, Integer pageSize) {
        QueryHandler queryHandler = getQueryHandler("from HomeGroupApply bean");
        if (null != disabled) {
            queryHandler.condition("bean.disabled = :disabled").setParameter("disabled", disabled);
        }
        queryHandler.order("bean.id desc");
        return getPage(queryHandler, pageIndex, pageSize);
    }

    @Override
    protected HomeGroupApply init(HomeGroupApply entity) {
        if (null == entity.getCreateDate()) {
            entity.setCreateDate(getDate());
        }
        return entity;
    }

}