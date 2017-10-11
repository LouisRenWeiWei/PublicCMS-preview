package com.publiccms.views.directive.api;

//Generated 2015-5-10 17:54:56 by com.publiccms.common.source.SourceGenerator
import static com.publiccms.common.tools.CommonUtils.getDate;

import java.io.IOException;
import java.util.UUID;

import com.publiccms.common.base.AbstractAppDirective;
import com.publiccms.entities.sys.SysApp;
import com.publiccms.entities.sys.SysAppToken;
import com.publiccms.entities.sys.SysUser;
import com.publiccms.logic.service.sys.SysAppService;
import com.publiccms.logic.service.sys.SysAppTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.publiccms.common.handler.RenderHandler;

/**
 *
 * AppTokenDirective
 * 
 */
@Component
public class AppTokenDirective extends AbstractAppDirective {
    private final static String KEY_NOT_EXISTS = "keyNotExists";
    private final static String SECRET_ERROR = "secretError";

    @Override
    public void execute(RenderHandler handler, SysApp app, SysUser user) throws IOException, Exception {
        SysApp entity = appService.getEntity(handler.getString("appKey"));
        if (null != entity) {
            if (entity.getAppSecret().equalsIgnoreCase(handler.getString("appSecret"))) {
                SysAppToken token = new SysAppToken(UUID.randomUUID().toString(), entity.getId(), getDate());
                appTokenService.save(token);
                handler.put("appToken", token.getAuthToken());
            } else {
                handler.put("error", SECRET_ERROR);
            }
        } else {
            handler.put("error", KEY_NOT_EXISTS);
        }
    }

    @Autowired
    private SysAppTokenService appTokenService;
    @Autowired
    private SysAppService appService;

    @Override
    public boolean needUserToken() {
        return false;
    }

    @Override
    public boolean needAppToken() {
        return false;
    }
}