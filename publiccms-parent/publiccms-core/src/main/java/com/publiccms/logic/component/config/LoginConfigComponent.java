package com.publiccms.logic.component.config;

import static com.publiccms.common.tools.LanguagesUtils.getMessage;
import static com.publiccms.common.constants.CommonConstants.applicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.publiccms.common.api.Config;
import com.publiccms.entities.sys.SysSite;
import com.publiccms.views.pojo.ExtendField;
import org.springframework.stereotype.Component;

import com.publiccms.common.base.Base;

/**
 *
 * LoginConfigComponent 登录配置组件
 *
 */
@Component
public class LoginConfigComponent implements Config, Base {
    /**
     * 
     */
    public static final String CONFIG_LOGIN_PATH = "login_path";
    /**
     * 
     */
    public static final String CONFIG_REGISTER_URL = "register_url";
    /**
     * 
     */
    public static final String CONFIG_CODE_DESCRIPTION = CONFIGPREFIX + CONFIG_CODE_SITE;

    @Override
    public String getCode(SysSite site) {
        return CONFIG_CODE_SITE;
    }

    @Override
    public String getCodeDescription(SysSite site, Locale locale) {
        return getMessage(applicationContext, locale, CONFIG_CODE_DESCRIPTION);
    }

    @Override
    public List<ExtendField> getExtendFieldList(SysSite site, Locale locale) {
        List<ExtendField> extendFieldList = new ArrayList<>();
        extendFieldList.add(new ExtendField(CONFIG_REGISTER_URL, INPUTTYPE_TEXT, true,
                getMessage(applicationContext, locale, CONFIG_CODE_DESCRIPTION + DOT + CONFIG_REGISTER_URL), null, null));
        extendFieldList.add(new ExtendField(CONFIG_LOGIN_PATH, INPUTTYPE_TEXT, false,
                getMessage(applicationContext, locale, CONFIG_CODE_DESCRIPTION + DOT + CONFIG_LOGIN_PATH), null, null));
        return extendFieldList;
    }
}
