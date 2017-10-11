package com.publiccms.views.directive.tools;

import static com.publiccms.common.tools.CommonUtils.notEmpty;
import static com.publiccms.common.tools.FreeMarkerUtils.generateStringByString;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.publiccms.common.base.AbstractTemplateDirective;
import com.publiccms.logic.component.template.TemplateComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.publiccms.common.handler.RenderHandler;

import freemarker.template.TemplateException;

/**
 *
 * TemplateResultDirective
 * 
 */
@Component
public class TemplateResultDirective extends AbstractTemplateDirective {

    @Override
    public void execute(RenderHandler handler) throws IOException, Exception {
        String content = handler.getString("templateContent");
        if (notEmpty(content)) {
            try {
                content = "<#attempt>" + content + "<#recover>${.error!}</#attempt>";
                Map<String, Object> model = new HashMap<>();
                expose(handler, model);
                model.remove("templateContent");
                handler.print(generateStringByString(content, templateComponent.getWebConfiguration(), model));
            } catch (IOException | TemplateException e) {
                handler.print(e.getMessage());
            }
        }
    }

    @Override
    public boolean needAppToken() {
        return true;
    }

    @Autowired
    private TemplateComponent templateComponent;

}
