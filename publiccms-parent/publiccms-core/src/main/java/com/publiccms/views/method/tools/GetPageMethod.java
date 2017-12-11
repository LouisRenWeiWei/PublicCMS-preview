package com.publiccms.views.method.tools;

import java.util.List;

import org.springframework.stereotype.Component;

import com.publiccms.common.base.BaseMethod;
import com.publiccms.common.tools.CommonUtils;

import freemarker.template.TemplateModelException;

/**
 *
 * GetPageMethod
 * 
 */
@Component
public class GetPageMethod extends BaseMethod {

    @SuppressWarnings("unchecked")
    @Override
    public Object exec(@SuppressWarnings("rawtypes") List arguments) throws TemplateModelException {
        String url = getString(0, arguments);
        Integer pageIndex = getInteger(1, arguments);
        if (CommonUtils.notEmpty(url) && CommonUtils.notEmpty(pageIndex)) {
            return getPageUrl(url, pageIndex);
        }
        return url;
    }

    public String getPageUrl(String url, int pageIndex) {
        int index = url.lastIndexOf('.');
        if (-1 < index) {
            String prefixFilePath = url.substring(0, index);
            String suffixFilePath = url.substring(index, url.length());
            if (url.lastIndexOf("/") < url.lastIndexOf("_")) {
                prefixFilePath = prefixFilePath.substring(0, url.lastIndexOf("_"));
            }
            if (1 < pageIndex) {
                return prefixFilePath + '_' + pageIndex + suffixFilePath;
            } else {
                return prefixFilePath + suffixFilePath;
            }

        } else {
            String prefixFilePath = url;
            if (url.lastIndexOf("/") < url.lastIndexOf("_")) {
                prefixFilePath = prefixFilePath.substring(0, url.lastIndexOf("_"));
            }
            if (1 < pageIndex) {
                return prefixFilePath + '_' + pageIndex;
            } else {
                return prefixFilePath;
            }
        }
    }

    @Override
    public boolean needAppToken() {
        return false;
    }

    @Override
    public int minParamtersNumber() {
        return 2;
    }
}
