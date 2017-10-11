package com.publiccms.logic.component.site;

import static com.publiccms.common.tools.LanguagesUtils.getMessage;
import static com.publiccms.common.constants.CommonConstants.applicationContext;

import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import com.publiccms.common.api.Config;
import com.publiccms.entities.sys.SysSite;
import com.publiccms.views.pojo.ExtendField;
import org.springframework.stereotype.Component;

/**
 * 
 * SiteAttributeComponent 站点扩展属性组件
 *
 */
@Component
public class SiteAttributeComponent implements Config {

    @Override
    public String getCode(SysSite site) {
        return CONFIG_CODE_SITEA_TTRIBUTE;
    }

    @Override
    public String getCodeDescription(SysSite site, Locale locale) {
        return getMessage(applicationContext, locale, CONFIGPREFIX + CONFIG_CODE_SITEA_TTRIBUTE);
    }

    @Override
    public List<ExtendField> getExtendFieldList(SysSite site, Locale locale) {
        List<ExtendField> extendFieldList = new ArrayList<>();
        return extendFieldList;
    }
}