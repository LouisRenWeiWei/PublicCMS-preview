package com.publiccms.common.search;

import static com.publiccms.logic.component.BeanComponent.getContentAttributeService;

import org.apache.lucene.document.Document;
import org.hibernate.search.bridge.FieldBridge;
import org.hibernate.search.bridge.LuceneOptions;
import com.publiccms.entities.cms.CmsContent;
import com.publiccms.entities.cms.CmsContentAttribute;

/**
 *
 * CmsContentBridge
 * 
 */
public class CmsContentBridge implements FieldBridge {

    @Override
    public void set(String name, Object value, Document document, LuceneOptions luceneOptions) {
        CmsContent content = (CmsContent) value;
        CmsContentAttribute entity = getContentAttributeService().getEntity(content.getId());
        if (null != entity) {
            content.setDescription(content.getDescription() + entity.getText());
        }
    }
}