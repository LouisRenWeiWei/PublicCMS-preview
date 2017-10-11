package com.publiccms.controller.admin;

import static com.publiccms.common.tools.CommonUtils.getDate;
import static com.publiccms.common.tools.CommonUtils.notEmpty;
import static com.publiccms.common.tools.ControllerUtils.verifyNotEmpty;
import static com.publiccms.common.tools.ControllerUtils.verifyNotEquals;
import static com.publiccms.common.tools.ControllerUtils.verifyNotExist;
import static com.publiccms.common.tools.RequestUtils.addCookie;
import static com.publiccms.common.tools.RequestUtils.getCookie;
import static com.publiccms.common.tools.RequestUtils.getIpAddress;
import static com.publiccms.common.tools.VerificationUtils.md5Encode;
import static org.apache.commons.lang3.StringUtils.trim;
import static com.publiccms.common.constants.CommonConstants.getCookiesAdmin;
import static com.publiccms.common.constants.CommonConstants.getCookiesUserSplit;
import static com.publiccms.common.constants.CommonConstants.getDefaultPage;
import static com.publiccms.logic.service.log.LogLoginService.CHANNEL_WEB_MANAGER;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.UUID;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.publiccms.common.base.AbstractController;
import com.publiccms.entities.log.LogLogin;
import com.publiccms.entities.log.LogOperate;
import com.publiccms.entities.sys.SysSite;
import com.publiccms.entities.sys.SysUser;
import com.publiccms.entities.sys.SysUserToken;
import com.publiccms.logic.component.cache.CacheComponent;
import com.publiccms.logic.service.log.LogLoginService;
import com.publiccms.logic.service.sys.SysUserService;
import com.publiccms.logic.service.sys.SysUserTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;

/**
 *
 * LoginAdminController
 *
 */
@Controller
public class LoginAdminController extends AbstractController {

    @Autowired
    private SysUserService service;
    @Autowired
    private SysUserTokenService sysUserTokenService;
    @Autowired
    private LogLoginService logLoginService;
    @Autowired
    private CacheComponent cacheComponent;

    /**
     * @param username
     * @param password
     * @param returnUrl
     * @param request
     * @param session
     * @param response
     * @param model
     * @return view name
     */
    @RequestMapping(value = "login", method = RequestMethod.POST)
    public String login(String username, String password, String returnUrl, HttpServletRequest request, HttpSession session,
            HttpServletResponse response, ModelMap model) {
        SysSite site = getSite(request);
        username = trim(username);
        password = trim(password);
        if (verifyNotEmpty("username", username, model) || verifyNotEmpty("password", password, model)) {
            model.addAttribute("username", username);
            model.addAttribute("returnUrl", returnUrl);
            return "login";
        }
        String ip = getIpAddress(request);
        SysUser user = service.findByName(site.getId(), username);
        if (verifyNotExist("username", user, model) || verifyNotEquals("password", md5Encode(password), user.getPassword(), model)
                || verifyNotAdmin(user, model) || verifyNotEnablie(user, model)) {
            model.addAttribute("username", username);
            model.addAttribute("returnUrl", returnUrl);
            Long userId = null;
            if (null != user) {
                userId = user.getId();
            }
            logLoginService
                    .save(new LogLogin(site.getId(), username, userId, ip, CHANNEL_WEB_MANAGER, false, getDate(), password));
            return "login";
        }

        setAdminToSession(session, user);
        service.updateLoginStatus(user.getId(), ip);
        String authToken = UUID.randomUUID().toString();
        sysUserTokenService.save(new SysUserToken(authToken, site.getId(), user.getId(), CHANNEL_WEB_MANAGER, getDate(), ip));
        try {
            StringBuilder sb = new StringBuilder();
            sb.append(user.getId()).append(getCookiesUserSplit()).append(authToken).append(getCookiesUserSplit())
                    .append(user.isSuperuserAccess()).append(getCookiesUserSplit())
                    .append(URLEncoder.encode(user.getNickName(), DEFAULT_CHARSET_NAME));
            addCookie(request.getContextPath(), response, getCookiesAdmin(), sb.toString(), Integer.MAX_VALUE, null);
        } catch (UnsupportedEncodingException e) {
            log.error(e.getMessage(), e);
        }
        logLoginService.save(new LogLogin(site.getId(), username, user.getId(), ip, CHANNEL_WEB_MANAGER, true, getDate(), null));
        if (notEmpty(returnUrl)) {
            return REDIRECT + returnUrl;
        }
        return REDIRECT + getDefaultPage();
    }

