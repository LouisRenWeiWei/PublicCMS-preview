package com.publiccms.views.method.tools;

import static com.publiccms.common.tools.LicenseUtils.getLicenseDate;
import static com.publiccms.common.tools.VerificationUtils.base64Encode;

import java.util.List;

import com.publiccms.common.constants.CmsVersion;
import org.springframework.stereotype.Component;

import com.publiccms.common.base.BaseMethod;

import freemarker.template.TemplateModelException;

/**
 *
 * GetLicenseDateMethod
 * 
 */
@Component
public class GetLicenseDataMethod extends BaseMethod {

    @Override
    public Object exec(@SuppressWarnings("rawtypes") List arguments) throws TemplateModelException {
        return base64Encode(getLicenseDate(CmsVersion.getLicense()));
    }

    @Override
    public boolean needAppToken() {
        return true;
    }

    @Override
    public int minParamtersNumber() {
        return 0;
    }
}
