package com.publiccms.common.servlet;

import static org.springframework.web.servlet.view.UrlBasedViewResolver.FORWARD_URL_PREFIX;
import static org.springframework.web.servlet.view.UrlBasedViewResolver.REDIRECT_URL_PREFIX;

import java.util.Locale;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.publiccms.common.constants.CmsVersion;
import com.publiccms.logic.component.site.SiteComponent;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.context.WebApplicationContext;
import org.springframework.web.servlet.View;

import com.publiccms.common.servlet.ErrorToNotFoundDispatcherServlet;

/**
 * 
 * MultiSiteDispatcherServlet 多站点动态请求处理Servlet
 *
 */
public class WebDispatcherServlet extends ErrorToNotFoundDispatcherServlet {
    /**
     * 
     */
    private static final long serialVersionUID = 1L;
    private SiteComponent siteComponent;
    /**
     * 
     */
    public static final String GLOBLE_URL_PREFIX = "globle:";
    private static final int GLOBLE_URL_PREFIX_LENGTH = GLOBLE_URL_PREFIX.length();
    private static final int REDIRECT_URL_PREFIX_LENGTH = REDIRECT_URL_PREFIX.length();
    private static final String SPECIAL_REDIRECT_URL = REDIRECT_URL_PREFIX + "//";
    private HttpRequestHandler installHandler;

    /**
     * @param applicationContext
     * @param installHandler
     */
    public WebDispatcherServlet(WebApplicationContext applicationContext, HttpRequestHandler installHandler) {
        super(applicationContext);
        this.installHandler = installHandler;
    }

    protected void doService(HttpServletRequest request, HttpServletResponse response) throws Exception {
        if (CmsVersion.isInitialized()) {
            super.doService(request, response);
        } else {
            installHandler.handleRequest(request, response);
        }
    }

    @Override
    protected View resolveViewName(String viewName, Map<String, Object> model, Locale locale, HttpServletRequest request)
            throws Exception {
        String multiSiteViewName;
        if (viewName.startsWith(REDIRECT_URL_PREFIX)) {
            if (viewName.startsWith(SPECIAL_REDIRECT_URL)) {
                multiSiteViewName = viewName.substring(0, REDIRECT_URL_PREFIX_LENGTH) + request.getScheme()
                        + viewName.substring(REDIRECT_URL_PREFIX_LENGTH - 1);
            } else {
                multiSiteViewName = viewName;
            }
        } else if (viewName.startsWith(FORWARD_URL_PREFIX)) {
            multiSiteViewName = viewName;
        } else if (viewName.startsWith(GLOBLE_URL_PREFIX)) {
            multiSiteViewName = viewName.substring(GLOBLE_URL_PREFIX_LENGTH);
        } else {
            multiSiteViewName = getSiteComponent().getViewNamePrefix(request.getServerName()) + viewName;
        }
        return super.resolveViewName(multiSiteViewName, model, locale, request);
    }

    /**
     * @return site component
     */
    public SiteComponent getSiteComponent() {
        if (null == siteComponent) {
            siteComponent = getWebApplicationContext().getBean(SiteComponent.class);
        }
        return siteComponent;
    }
}
