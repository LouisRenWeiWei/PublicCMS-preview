package com.publiccms.controller.web;

import static com.publiccms.common.tools.CommonUtils.notEmpty;
import static com.publiccms.common.tools.RequestUtils.getEncodePath;
import static org.apache.commons.lang3.ArrayUtils.contains;
import static org.apache.commons.lang3.ArrayUtils.isNotEmpty;
import static org.apache.commons.lang3.StringUtils.split;
import static com.publiccms.common.api.Config.CONFIG_CODE_SITE;
import static com.publiccms.common.constants.CommonConstants.getDefaultPage;
import static com.publiccms.common.constants.CommonConstants.getDefaultSubfix;
import static com.publiccms.logic.component.config.LoginConfigComponent.CONFIG_LOGIN_PATH;

import java.io.IOException;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.publiccms.common.base.AbstractController;
import com.publiccms.entities.sys.SysDomain;
import com.publiccms.entities.sys.SysSite;
import com.publiccms.logic.component.config.ConfigComponent;
import com.publiccms.logic.component.template.MetadataComponent;
import com.publiccms.logic.component.template.TemplateCacheComponent;
import com.publiccms.views.pojo.entities.CmsPageMetadata;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UrlPathHelper;

/**
 * 
 * IndexController 统一分发Controller
 *
 */
@Controller
public class IndexController extends AbstractController {
    @Autowired
    private MetadataComponent metadataComponent;
    @Autowired
    private TemplateCacheComponent templateCacheComponent;
    @Autowired
    private ConfigComponent configComponent;
    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    /**
     * REST页面请求统一分发
     * 
     * @param id
     * @param body
     * @param request
     * @param response
     * @param model
     * @return view name
     */
    @RequestMapping({ "/**/{id:[0-9]+}" })
    public String rest(@PathVariable("id") long id, @RequestBody(required = false) String body, HttpServletRequest request,
            HttpServletResponse response, ModelMap model) {
        return restPage(id, null, body, request, response, model);
    }
    
    /**
     * REST页面请求统一分发
     * 
     * @param id
     * @param pageIndex
     * @param body
     * @param request
     * @param response
     * @param model
     * @return view name
     */
    @RequestMapping({ "/**/{id:[0-9]+}_{pageIndex:[0-9]+}" })
    public String restPage(@PathVariable("id") long id, @PathVariable("pageIndex") Integer pageIndex,
            @RequestBody(required = false) String body, HttpServletRequest request, HttpServletResponse response,
            ModelMap model) {
        String requestPath = urlPathHelper.getLookupPathForRequest(request);
        if (requestPath.endsWith(SEPARATOR)) {
            requestPath = requestPath.substring(0, requestPath.lastIndexOf(SEPARATOR, requestPath.length() - 2))
                    + getDefaultSubfix();
        } else {
            requestPath = requestPath.substring(0, requestPath.lastIndexOf(SEPARATOR)) + getDefaultSubfix();
        }
        return getViewName(id, pageIndex, requestPath, body, request, response, model);
    }

    /**
     * 页面请求统一分发
     * 
     * @param body
     * @param request
     * @param response
     * @param model
     * @return view name
     */
    @RequestMapping({ SEPARATOR, "/**" })
    public String page(@RequestBody(required = false) String body, HttpServletRequest request, HttpServletResponse response,
            ModelMap model) {
        String requestPath = urlPathHelper.getLookupPathForRequest(request);
        if (requestPath.endsWith(SEPARATOR)) {
            requestPath += getDefaultPage();
        }
        return getViewName(null, null, requestPath, body, request, response, model);
    }

    private String getViewName(Long id, Integer pageIndex, String requestPath, String body, HttpServletRequest request,
            HttpServletResponse response, ModelMap model) {
        SysDomain domain = getDomain(request);
        SysSite site = getSite(request);
        String fullRequestPath = siteComponent.getViewNamePrefix(site, domain) + requestPath;
        String templatePath = siteComponent.getWebTemplateFilePath() + fullRequestPath;
        CmsPageMetadata metadata = metadataComponent.getTemplateMetadata(templatePath, true);
        if (null != metadata) {
            if (metadata.isUseDynamic()) {
                if (metadata.isNeedLogin() && null == getUserFromSession(request.getSession())) {
                    Map<String, String> config = configComponent.getConfigData(site.getId(), CONFIG_CODE_SITE);
                    String loginPath = config.get(CONFIG_LOGIN_PATH);
                    StringBuilder sb = new StringBuilder(REDIRECT);
                    if (notEmpty(loginPath)) {
                        return sb.append(loginPath).append("?returnUrl=")
                                .append(getEncodePath(requestPath, request.getQueryString())).toString();
                    } else {
                        return sb.append(site.getDynamicPath()).toString();
                    }
                }
                String[] acceptParamters = split(metadata.getAcceptParamters(), COMMA_DELIMITED);
                billingRequestParamtersToModel(request, acceptParamters, model);
                if (null != id && contains(acceptParamters, "id")) {
                    model.addAttribute("id", id.toString());
                    if (null != pageIndex && contains(acceptParamters, "pageIndex")) {
                        model.addAttribute("pageIndex", pageIndex.toString());
                    }
                }
                model.addAttribute("metadata", metadata);
                if (metadata.isNeedBody()) {
                    model.addAttribute("body", body);
                }
                if (notEmpty(metadata.getContentType())) {
                    response.setContentType(metadata.getContentType());
                }
                if (notEmpty(metadata.getCacheTime()) && 0 < metadata.getCacheTime()) {
                    int cacheMillisTime = metadata.getCacheTime() * 1000;
                    String cacheControl = request.getHeader("Cache-Control");
                    String pragma = request.getHeader("Pragma");
                    if (notEmpty(cacheControl) && "no-cache".equalsIgnoreCase(cacheControl)
                            || notEmpty(pragma) && "no-cache".equalsIgnoreCase(pragma)) {
                        cacheMillisTime = 0;
                    }
                    return templateCacheComponent.getCachedPath(requestPath, fullRequestPath, cacheMillisTime,
                            acceptParamters, request, model);
                }
            } else {
                try {
                    response.sendError(HttpServletResponse.SC_NOT_FOUND);
                } catch (IOException e) {
                }
            }
        }
        return requestPath;
    }

    private void billingRequestParamtersToModel(HttpServletRequest request, String[] acceptParamters, ModelMap model) {
        for (String paramterName : acceptParamters) {
            String[] values = request.getParameterValues(paramterName);
            if (isNotEmpty(values)) {
                if (1 < values.length) {
                    model.addAttribute(paramterName, values);
                } else {
                    model.addAttribute(paramterName, values[0]);
                }
            }
        }
    }
}
