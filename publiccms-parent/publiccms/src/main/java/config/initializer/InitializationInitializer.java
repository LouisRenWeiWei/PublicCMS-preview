package config.initializer;

import static org.apache.commons.io.FileUtils.readFileToString;
import static org.apache.commons.logging.LogFactory.getLog;
import static com.publiccms.common.constants.CommonConstants.CMS_CONFIG_FILE;
import static com.publiccms.common.constants.CommonConstants.CMS_FILEPATH;
import static com.publiccms.common.constants.CommonConstants.INSTALL_LOCK_FILENAME;
import static com.publiccms.common.database.CmsDataSource.DATABASE_CONFIG_FILENAME;
import static com.publiccms.common.servlet.InstallServlet.STEP_CHECKDATABASE;
import static com.publiccms.common.tools.DatabaseUtils.getConnection;
import static org.springframework.core.io.support.PropertiesLoaderUtils.loadAllProperties;

import java.beans.PropertyVetoException;
import java.io.File;
import java.io.IOException;
import java.net.Authenticator;
import java.sql.Connection;
import java.sql.SQLException;
import java.util.Properties;

import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRegistration.Dynamic;

import org.apache.commons.logging.Log;
import com.publiccms.common.constants.CmsVersion;
import com.publiccms.common.servlet.InstallHttpRequestHandler;
import com.publiccms.common.servlet.InstallServlet;
import org.springframework.web.HttpRequestHandler;
import org.springframework.web.WebApplicationInitializer;
import org.springframework.web.util.IntrospectorCleanupListener;

import com.publiccms.common.base.Base;
import com.publiccms.common.proxy.UsernamePasswordAuthenticator;

/**
 *
 * InstallationInitializer
 *
 */
public class InitializationInitializer implements WebApplicationInitializer, Base {
    protected final Log log = getLog(getClass());
    /**
     * 安装Servlet映射路径
     */
    public final static String INSTALL_SERVLET_MAPPING = "/install/";
    /**
     * 安装跳转处理器
     */
    public final static HttpRequestHandler INSTALL_HTTPREQUEST_HANDLER = new InstallHttpRequestHandler(INSTALL_SERVLET_MAPPING);

    @Override
    public void onStartup(ServletContext servletcontext) throws ServletException {
        servletcontext.addListener(IntrospectorCleanupListener.class);

        Properties config = null;
        Connection connection = null;
        try {
            config = loadAllProperties(CMS_CONFIG_FILE);
            // 检查路径是否存在- 2017-06-17
            checkFilePath(servletcontext, config.getProperty("cms.filePath"));
            initProxy(config);
            File file = new File(CMS_FILEPATH + INSTALL_LOCK_FILENAME);
            if (file.exists()) {
                connection = getConnection(CMS_FILEPATH + DATABASE_CONFIG_FILENAME);
                connection.close();
                String version = readFileToString(file, DEFAULT_CHARSET);
                if (CmsVersion.getVersion().equals(version)) {
                    CmsVersion.setInitialized(true);
                    log.info("PublicCMS " + CmsVersion.getVersion() + " will start normally in " + CMS_FILEPATH);
                } else {
                    createInstallServlet(servletcontext, config, STEP_CHECKDATABASE, version);
                    log.warn("PublicCMS " + CmsVersion.getVersion() + " installer will start in " + CMS_FILEPATH
                            + ", please upgrade your database!");
                }
            } else {
                createInstallServlet(servletcontext, config, null, null);
                log.warn("PublicCMS " + CmsVersion.getVersion() + " installer will start in " + CMS_FILEPATH
                        + ", please configure your database information and initialize the database!");
            }
        } catch (PropertyVetoException | SQLException | IOException | ClassNotFoundException e) {
            if (null != connection) {
                try {
                    connection.close();
                } catch (SQLException e1) {
                }
            }
            createInstallServlet(servletcontext, config, null, null);
            log.warn("PublicCMS " + CmsVersion.getVersion() + " installer will start in " + CMS_FILEPATH
                    + ", please modify your database configuration!");
        }
    }

    private void createInstallServlet(ServletContext servletcontext, Properties config, String startStep, String version) {
        Dynamic registration = servletcontext.addServlet("install", new InstallServlet(config, startStep, version));
        registration.setLoadOnStartup(1);
        registration.addMapping(new String[] { INSTALL_SERVLET_MAPPING });
    }

    /**
     * 检查CMS路径变量
     * 
     * @param servletcontext
     * @param defaultPath
     * @throws ServletException
     */
    private void checkFilePath(ServletContext servletcontext, String defaultPath) throws ServletException {
        CMS_FILEPATH = System.getProperty("cms.filePath", defaultPath);
        File cmsDataFolder = new File(CMS_FILEPATH);
        if (!cmsDataFolder.exists()) {
            // 尝试创建
            log.warn("The directory " + CMS_FILEPATH + " does not exist, try to create the directory.");
            try {
                cmsDataFolder.mkdirs();
            } catch (Exception e) {
                CMS_FILEPATH = new File(servletcontext.getRealPath("/"), "cms_filepath_temp").getPath();
                log.warn("the cms.filePath parameter is invalid , " + CMS_FILEPATH + " will be use as the default cms.filepath.");
            }
        }
    }

    /**
     * 代理配置
     * 
     * @param config
     * @throws IOException
     */
    private void initProxy(Properties config) throws IOException {
        if ("true".equalsIgnoreCase(System.getProperty("cms.proxy.enable", config.getProperty("cms.proxy.enable", "false")))) {
            Properties proxyProperties = loadAllProperties(
                    System.getProperty("cms.proxy.configFilePath", config.getProperty("cms.proxy.configFilePath")));
            for (String key : proxyProperties.stringPropertyNames()) {
                System.setProperty(key, proxyProperties.getProperty(key));
            }
            Authenticator.setDefault(new UsernamePasswordAuthenticator(config.getProperty("cms.proxy.userName"),
                    config.getProperty("cms.proxy.password")));
        }
    }
}
