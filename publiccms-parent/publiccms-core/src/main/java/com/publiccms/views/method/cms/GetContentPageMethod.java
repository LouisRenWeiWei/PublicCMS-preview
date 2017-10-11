package com.publiccms.views.method.cms;

import static com.publiccms.common.tools.CommonUtils.notEmpty;
import static org.apache.commons.lang3.StringUtils.splitByWholeSeparator;
import static com.publiccms.common.constants.CommonConstants.getCkeditorPageBreakTag;
import static com.publiccms.common.constants.CommonConstants.getUeditorPageBreakTag;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.stereotype.Component;

import com.publiccms.common.base.BaseMethod;
import com.publiccms.common.handler.PageHandler;

import freemarker.template.TemplateModelException;

/**
 *
 * GetContentAttributesMethod
 * 
 */
@Component
public class GetContentPageMethod extends BaseMethod {

    @SuppressWarnings("unchecked")
    @Override
    public Object exec(@SuppressWarnings("rawtypes") List arguments) throws TemplateModelException {
        String text = getString(0, arguments);
        Integer pageIndex = getInteger(1, arguments);
        if (notEmpty(text)) {
            String pageBreakTag = null;
            if (-1 < text.indexOf(getCkeditorPageBreakTag())) {
                pageBreakTag = getCkeditorPageBreakTag();
            } else {
                pageBreakTag = getUeditorPageBreakTag();
            }
            String[] texts = splitByWholeSeparator(text, pageBreakTag);
            PageHandler page = new PageHandler(pageIndex, 1, texts.length, null);
            Map<String, Object> resultMap = new HashMap<>();
            resultMap.put("page", page);
            resultMap.put("text", texts[page.getPageIndex() - 1]);
            return resultMap;
        }
        return null;
    }

    @Override
    public int minParamtersNumber() {
        return 1;
    }

    @Override
    public boolean needAppToken() {
        return false;
    }
}
