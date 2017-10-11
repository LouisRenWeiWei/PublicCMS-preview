package com.publiccms.logic.service.cms;

// Generated 2016-11-20 14:50:55 by com.publiccms.common.source.SourceGenerator

import static com.publiccms.common.tools.CommonUtils.notEmpty;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.publiccms.entities.cms.CmsDictionaryData;
import com.publiccms.entities.cms.CmsDictionaryDataId;
import com.publiccms.logic.dao.cms.CmsDictionaryDataDao;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.publiccms.common.base.BaseService;

/**
 *
 * CmsDictionaryDataService
 * 
 */
@Service
@Transactional
public class CmsDictionaryDataService extends BaseService<CmsDictionaryData> {
    /**
     * @param dictionaryId
     * @param dataList
     */
    public void save(Long dictionaryId, List<CmsDictionaryData> dataList) {
        if (notEmpty(dataList)) {
            for (CmsDictionaryData entity : dataList) {
                if (null != entity.getId()) {
                    entity.getId().setDictionaryId(dictionaryId);
                    save(entity);
                }
            }
        }
    }

    /**
     * @param dictionaryId
     * @param dataList
     */
    public void update(Long dictionaryId, List<CmsDictionaryData> dataList) {
        Set<CmsDictionaryDataId> idSet = new HashSet<>();
        if (notEmpty(dataList)) {
            for (CmsDictionaryData entity : dataList) {
                if (null != entity.getId()) {
                    entity.getId().setDictionaryId(dictionaryId);
                    CmsDictionaryData oldEntity = getEntity(entity.getId());
                    if (null == oldEntity) {
                        save(entity);
                    } else {
                        oldEntity.setText(entity.getText());
                    }
                    idSet.add(entity.getId());
                }
            }
        }
        List<CmsDictionaryData> list = getList(dictionaryId);
        for (CmsDictionaryData entity : list) {
            if (!idSet.contains(entity.getId())) {
                delete(entity.getId());
            }
        }
    }

    /**
     * @param dictionaryId
     * @return
     */
    @Transactional(readOnly = true)
    public List<CmsDictionaryData> getList(long dictionaryId) {
        return dao.getList(dictionaryId);
    }

    @Autowired
    private CmsDictionaryDataDao dao;

}