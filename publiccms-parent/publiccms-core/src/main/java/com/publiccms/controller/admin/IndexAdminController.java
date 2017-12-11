package com.publiccms.controller.admin;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.util.UrlPathHelper;

import com.publiccms.common.base.AbstractController;
import com.publiccms.common.constants.CommonConstants;
import com.publiccms.common.tools.CommonUtils;

/**
 * 
 * IndexAdminController 统一分发Controller
 *
 */
@Controller
public class IndexAdminController extends AbstractController {
    private UrlPathHelper urlPathHelper = new UrlPathHelper();

    /**
     * 页面请求统一分发
     * 
     * @param request
     * @return view name
     */
    @RequestMapping("/**")
    public String page(HttpServletRequest request) {
        String path = urlPathHelper.getLookupPathForRequest(request);
        if (CommonUtils.notEmpty(path)) {
            if (SEPARATOR.equals(path) || path.endsWith(SEPARATOR)) {
                path += CommonConstants.getDefaultPage();
            }
            int index = path.lastIndexOf(DOT);
            path = path.substring(path.indexOf(SEPARATOR) > 0 ? 0 : 1, index > -1 ? index : path.length());
        }
        return path;
    }
}