    /**
     * @param username
     * @param password
     * @param request
     * @param session
     * @param response
     * @param model
     * @return view name
     */
    @RequestMapping(value = "loginDialog", method = RequestMethod.POST)
    public String loginDialog(String username, String password, HttpServletRequest request, HttpSession session,
            HttpServletResponse response, ModelMap model) {
        if ("login".equals(login(username, password, null, request, session, response, model))) {
            return TEMPLATE_ERROR;
        }
        return TEMPLATE_DONE;
    }

    /**
     * @param oldpassword
     * @param password
     * @param repassword
     * @param request
     * @param session
     * @param response
     * @param model
     * @return view name
     */
    @RequestMapping(value = "changePassword", method = RequestMethod.POST)
    public String changeMyselfPassword(String oldpassword, String password, String repassword, HttpServletRequest request,
            HttpSession session, HttpServletResponse response, ModelMap model) {
        SysSite site = getSite(request);
        SysUser user = service.getEntity(getAdminFromSession(session).getId());
        if (verifyNotEquals("siteId", site.getId(), user.getSiteId(), model)) {
            return TEMPLATE_ERROR;
        }
        String encodedOldPassword = md5Encode(oldpassword);
        if (verifyNotEquals("password", user.getPassword(), encodedOldPassword, model)) {
            return TEMPLATE_ERROR;
        } else if (verifyNotEmpty("password", password, model) || verifyNotEquals("repassword", password, repassword, model)) {
            return TEMPLATE_ERROR;
        } else {
            clearAdminToSession(request.getContextPath(), request.getSession(), response);
            model.addAttribute(MESSAGE, "message.needReLogin");
        }
        service.updatePassword(user.getId(), md5Encode(password));
        sysUserTokenService.delete(user.getId());
        logOperateService.save(new LogOperate(site.getId(), user.getId(), LogLoginService.CHANNEL_WEB_MANAGER, "changepassword",
                getIpAddress(request), getDate(), encodedOldPassword));
        return "common/ajaxTimeout";
    }

    /**
     * @param request
     * @param response
     * @return view name
     */
    @RequestMapping(value = "logout", method = RequestMethod.GET)
    public String logout(HttpServletRequest request, HttpServletResponse response) {
        Cookie userCookie = getCookie(request.getCookies(), getCookiesAdmin());
        if (null != userCookie && notEmpty(userCookie.getValue())) {
            String value = userCookie.getValue();
            if (null != value) {
                String[] userData = value.split(getCookiesUserSplit());
                if (userData.length > 1) {
                    sysUserTokenService.delete(userData[1]);
                }
            }
        }
        clearAdminToSession(request.getContextPath(), request.getSession(), response);
        return REDIRECT + getDefaultPage();
    }

    /**
     * @return view name
     */
    @RequestMapping(value = "clearCache")
    public String clearCache() {
        cacheComponent.clear();
        return TEMPLATE_DONE;
    }

    protected boolean verifyNotAdmin(SysUser user, ModelMap model) {
        if (!user.isDisabled() && !user.isSuperuserAccess()) {
            model.addAttribute(ERROR, "verify.user.notAdmin");
            return true;
        }
        return false;
    }

    protected boolean verifyNotEnablie(SysUser user, ModelMap model) {
        if (user.isDisabled()) {
            model.addAttribute(ERROR, "verify.user.notEnablie");
            return true;
        }
        return false;
    }
}
