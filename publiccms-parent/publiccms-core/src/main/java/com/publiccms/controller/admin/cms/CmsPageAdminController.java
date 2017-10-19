package com.publiccms.controller.admin.cms;
import static com.publiccms.common.tools.CommonUtils.getDate;
import static com.publiccms.common.tools.CommonUtils.notEmpty;
import static com.publiccms.common.tools.RequestUtils.getIpAddress;
import static com.publiccms.logic.component.site.SiteComponent.getFullFileName;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.publiccms.common.base.AbstractController;
import com.publiccms.entities.log.LogOperate;
import com.publiccms.entities.sys.SysSite;
import com.publiccms.logic.component.template.MetadataComponent;
import com.publiccms.logic.component.template.TemplateCacheComponent;
import com.publiccms.logic.service.log.LogLoginService;
import com.publiccms.views.pojo.entities.CmsPageMetadata;
import com.publiccms.views.pojo.model.CmsPlaceParamters;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.RequestMapping;

/**
 * 
 * CmsPageController
 *
 */
@Controller
@RequestMapping("cmsPage")
public class CmsPageAdminController extends AbstractController {
    @Autowired
    private MetadataComponent metadataComponent;
    @Autowired
    private TemplateCacheComponent templateCacheComponent;

    /**
     * @param path
     * @param placeParamters
     * @param request
     * @param session
     * @param model
     * @return view name
     */
    @RequestMapping("save")
    public String saveMetadata(String path, @ModelAttribute CmsPlaceParamters placeParamters, HttpServletRequest request,
            HttpSession session, ModelMap model) {
        if (notEmpty(path)) {
            SysSite site = getSite(request);
            String filePath = siteComponent.getWebTemplateFilePath(site, path);
            CmsPageMetadata oldmetadata = metadataComponent.getTemplateMetadata(filePath);
            oldmetadata.setExtendDataList(placeParamters.getExtendDataList());
            metadataComponent.updateTemplateMetadata(filePath, oldmetadata);
            logOperateService.save(new LogOperate(site.getId(), getAdminFromSession(session).getId(),
                    LogLoginService.CHANNEL_WEB_MANAGER, "update.template.data", getIpAddress(request), getDate(), path));
        }
        return TEMPLATE_DONE;
    }

    /**
     * @param path
     * @param request
     * @param session
     * @param model
     * @return view name
     */
    @RequestMapping("clearCache")
    public String clearCache(String path, HttpServletRequest request, HttpSession session, ModelMap model) {
        if (notEmpty(path)) {
            SysSite site = getSite(request);
            templateCacheComponent.deleteCachedFile(getFullFileName(site, path));
            logOperateService.save(new LogOperate(site.getId(), getAdminFromSession(session).getId(),
                    LogLoginService.CHANNEL_WEB_MANAGER, "clear.pageCache", getIpAddress(request), getDate(), path));
        }
        return TEMPLATE_DONE;
    }
}
