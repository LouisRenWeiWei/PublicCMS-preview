package com.publiccms.common.copyright;

import static com.publiccms.common.tools.LicenseUtils.readLicense;
import static com.publiccms.common.tools.LicenseUtils.verifyLicense;
import static com.publiccms.common.tools.IpUtils.isIp;
import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.lang3.ArrayUtils.contains;
import static org.apache.commons.lang3.StringUtils.split;
import static com.publiccms.common.constants.CommonConstants.PUBLIC_KEY;

import java.io.File;
import java.io.IOException;

import com.publiccms.common.base.Base;
import com.publiccms.common.copyright.Copyright;
import com.publiccms.common.copyright.License;

/**
 *
 * CmsCopyright
 *
 */
public class CmsCopyright implements Copyright, Base {
    private long lastModify = 0L;
    private License license;

    @Override
    public boolean verify(String licenseFilePath) {
        License license = getLicense(licenseFilePath);
        return verifyLicense(PUBLIC_KEY, license);
    }

    @Override
    public boolean verify(String licenseFilePath, String domain) {
        License license = getLicense(licenseFilePath);
        return verifyLicense(PUBLIC_KEY, license) && verifyDomain(domain, license.getDomain());
    }

    @Override
    public License getLicense(String licenseFilePath) {
        if (null != licenseFilePath) {
            File licenseFile = new File(licenseFilePath);
            if (null == license || lastModify != licenseFile.lastModified()) {
                try {
                    String licenseText = readFileToString(licenseFile, DEFAULT_CHARSET);
                    license = readLicense(licenseText);
                    lastModify = licenseFile.lastModified();
                } catch (IOException e) {
                }
            }
        }
        return license;
    }

    private boolean verifyDomain(String domain, String licenseDomain) {
        if ("*".equals(licenseDomain) || isIp(domain) || domain.toLowerCase().startsWith("dev.")
                || -1 < domain.toLowerCase().indexOf(".dev.") || "localhost".equals(domain)) {
            return true;
        } else {
            String[] licenseDomains = split(licenseDomain, ",");
            int index;
            while (0 < (index = domain.indexOf(DOT))) {
                if (contains(licenseDomains, domain)) {
                    return true;
                } else {
                    domain = domain.substring(index + 1);
                }
            }
        }
        return false;
    }
}
