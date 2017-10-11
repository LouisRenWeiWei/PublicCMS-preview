package com.publiccms.views.directive.tools;

import static com.publiccms.common.tools.CommonUtils.notEmpty;
import static com.publiccms.logic.component.site.SiteComponent.getFullFileName;

import java.io.IOException;

import com.publiccms.common.base.AbstractTemplateDirective;
import com.publiccms.entities.cms.CmsCategory;
import com.publiccms.entities.cms.CmsContent;
import com.publiccms.entities.sys.SysSite;
import com.publiccms.logic.component.template.TemplateComponent;
import com.publiccms.logic.service.cms.CmsCategoryService;
import com.publiccms.logic.service.cms.CmsContentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.publiccms.common.handler.RenderHandler;

import freemarker.template.TemplateException;

/**
 *
 * CreateContentFileDirective
 * 
 */
@Component
public class CreateContentFileDirective extends AbstractTemplateDirective {
    
    @Override
    public void execute(RenderHandler handler) throws IOException, Exception {
        Long id = handler.getLong("id");
        String templatePath = handler.getString("templatePath");
        String filePath = handler.getString("filePath");
        Integer pageIndex = handler.getInteger("pageIndex");
        if (notEmpty(id) && notEmpty(templatePath) && notEmpty(filePath)) {
            SysSite site = getSite(handler);
            String templateFullPath = getFullFileName(site, templatePath);
            try {
                CmsContent content = contentService.getEntity(id);
                if (null != content && site.getId() == content.getSiteId()) {
                    CmsCategory category = categoryService.getEntity(content.getCategoryId());
                    handler.put(
                            "url",
                            templateComponent.createContentFile(site, content, category, false, templateFullPath, filePath,
                                    pageIndex)).render();
                }
            } catch (IOException | TemplateException e) {
                handler.print(e.getMessage());
            }
        }
    }
    
    @Override
    public boolean needAppToken() {
        return true;
    }

    @Autowired
    private TemplateComponent templateComponent;
    @Autowired
    private CmsCategoryService categoryService;
    @Autowired
    private CmsContentService contentService;

}
