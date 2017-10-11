package com.publiccms.views.directive.tools;

import static com.publiccms.common.tools.CommonUtils.notEmpty;
import static com.publiccms.common.tools.FreeMarkerUtils.generateStringByFile;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

import com.publiccms.common.base.AbstractTemplateDirective;
import com.publiccms.entities.sys.SysSite;
import com.publiccms.logic.component.site.EmailComponent;
import com.publiccms.logic.component.template.TemplateComponent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.publiccms.common.handler.RenderHandler;

/**
 *
 * SendEmailDirective
 * 
 */
@Component
public class SendEmailDirective extends AbstractTemplateDirective {

    @Override
    public void execute(RenderHandler handler) throws IOException, Exception {
        String email = handler.getString("email");
        String title = handler.getString("title");
        String templatePath = handler.getString("templatePath");
        if (notEmpty(email) && notEmpty(title)) {
            SysSite site = getSite(handler);
            String content = handler.getString("content");
            if (notEmpty(templatePath)) {
                Map<String, Object> model = new HashMap<>();
                expose(handler, model);
                content = generateStringByFile(siteComponent.getWebTemplateFilePath(site, templatePath),
                        templateComponent.getWebConfiguration(), model);
            }
            if (notEmpty(content)) {
                handler.put("result", emailComponent.sendHtml(site.getId(), email, title, content)).render();
            }
        }
    }

    @Override
    public boolean needAppToken() {
        return true;
    }

    @Autowired
    private EmailComponent emailComponent;
    @Autowired
    private TemplateComponent templateComponent;
}
