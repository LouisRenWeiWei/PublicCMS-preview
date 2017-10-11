package com.publiccms.common.tools;

import static com.publiccms.common.tools.VerificationUtils.base64Decode;
import static com.publiccms.common.tools.VerificationUtils.decrypt;
import static com.publiccms.common.constants.CommonConstants.ENCRYPT_KEY;

import java.beans.PropertyVetoException;
import java.io.IOException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import com.publiccms.common.database.CmsDataSource;

/**
 *
 * DatabaseUtils
 * 
 */
public class DatabaseUtils {

    /**
     * @param databaseConfigFile
     * @return connection
     * @throws SQLException
     * @throws IOException
     * @throws PropertyVetoException
     * @throws ClassNotFoundException
     */
    public static Connection getConnection(String databaseConfigFile)
            throws SQLException, IOException, PropertyVetoException, ClassNotFoundException {
        Properties dbconfigProperties = CmsDataSource.loadDatabaseConfig(databaseConfigFile);
        String driverClassName = dbconfigProperties.getProperty("jdbc.driverClassName");
        String url = dbconfigProperties.getProperty("jdbc.url");
        String userName = dbconfigProperties.getProperty("jdbc.username");
        String password = dbconfigProperties.getProperty("jdbc.password");
        String encryptPassword = dbconfigProperties.getProperty("jdbc.encryptPassword");
        if (null != encryptPassword) {
            password = decrypt(base64Decode(encryptPassword), ENCRYPT_KEY);
        }
        return getConnection(driverClassName, url, userName, password);
    }

    /**
     * @param driverClassName
     * @param url
     * @param userName
     * @param password
     * @return connection
     * @throws ClassNotFoundException
     * @throws SQLException
     */
    public static Connection getConnection(String driverClassName, String url, String userName, String password)
            throws ClassNotFoundException, SQLException {
        Class.forName(driverClassName);
        return DriverManager.getConnection(url, userName, password);
    }
}
