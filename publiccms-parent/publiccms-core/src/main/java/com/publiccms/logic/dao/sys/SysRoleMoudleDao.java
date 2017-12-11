package com.publiccms.logic.dao.sys;

import java.util.ArrayList;
import java.util.List;

import org.springframework.stereotype.Repository;

import com.publiccms.common.base.BaseDao;
import com.publiccms.common.handler.PageHandler;
import com.publiccms.common.handler.QueryHandler;
import com.publiccms.common.tools.CommonUtils;
import com.publiccms.entities.sys.SysRoleMoudle;

/**
 *
 * SysRoleMoudleDao
 * 
 */
@Repository
public class SysRoleMoudleDao extends BaseDao<SysRoleMoudle> {

    /**
     * @param roleId
     * @param moudleId
     * @param pageIndex
     * @param pageSize
     * @return results page
     */
    public PageHandler getPage(Integer roleId, Integer moudleId, Integer pageIndex, Integer pageSize) {
        QueryHandler queryHandler = getQueryHandler("from SysRoleMoudle bean");
        if (CommonUtils.notEmpty(roleId)) {
            queryHandler.condition("bean.id.roleId = :roleId").setParameter("roleId", roleId);
        }
        if (CommonUtils.notEmpty(moudleId)) {
            queryHandler.condition("bean.id.moudleId = :moudleId").setParameter("moudleId", moudleId);
        }
        return getPage(queryHandler, pageIndex, pageSize);
    }

    /**
     * @param roleIds
     * @param moudleId
     * @return entity
     */
    public SysRoleMoudle getEntity(Integer[] roleIds, Integer moudleId) {
        if (CommonUtils.notEmpty(roleIds) && CommonUtils.notEmpty(moudleId)) {
            QueryHandler queryHandler = getQueryHandler("from SysRoleMoudle bean");
            queryHandler.condition("bean.id.roleId in (:roleIds)").setParameter("roleIds", roleIds);
            queryHandler.condition("bean.id.moudleId = :moudleId").setParameter("moudleId", moudleId);
            return getEntity(queryHandler);
        }
        return null;
    }

    /**
     * @param roleIds
     * @param moudleIds
     * @return entitys list
     */
    @SuppressWarnings("unchecked")
    public List<SysRoleMoudle> getEntitys(Integer[] roleIds, Integer[] moudleIds) {
        if (CommonUtils.notEmpty(roleIds) && CommonUtils.notEmpty(moudleIds)) {
            QueryHandler queryHandler = getQueryHandler("from SysRoleMoudle bean");
            queryHandler.condition("bean.id.roleId in (:roleIds)").setParameter("roleIds", roleIds);
            queryHandler.condition("bean.id.moudleId in (:moudleIds)").setParameter("moudleIds", moudleIds);
            return (List<SysRoleMoudle>) getList(queryHandler);
        }
        return new ArrayList<>();
    }

    /**
     * @param roleId
     * @return number of data deleted
     */
    public int deleteByRoleId(Integer roleId) {
        if (CommonUtils.notEmpty(roleId)) {
            QueryHandler queryHandler = getDeleteQueryHandler("from SysRoleMoudle bean where bean.id.roleId = :roleId");
            queryHandler.setParameter("roleId", roleId);
            return delete(queryHandler);
        }
        return 0;
    }

    /**
     * @param moudleId
     * @return number of data deleted
     */
    public int deleteByMoudleId(Integer moudleId) {
        if (CommonUtils.notEmpty(moudleId)) {
            QueryHandler queryHandler = getDeleteQueryHandler("from SysRoleMoudle bean where bean.id.moudleId = :moudleId");
            queryHandler.setParameter("moudleId", moudleId);
            return delete(queryHandler);
        }
        return 0;
    }

    @Override
    protected SysRoleMoudle init(SysRoleMoudle entity) {
        return entity;
    }

}