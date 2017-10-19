package com.publiccms.logic.service.cms;

// Generated 2015-5-8 16:50:23 by com.publiccms.common.source.SourceGenerator
import static com.publiccms.common.tools.CommonUtils.empty;
import static com.publiccms.common.tools.CommonUtils.getDate;
import static com.publiccms.common.tools.CommonUtils.notEmpty;
import static org.apache.commons.lang3.ArrayUtils.add;
import static org.apache.commons.lang3.StringUtils.splitByWholeSeparator;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.publiccms.common.base.BaseService;
import com.publiccms.common.handler.FacetPageHandler;
import com.publiccms.common.handler.PageHandler;
import com.publiccms.entities.cms.CmsCategory;
import com.publiccms.entities.cms.CmsContent;
import com.publiccms.logic.dao.cms.CmsCategoryDao;
import com.publiccms.logic.dao.cms.CmsContentDao;
import com.publiccms.views.pojo.entities.CmsContentStatistics;
import com.publiccms.views.pojo.query.CmsContentQuery;

/**
 *
 * CmsContentService
 * 
 */
@Service
@Transactional
public class CmsContentService extends BaseService<CmsContent> {

    /**
     * 
     */
    public static final int STATUS_DRAFT = 0;
    /**
     * 
     */
    public static final int STATUS_NORMAL = 1;
    /**
     * 
     */
    public static final int STATUS_PEND = 2;

    /**
     * @param siteId
     * @param text
     * @param tagId
     * @param startPublishDate
     * @param endPublishDate
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageHandler query(Integer siteId, String text, String tagId, Date startPublishDate, Date endPublishDate,
            Integer pageIndex, Integer pageSize) {
        return dao.query(siteId, text, tagId, startPublishDate, endPublishDate, pageIndex, pageSize);
    }

    /**
     * @param siteId
     * @param categoryIds
     * @param modelIds
     * @param userIds
     * @param text
     * @param tagId
     * @param startPublishDate
     * @param endPublishDate
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public FacetPageHandler facetQuery(Integer siteId, String[] categoryIds, String[] modelIds, String[] userIds, String text,
            String tagId, Date startPublishDate, Date endPublishDate, Integer pageIndex, Integer pageSize) {
        return dao.facetQuery(siteId, categoryIds, modelIds, userIds, text, tagId, startPublishDate, endPublishDate, pageIndex,
                pageSize);
    }

    /**
     * @param siteId
     * @param ids
     */
    public void index(int siteId, Serializable[] ids) {
        dao.index(siteId, ids);
    }

    /**
     * @return
     */
    public Future<?> reCreateIndex() {
        return dao.reCreateIndex();
    }

    /**
     * @param queryEntity
     * @param containChild
     * @param orderField
     * @param orderType
     * @param pageIndex
     * @param pageSize
     * @return
     */
    @Transactional(readOnly = true)
    public PageHandler getPage(CmsContentQuery queryEntity, Boolean containChild, String orderField, String orderType,
            Integer pageIndex, Integer pageSize) {
        queryEntity.setCategoryIds(getCategoryIds(containChild, queryEntity.getCategoryId(), queryEntity.getCategoryIds()));
        return dao.getPage(queryEntity, orderField, orderType, pageIndex, pageSize);
    }

    /**
     * @param siteId
     * @param ids
     */
    public void refresh(int siteId, Serializable[] ids) {
        List<CmsContent> list = getEntitys(ids);
        Collections.reverse(list);
        for (CmsContent entity : list) {
            if (null != entity && STATUS_NORMAL == entity.getStatus() && siteId == entity.getSiteId()) {
                Date now = getDate();
                if (now.after(entity.getPublishDate())) {
                    entity.setPublishDate(now);
                }
            }
        }
    }

    /**
     * @param siteId
     * @param userId
     * @param ids
     * @param refresh
     * @return
     */
    public List<CmsContent> check(int siteId, Long userId, Serializable[] ids, Boolean refresh) {
        List<CmsContent> entityList = new ArrayList<>();
        for (CmsContent entity : getEntitys(ids)) {
            if (null != entity && siteId == entity.getSiteId() && STATUS_PEND == entity.getStatus()) {
                entity.setStatus(STATUS_NORMAL);
                entity.setCheckUserId(userId);
                entity.setCheckDate(getDate());
                entityList.add(entity);
                if (null != refresh && refresh) {
                    Date now = getDate();
                    if (now.after(entity.getPublishDate())) {
                        entity.setPublishDate(now);
                    }
                }
            }
        }
        return entityList;
    }

    /**
     * @param siteId
     * @param userId
     * @param ids
     * @return
     */
    public List<CmsContent> uncheck(int siteId, Long userId, Serializable[] ids) {
        List<CmsContent> entityList = new ArrayList<>();
        for (CmsContent entity : getEntitys(ids)) {
            if (null != entity && siteId == entity.getSiteId() && STATUS_NORMAL == entity.getStatus()) {
                entity.setStatus(STATUS_PEND);
                entityList.add(entity);
            }
        }
        return entityList;
    }

    /**
     * @param id
     * @param tagIds
     * @return
     */
    public CmsContent updateTagIds(Serializable id, String tagIds) {
        CmsContent entity = getEntity(id);
        if (null != entity) {
            entity.setTagIds(tagIds);
        }
        return entity;
    }

