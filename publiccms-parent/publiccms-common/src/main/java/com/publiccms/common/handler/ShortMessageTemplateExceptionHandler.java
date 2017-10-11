package com.publiccms.common.handler;

import static org.apache.commons.logging.LogFactory.getLog;

import java.io.IOException;
import java.io.Writer;

import org.apache.commons.logging.Log;

import freemarker.core.Environment;
import freemarker.template.TemplateException;
import freemarker.template.TemplateExceptionHandler;

/**
 *
 * ShortMessageTemplateExceptionHandler
 * 
 */
public class ShortMessageTemplateExceptionHandler implements TemplateExceptionHandler {
    protected final Log log = getLog(getClass());

    @Override
    public void handleTemplateException(TemplateException templateexception, Environment environment, Writer writer)
            throws TemplateException {
        try {
            String code = templateexception.getFTLInstructionStack();
            if (null != code && code.indexOf("Failed at: ") > 0 && code.indexOf("  [") > 0) {
                writer.write("error:" + code.substring(code.indexOf("Failed at: ") + 11, code.indexOf("  [")));
            } else {
                writer.write("[some errors occurred!]");
            }
        } catch (IOException e) {
            log.error(environment.getCurrentTemplate().getSourceName(), e);
        }
    }

}
