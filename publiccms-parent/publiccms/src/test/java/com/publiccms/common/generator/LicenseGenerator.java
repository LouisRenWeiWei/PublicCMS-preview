package com.publiccms.common.generator;

import static com.publiccms.common.tools.DateFormatUtils.getDateFormat;
import static com.publiccms.common.tools.LicenseUtils.DATE_FORMAT_STRING;
import static com.publiccms.common.tools.LicenseUtils.generateSignaturer;
import static com.publiccms.common.tools.LicenseUtils.readLicense;
import static com.publiccms.common.tools.LicenseUtils.verifyLicense;
import static com.publiccms.common.tools.LicenseUtils.writeLicense;
import static com.publiccms.common.tools.VerificationUtils.base64Encode;
import static com.publiccms.common.tools.VerificationUtils.sha2Encode;
import static org.apache.commons.lang3.time.DateUtils.addMonths;
import static com.publiccms.common.constants.CommonConstants.PUBLIC_KEY;

import java.security.KeyPair;
import java.security.SecureRandom;
import java.util.Date;
import java.util.Scanner;

import com.publiccms.common.base.Base;
import com.publiccms.common.copyright.License;
import com.publiccms.common.tools.VerificationUtils;

/**
 *
 * LicenseGenerator
 * 
 */
public class LicenseGenerator implements Base {

    /**
     * @param arg
     * @throws Throwable
     */
    public static void main(String[] arg) throws Throwable {
        Scanner sc = new Scanner(System.in);
        System.out.println("Please Enter a password:");
        SecureRandom secrand = new SecureRandom();
        secrand.setSeed(sha2Encode(sc.nextLine()).getBytes(DEFAULT_CHARSET)); // 初始化随机产生器
        KeyPair keyPair = VerificationUtils.generateKeyPair(1024, secrand);
        String publicKey = base64Encode(keyPair.getPublic().getEncoded());
        if (PUBLIC_KEY.equals(publicKey)) {
            License license = new License();
            license.setVersion("1.0");
            license.setAuthorization("免费体验");
            license.setOrganization("所有用户");
            license.setIssue("PublicCMS官网");
            license.setDomain("*");
            license.setStartDate(getDateFormat(DATE_FORMAT_STRING).format(new Date()));
            license.setEndDate(getDateFormat(DATE_FORMAT_STRING).format(addMonths(new Date(), 3)));
            license.setSignaturer(generateSignaturer(keyPair.getPrivate().getEncoded(), license));
            String s2 = generateSignaturer(keyPair.getPrivate().getEncoded(), license);
            System.out.println(license.getSignaturer().equals(s2));
            String licenseText = writeLicense(license);
            System.out.println("----------PublicCMS License-----------");
            System.out.println(licenseText);
            System.out.println("----------PublicCMS License-----------");
            License l = readLicense(licenseText);
            System.out.println(verifyLicense(PUBLIC_KEY, l));
        } else {
            System.out.println(publicKey);
        }
        sc.close();
    }
}