    /**
     * @param entitys
     */
    public void updateStatistics(Collection<CmsContentStatistics> entitys) {
        for (CmsContentStatistics entityStatistics : entitys) {
            CmsContent entity = getEntity(entityStatistics.getId());
            if (null != entity) {
                entity.setClicks(entity.getClicks() + entityStatistics.getClicks());
                entity.setComments(entity.getComments() + entityStatistics.getComments());
                entity.setScores(entity.getScores() + entityStatistics.getScores());
            }
        }
    }

    /**
     * @param siteId
     * @param id
     * @param categoryId
     * @return
     */
    public CmsContent updateCategoryId(int siteId, Serializable id, int categoryId) {
        CmsContent entity = getEntity(id);
        if (null != entity && siteId == entity.getSiteId()) {
            entity.setCategoryId(categoryId);
        }
        return entity;
    }

    /**
     * @param id
     * @param num
     * @return
     */
    public CmsContent updateChilds(Serializable id, int num) {
        CmsContent entity = getEntity(id);
        if (null != entity) {
            entity.setChilds(entity.getChilds() + num);
        }
        return entity;
    }

    /**
     * @param siteId
     * @param id
     * @param sort
     * @return
     */
    public CmsContent sort(Integer siteId, Long id, int sort) {
        CmsContent entity = getEntity(id);
        if (null != entity && siteId == entity.getSiteId()) {
            entity.setSort(sort);
        }
        return entity;
    }

    /**
     * @param id
     * @param url
     * @param hasStatic
     * @return
     */
    public CmsContent updateUrl(Serializable id, String url, boolean hasStatic) {
        CmsContent entity = getEntity(id);
        if (null != entity) {
            entity.setUrl(url);
            entity.setHasStatic(hasStatic);
        }
        return entity;
    }

    /**
     * @param siteId
     * @param categoryIds
     * @return
     */
    public int deleteByCategoryIds(int siteId, Integer[] categoryIds) {
        return dao.deleteByCategoryIds(siteId, categoryIds);
    }

    /**
     * @param siteId
     * @param ids
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<CmsContent> delete(int siteId, Serializable[] ids) {
        List<CmsContent> entityList = new ArrayList<>();
        for (CmsContent entity : getEntitys(ids)) {
            if (siteId == entity.getSiteId() && !entity.isDisabled()) {
                if (0 < entity.getChilds()) {
                    for (CmsContent child : (List<CmsContent>) getPage(new CmsContentQuery(siteId, null, null, null, null, null,
                            entity.getId(), null, null, null, null, null, null, null, null), null, null, null, null, null)
                                    .getList()) {
                        child.setDisabled(true);
                        entityList.add(child);
                    }
                }
                entity.setDisabled(true);
                entityList.add(entity);
            }
        }
        return entityList;
    }

    private Integer[] getCategoryIds(Boolean containChild, Integer categoryId, Integer[] categoryIds) {
        if (empty(categoryId)) {
            return categoryIds;
        } else if (null != containChild && containChild) {
            CmsCategory category = categoryDao.getEntity(categoryId);
            if (null != category && notEmpty(category.getChildIds())) {
                String[] categoryStringIds = add(splitByWholeSeparator(category.getChildIds(), COMMA_DELIMITED),
                        String.valueOf(categoryId));
                categoryIds = new Integer[categoryStringIds.length + 1];
                for (int i = 0; i < categoryStringIds.length; i++) {
                    categoryIds[i] = Integer.parseInt(categoryStringIds[i]);
                }
                categoryIds[categoryStringIds.length] = categoryId;
            }
        }
        return categoryIds;
    }

    /**
     * @param siteId
     * @param ids
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<CmsContent> recycle(int siteId, Serializable[] ids) {
        List<CmsContent> entityList = new ArrayList<>();
        for (CmsContent entity : getEntitys(ids)) {
            if (siteId == entity.getSiteId() && entity.isDisabled()) {
                if (0 < entity.getChilds()) {
                    for (CmsContent child : (List<CmsContent>) getPage(new CmsContentQuery(siteId, null, null, null, null, null,
                            entity.getId(), null, null, null, null, null, null, null, null), false, null, null, null, null)
                                    .getList()) {
                        child.setDisabled(false);
                        entityList.add(child);
                    }
                }
                entity.setDisabled(false);
                entityList.add(entity);
            }
        }
        return entityList;
    }

    /**
     * @param siteId
     * @param ids
     * @return
     */
    @SuppressWarnings("unchecked")
    public List<CmsContent> realDelete(Integer siteId, Long[] ids) {
        List<CmsContent> entityList = new ArrayList<>();
        for (CmsContent entity : getEntitys(ids)) {
            if (siteId == entity.getSiteId() && entity.isDisabled()) {
                if (0 < entity.getChilds()) {
                    for (CmsContent child : (List<CmsContent>) getPage(new CmsContentQuery(siteId, null, null, null, null, null,
                            entity.getId(), null, null, null, null, null, null, null, null), false, null, null, null, null)
                                    .getList()) {
                        delete(child.getId());
                    }
                }
                delete(entity.getId());
            }
        }
        return entityList;
    }

    @Autowired
    private CmsContentDao dao;
    @Autowired
    private CmsCategoryDao categoryDao;
}