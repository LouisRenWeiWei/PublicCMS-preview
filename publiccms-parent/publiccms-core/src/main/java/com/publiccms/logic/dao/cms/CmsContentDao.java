package com.publiccms.logic.dao.cms;

import java.io.Serializable;
import java.util.Arrays;

// Generated 2015-5-8 16:50:23 by com.publiccms.common.source.SourceGenerator

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.hibernate.search.FullTextQuery;
import org.springframework.stereotype.Repository;

import com.publiccms.common.base.BaseDao;
import com.publiccms.common.handler.FacetPageHandler;
import com.publiccms.common.handler.PageHandler;
import com.publiccms.common.handler.QueryHandler;
import com.publiccms.common.tools.CommonUtils;
import com.publiccms.entities.cms.CmsContent;
import com.publiccms.views.pojo.query.CmsContentQuery;

/**
 *
 * CmsContentDao
 * 
 */
@Repository
public class CmsContentDao extends BaseDao<CmsContent> {
    private static final String[] textFields = new String[] { "title", "author", "editor", "description" };
    private static final String[] tagFields = new String[] { "tagIds" };
    private static final String[] facetFields = new String[] { "categoryId", "modelId", "userId" };

    /**
     * @param siteId
     * @param text
     * @param tagId
     * @param startPublishDate
     * @param endPublishDate
     * @param pageIndex
     * @param pageSize
     * @return results page
     */
    public PageHandler query(Integer siteId, String text, String tagId, Date startPublishDate, Date endPublishDate,
            Integer pageIndex, Integer pageSize) {
        FullTextQuery query;
        if (CommonUtils.notEmpty(tagId)) {
            query = getQuery(tagFields, tagId);
        } else {
            query = getQuery(textFields, text);
        }
        query.enableFullTextFilter("publishDate").setParameter("startPublishDate", startPublishDate)
                .setParameter("endPublishDate", endPublishDate);
        query.enableFullTextFilter("siteId").setParameter("siteId", siteId);
        return getPage(query, pageIndex, pageSize);
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
     * @return results page
     */
    public FacetPageHandler facetQuery(Integer siteId, String[] categoryIds, String[] modelIds, String[] userIds, String text,
            String tagId, Date startPublishDate, Date endPublishDate, Integer pageIndex, Integer pageSize) {
        FullTextQuery query;
        if (CommonUtils.notEmpty(tagId)) {
            query = getFacetQuery(tagFields, facetFields, tagId, 10);
        } else {
            query = getFacetQuery(textFields, facetFields, text, 10);
        }
        query.enableFullTextFilter("publishDate").setParameter("startPublishDate", startPublishDate)
                .setParameter("endPublishDate", endPublishDate);
        query.enableFullTextFilter("siteId").setParameter("siteId", siteId);
        Map<String, List<String>> valueMap = new HashMap<>();
        if (CommonUtils.notEmpty(categoryIds)) {
            valueMap.put("categoryId", Arrays.asList(categoryIds));
        }
        if (CommonUtils.notEmpty(modelIds)) {
            valueMap.put("modelId", Arrays.asList(modelIds));
        }
        if (CommonUtils.notEmpty(userIds)) {
            valueMap.put("userId", Arrays.asList(userIds));
        }
        return getFacetPage(query, facetFields, valueMap, pageIndex, pageSize);
    }

    /**
     * @param siteId
     * @param categoryIds
     * @return number of data deleted
     */
    public int deleteByCategoryIds(int siteId, Integer[] categoryIds) {
        if (CommonUtils.notEmpty(categoryIds)) {
            QueryHandler queryHandler = getQueryHandler("update CmsContent bean set bean.disabled = :disabled");
            queryHandler.condition("bean.siteId = :siteId").setParameter("siteId", siteId);
            queryHandler.condition("bean.categoryId in (:categoryIds)").setParameter("categoryIds", categoryIds)
                    .setParameter("disabled", true);
            return update(queryHandler);
        }
        return 0;
    }

    /**
     * @param siteId
     * @param ids
     */
    public void index(int siteId, Serializable[] ids) {
        for (CmsContent entity : getEntitys(ids)) {
            if (siteId == entity.getSiteId()) {
                index(entity);
            }
        }
    }

    /**
     * @param queryEntitry
     * @param orderField
     * @param orderType
     * @param pageIndex
     * @param pageSize
     * @return results page
     */
    public PageHandler getPage(CmsContentQuery queryEntitry, String orderField, String orderType, Integer pageIndex,
            Integer pageSize) {
        QueryHandler queryHandler = getQueryHandler("from CmsContent bean");
        if (CommonUtils.notEmpty(queryEntitry.getSiteId())) {
            queryHandler.condition("bean.siteId = :siteId").setParameter("siteId", queryEntitry.getSiteId());
        }
        if (CommonUtils.notEmpty(queryEntitry.getStatus())) {
            queryHandler.condition("bean.status in (:status)").setParameter("status", queryEntitry.getStatus());
        }
        if (CommonUtils.notEmpty(queryEntitry.getCategoryIds())) {
            queryHandler.condition("bean.categoryId in (:categoryIds)").setParameter("categoryIds",
                    queryEntitry.getCategoryIds());
        } else if (CommonUtils.notEmpty(queryEntitry.getCategoryId())) {
            queryHandler.condition("bean.categoryId = :categoryId").setParameter("categoryId", queryEntitry.getCategoryId());
        }
        if (null != queryEntitry.getDisabled()) {
            queryHandler.condition("bean.disabled = :disabled").setParameter("disabled", queryEntitry.getDisabled());
        }
        if (CommonUtils.notEmpty(queryEntitry.getModelIds())) {
            queryHandler.condition("bean.modelId in (:modelIds)").setParameter("modelIds", queryEntitry.getModelIds());
        }
        if (CommonUtils.notEmpty(queryEntitry.getParentId())) {
            queryHandler.condition("bean.parentId = :parentId").setParameter("parentId", queryEntitry.getParentId());
        } else if (null != queryEntitry.getEmptyParent() && queryEntitry.getEmptyParent()) {
            queryHandler.condition("bean.parentId is null");
        }
        if (null != queryEntitry.getOnlyUrl()) {
            queryHandler.condition("bean.onlyUrl = :onlyUrl").setParameter("onlyUrl", queryEntitry.getOnlyUrl());
        }
        if (null != queryEntitry.getHasImages()) {
            queryHandler.condition("bean.hasImages = :hasImages").setParameter("hasImages", queryEntitry.getHasImages());
        }
        if (null != queryEntitry.getHasFiles()) {
            queryHandler.condition("bean.hasFiles = :hasFiles").setParameter("hasFiles", queryEntitry.getHasFiles());
        }
        if (CommonUtils.notEmpty(queryEntitry.getTitle())) {
            queryHandler.condition("(bean.title like :title)").setParameter("title", like(queryEntitry.getTitle()));
        }
        if (CommonUtils.notEmpty(queryEntitry.getUserId())) {
            queryHandler.condition("bean.userId = :userId").setParameter("userId", queryEntitry.getUserId());
        }
        if (null != queryEntitry.getStartPublishDate()) {
            queryHandler.condition("bean.publishDate > :startPublishDate").setParameter("startPublishDate", queryEntitry.getStartPublishDate());
        }
        if (null != queryEntitry.getEndPublishDate()) {
            queryHandler.condition("bean.publishDate <= :endPublishDate").setParameter("endPublishDate", queryEntitry.getEndPublishDate());
        }
        if (!ORDERTYPE_ASC.equalsIgnoreCase(orderType)) {
            orderType = ORDERTYPE_DESC;
        }
        if (null == orderField) {
            orderField = BLANK;
        }
        switch (orderField) {
        case "scores":
            queryHandler.order("bean.scores " + orderType);
            break;
        case "comments":
            queryHandler.order("bean.comments " + orderType);
            break;
        case "clicks":
            queryHandler.order("bean.clicks " + orderType);
            break;
        case "publishDate":
            queryHandler.order("bean.publishDate " + orderType);
            break;
        case "updateDate":
            queryHandler.order("bean.updateDate " + orderType);
            break;
        case "checkDate":
            queryHandler.order("bean.checkDate " + orderType);
            break;
        case "default":
            orderType = ORDERTYPE_DESC;
        default:
            if (ORDERTYPE_DESC.equals(orderType)) {
                queryHandler.order("bean.sort desc");
            }
            queryHandler.order("bean.publishDate desc");
        }
        queryHandler.order("bean.id desc");
        return getPage(queryHandler, pageIndex, pageSize);
    }

    @Override
    protected CmsContent init(CmsContent entity) {
        if (null == entity.getCreateDate()) {
            entity.setCreateDate(CommonUtils.getDate());
        }
        if (null == entity.getPublishDate()) {
            entity.setPublishDate(CommonUtils.getDate());
        }
        if (CommonUtils.empty(entity.getTagIds())) {
            entity.setTagIds(null);
        }
        if (CommonUtils.empty(entity.getAuthor())) {
            entity.setAuthor(null);
        }
        if (CommonUtils.empty(entity.getCover())) {
            entity.setCover(null);
        }
        return entity;
    }

}