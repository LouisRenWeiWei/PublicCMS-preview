package com.publiccms.common.tools;

import static com.publiccms.common.tools.VerificationUtils.base64Decode;
import static com.publiccms.common.tools.VerificationUtils.base64Encode;
import static com.publiccms.common.tools.VerificationUtils.privateKeySign;
import static com.publiccms.common.tools.VerificationUtils.publicKeyVerify;
import static com.publiccms.common.tools.DateFormatUtils.getDateFormat;
import static org.apache.commons.lang3.time.DateUtils.addDays;
import static org.apache.commons.lang3.StringUtils.split;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;

import com.publiccms.common.base.Base;
import com.publiccms.common.copyright.License;

public class LicenseUtils implements Base {
    public static final String DATE_FORMAT_STRING = "yyyy-MM-dd";

    public static String writeLicense(License license) {
        try {
            StringWriter sw = new StringWriter();
            BufferedWriter bw = new BufferedWriter(sw);
            for (Entry<String, String> entry : license.entrySet()) {
                bw.append(entry.getKey()).append("=").append(entry.getValue());
                bw.newLine();
            }
            bw.close();
            return sw.getBuffer().toString();
        } catch (IOException e) {
        }
        return null;
    }

    public static License readLicense(String licenseText) {
        License license = new License();
        if (null != licenseText) {
            try {
                BufferedReader br = new BufferedReader(new StringReader(licenseText));
                String temp = null;
                while ((temp = br.readLine()) != null) {
                    String[] values = split(temp, "=", 2);
                    if (values.length == 2) {
                        license.put(values[0], values[1]);
                    }
                }
                br.close();
            } catch (IOException e) {
            }
        }
        return license;
    }

    public static boolean verifyLicense(String publicKey, License license) {
        if (null != license
                && publicKeyVerify(base64Decode(publicKey), getLicenseDate(license), base64Decode(license.getSignaturer()))) {
            Date now = new Date();
            try {
                if (now.after(getDateFormat("yyyy-MM-dd").parse(license.getStartDate()))
                        && now.before(addDays(getDateFormat("yyyy-MM-dd").parse(license.getEndDate()), 1))) {
                    return true;
                }
            } catch (ParseException e) {
            }
        }
        return false;
    }

    public static String generateSignaturer(byte[] privateKey, License license) {
        return base64Encode(privateKeySign(privateKey, getLicenseDate(license)));
    }

    public static byte[] getLicenseDate(License license) {
        StringBuilder sb = new StringBuilder();
        if (null != license) {
            List<String> list = new ArrayList<>(license.keySet());
            Collections.sort(list);
            for (String key : list) {
                if (!License.KEY_SIGNATURER.equals(key)) {
                    sb.append(key).append("=").append(license.get(key)).append(";");
                }
            }
        }
        return sb.toString().getBytes(DEFAULT_CHARSET);
    }
}