package com.publiccms.views.directive.tools;

import static com.publiccms.common.tools.CommonUtils.notEmpty;
import static com.publiccms.common.tools.VerificationUtils.base64Decode;
import static com.publiccms.common.tools.VerificationUtils.publicKeyVerify;
import static com.publiccms.common.constants.CommonConstants.PUBLIC_KEY;

import java.io.IOException;

import com.publiccms.common.base.AbstractTemplateDirective;
import org.springframework.stereotype.Component;

import com.publiccms.common.handler.RenderHandler;

/**
 * 
 * VersionDirective 技术框架版本指令
 *
 */
@Component
public class LicenseVerifyDirective extends AbstractTemplateDirective {

    @Override
    public void execute(RenderHandler handler) throws IOException, Exception {
        String licenseData = handler.getString("licenseData");
        String signaturer = handler.getString("signaturer");
        handler.put("result", false);
        if (notEmpty(signaturer) && notEmpty(licenseData)) {
            handler.put("result", publicKeyVerify(base64Decode(PUBLIC_KEY), base64Decode(licenseData), base64Decode(signaturer)));
        }
        handler.render();
    }

}
