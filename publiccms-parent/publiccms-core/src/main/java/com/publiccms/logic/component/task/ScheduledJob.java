package com.publiccms.logic.component.task;

import static com.publiccms.common.tools.FreeMarkerUtils.generateStringByFile;
import static com.publiccms.common.base.AbstractFreemarkerView.exposeSite;
import static com.publiccms.logic.component.BeanComponent.getLogTaskService;
import static com.publiccms.logic.component.BeanComponent.getScheduledTask;
import static com.publiccms.logic.component.BeanComponent.getSiteService;
import static com.publiccms.logic.component.BeanComponent.getSysTaskService;
import static com.publiccms.logic.component.BeanComponent.getTemplateComponent;
import static com.publiccms.logic.component.site.SiteComponent.getFullFileName;

import java.io.IOException;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

import com.publiccms.entities.log.LogTask;
import com.publiccms.entities.sys.SysSite;
import com.publiccms.entities.sys.SysTask;
import org.quartz.JobExecutionContext;
import org.quartz.JobExecutionException;
import org.springframework.scheduling.quartz.QuartzJobBean;

import freemarker.template.TemplateException;

/**
 * 
 * ScheduledJob 任务计划实现类
 *
 */
public class ScheduledJob extends QuartzJobBean {
    private static String[] ignoreProperties = new String[] { "id", "begintime", "taskId", "siteId" };

    @Override
    public void executeInternal(JobExecutionContext context) throws JobExecutionException {
        Integer taskId = (Integer) context.getJobDetail().getJobDataMap().get(ScheduledTask.ID);
        SysTask task = getSysTaskService().getEntity(taskId);
        if (null != task) {
            if (ScheduledTask.TASK_STATUS_READY == task.getStatus() && getSysTaskService().updateStatusToRunning(task.getId())) {
                LogTask entity = new LogTask(task.getSiteId(), task.getId(), new Date(), false);
                getLogTaskService().save(entity);
                boolean success = false;
                String result;
                try {
                    success = true;
                    Map<String, Object> map = new HashMap<>();
                    map.put("task", task);
                    SysSite site = getSiteService().getEntity(task.getSiteId());
                    exposeSite(map, site);
                    String fulllPath = getFullFileName(site, task.getFilePath());
                    result = generateStringByFile(fulllPath, getTemplateComponent().getTaskConfiguration(), map);
                } catch (IOException | TemplateException e) {
                    result = e.getMessage();
                }
                entity.setEndtime(new Date());
                entity.setSuccess(success);
                entity.setResult(result);
                getLogTaskService().update(entity.getId(), entity, ignoreProperties);
                getSysTaskService().updateStatus(task.getId(), ScheduledTask.TASK_STATUS_READY);

            }
        } else {
            getScheduledTask().delete(taskId);
        }
    }
}