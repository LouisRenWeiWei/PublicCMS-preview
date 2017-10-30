package com.publiccms.controller.admin.sys;

import static com.publiccms.common.tools.CommonUtils.getDate;
import static com.publiccms.common.tools.CommonUtils.notEmpty;
import static com.publiccms.common.tools.ControllerUtils.verifyCustom;
import static com.publiccms.common.tools.FreeMarkerUtils.generateStringByFile;
import static com.publiccms.common.tools.JsonUtils.getString;
import static com.publiccms.common.tools.RequestUtils.getIpAddress;
import static com.publiccms.logic.component.site.SiteComponent.getFullFileName;
import static com.publiccms.common.base.AbstractFreemarkerView.exposeAttribute;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import com.publiccms.common.base.AbstractController;
import com.publiccms.entities.log.LogOperate;
import com.publiccms.entities.sys.SysSite;
import com.publiccms.logic.component.site.FileComponent;
import com.publiccms.logic.component.template.TemplateComponent;
import com.publiccms.logic.service.log.LogLoginService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;

import freemarker.template.TemplateException;

/**
 *
 * CmsTemplateAdminController
 *
 */
@Controller
@RequestMapping("taskTemplate")
public class TaskTemplateAdminController extends AbstractController {
    @Autowired
    private TemplateComponent templateComponent;
    @Autowired
    private FileComponent fileComponent;

    /**
     * @param path
     * @param content
     * @param request
     * @param session
     * @param model
     * @return view name
     */
    @RequestMapping("save")
    public String save(String path, String content, HttpServletRequest request, HttpSession session, ModelMap model) {
        SysSite site = getSite(request);
        if (notEmpty(path)) {
            try {
                String filePath = siteComponent.getTaskTemplateFilePath(site, path);
                File templateFile = new File(filePath);
                if (notEmpty(templateFile)) {
                    fileComponent.updateFile(templateFile, content);
                    logOperateService.save(new LogOperate(site.getId(), getAdminFromSession(session).getId(),
                            LogLoginService.CHANNEL_WEB_MANAGER, "update.task.template", getIpAddress(request), getDate(), path));
                } else {
                    fileComponent.createFile(templateFile, content);
                    logOperateService.save(new LogOperate(site.getId(), getAdminFromSession(session).getId(),
                            LogLoginService.CHANNEL_WEB_MANAGER, "save.task.template", getIpAddress(request), getDate(), path));
                }
                templateComponent.clear();
            } catch (IOException e) {
                model.addAttribute(ERROR, e.getMessage());
                log.error(e.getMessage(), e);
                return TEMPLATE_ERROR;
            }
        }
        return TEMPLATE_DONE;
    }

    /**
     * @param filePath
     * @param request
     * @param session
     * @param model
     * @return view name
     */
    @RequestMapping("runTask")
    public String runTask(String filePath, HttpServletRequest request, HttpSession session, ModelMap model) {
        SysSite site = getSite(request);
        model.addAttribute("filePath", filePath);
        try {
            String fulllPath = getFullFileName(site, filePath);
            Map<String, Object> map = new HashMap<>();
            exposeAttribute(map, request.getScheme(), request.getServerName(), request.getServerPort(), request.getContextPath());
            model.addAttribute("result", generateStringByFile(fulllPath, templateComponent.getTaskConfiguration(), map));
        } catch (IOException | TemplateException e) {
            model.addAttribute(ERROR, e.getMessage());
            log.error(e.getMessage(), e);
        }
        logOperateService.save(new LogOperate(site.getId(), getAdminFromSession(session).getId(),
                LogLoginService.CHANNEL_WEB_MANAGER, "run.task.template", getIpAddress(request), getDate(), getString(model)));
        return TEMPLATE_DONE;
    }

    /**
     * @param path
     * @param request
     * @param session
     * @param model
     * @return view name
     */
    @RequestMapping("delete")
    public String delete(String path, HttpServletRequest request, HttpSession session, ModelMap model) {
        if (notEmpty(path)) {
            SysSite site = getSite(request);
            String filePath = siteComponent.getTaskTemplateFilePath(site, path);
            if (verifyCustom("notExist.template", !fileComponent.deleteFile(filePath), model)) {
                return TEMPLATE_ERROR;
            }
            templateComponent.clear();
            logOperateService.save(new LogOperate(site.getId(), getAdminFromSession(session).getId(),
                    LogLoginService.CHANNEL_WEB_MANAGER, "delete.task.template", getIpAddress(request), getDate(), path));
        }
        return TEMPLATE_DONE;
    }

}
