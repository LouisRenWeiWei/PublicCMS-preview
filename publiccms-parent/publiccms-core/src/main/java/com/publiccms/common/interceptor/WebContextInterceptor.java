package com.publiccms.common.interceptor;

import static com.publiccms.common.tools.CommonUtils.getDate;
import static com.publiccms.common.tools.RequestUtils.cancleCookie;
import static com.publiccms.common.tools.RequestUtils.getCookie;
import static com.publiccms.common.tools.RequestUtils.getIpAddress;
import static org.apache.commons.lang3.StringUtils.isNotBlank;
import static org.apache.commons.lang3.time.DateUtils.addSeconds;
import static com.publiccms.common.base.AbstractController.clearUserToSession;
import static com.publiccms.common.base.AbstractController.getUserFromSession;
import static com.publiccms.common.base.AbstractController.getUserTimeFromSession;
import static com.publiccms.common.base.AbstractController.setUserToSession;
import static com.publiccms.common.constants.CmsVersion.getVersion;
import static com.publiccms.common.constants.CommonConstants.getCookiesUser;
import static com.publiccms.common.constants.CommonConstants.getCookiesUserSplit;
import static com.publiccms.common.constants.CommonConstants.getXPowered;
import static com.publiccms.logic.service.log.LogLoginService.CHANNEL_WEB;

import java.util.Date;

import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import com.publiccms.entities.log.LogLogin;
import com.publiccms.entities.sys.SysSite;
import com.publiccms.entities.sys.SysUser;
import com.publiccms.entities.sys.SysUserToken;
import com.publiccms.logic.component.site.SiteComponent;
import com.publiccms.logic.service.log.LogLoginService;
import com.publiccms.logic.service.sys.SysUserService;
import com.publiccms.logic.service.sys.SysUserTokenService;
import org.springframework.beans.factory.annotation.Autowired;

import com.publiccms.common.base.BaseInterceptor;

/**
 * 
 * WebContextInterceptor 权限拦截器
 *
 */
public class WebContextInterceptor extends BaseInterceptor {
    @Autowired
    private SysUserService sysUserService;
    @Autowired
    private SysUserTokenService sysUserTokenService;
    @Autowired
    private SiteComponent siteComponent;
    @Autowired
    private LogLoginService logLoginService;

    protected SysUser initUser(SysUser user, String channel, String cookiesName, SysSite site, HttpServletRequest request,
            HttpServletResponse response) {
        response.addHeader(getXPowered(), getVersion());
        String contextPath = request.getContextPath();
        if (null == user) {
            Cookie userCookie = getCookie(request.getCookies(), cookiesName);
            if (null != userCookie && isNotBlank(userCookie.getValue())) {
                String value = userCookie.getValue();
                if (null != value) {
                    String[] userData = value.split(getCookiesUserSplit());
                    if (userData.length > 1) {
                        try {
                            Long userId = Long.parseLong(userData[0]);
                            SysUserToken userToken = sysUserTokenService.getEntity(userData[1]);
                            if (null != userToken && null != site && !site.isDisabled() && site.getId() == userToken.getSiteId()
                                    && userId == userToken.getUserId() && channel.equals(userToken.getChannel())
                                    && null != (user = sysUserService.getEntity(userId)) && !user.isDisabled()) {
                                user.setPassword(null);
                                String ip = getIpAddress(request);
                                sysUserService.updateLoginStatus(user.getId(), ip);
                                logLoginService.save(new LogLogin(site.getId(), user.getName(), user.getId(), ip, channel, true, getDate(), null));
                            } else {
                                user = null;
                                if (null != userToken) {
                                    sysUserTokenService.delete(userToken.getAuthToken());
                                }
                                cancleCookie(contextPath, response, cookiesName, null);
                            }
                        } catch (NumberFormatException e) {
                            cancleCookie(contextPath, response, cookiesName, null);
                        }
                    } else {
                        cancleCookie(contextPath, response, cookiesName, null);
                    }
                }
            }
        }
        return user;
    }

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        HttpSession session = request.getSession();
        SysSite site = siteComponent.getSite(request.getServerName());
        SysUser user = initUser(getUserFromSession(session), CHANNEL_WEB, getCookiesUser(), site, request, response);
        if (null != user) {
            Date date = getUserTimeFromSession(session);
            if (null == date || date.before(addSeconds(new Date(), -30))) {
                SysUser entity = sysUserService.getEntity(user.getId());
                if (null != entity && !entity.isDisabled() && null != site && !site.isDisabled()
                        && site.getId() == entity.getSiteId()) {
                    user.setName(entity.getName());
                    user.setNickName(entity.getNickName());
                    user.setEmail(entity.getEmail());
                    user.setEmailChecked(entity.isEmailChecked());
                    user.setSuperuserAccess(entity.isSuperuserAccess());
                    setUserToSession(session, user);
                } else {
                    Cookie userCookie = getCookie(request.getCookies(), getCookiesUser());
                    if (null != userCookie && isNotBlank(userCookie.getValue())) {
                        String value = userCookie.getValue();
                        if (null != value) {
                            String[] userData = value.split(getCookiesUserSplit());
                            if (userData.length > 1) {
                                sysUserTokenService.delete(userData[1]);
                            }
                        }
                    }
                    clearUserToSession(request.getContextPath(), session, response);
                }
            }
        }
        return true;
    }
}
