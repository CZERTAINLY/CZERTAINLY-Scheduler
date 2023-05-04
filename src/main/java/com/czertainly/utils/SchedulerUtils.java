package com.czertainly.utils;

import com.czertainly.api.model.scheduler.SchedulerExecutionStatus;
import com.czertainly.constants.JobConstants;
import com.czertainly.dao.entity.SchedulerJobHistory;
import com.czertainly.jobs.SchedulerJob;
import org.quartz.*;

import java.util.Date;

public class SchedulerUtils {

    public static JobDetail prepareJobDetail(final String jobName, final String className) {
        return JobBuilder.newJob(SchedulerJob.class)
                .withIdentity(jobName, JobConstants.GROUP_NAME)
                .usingJobData(JobConstants.CLASS_TOBE_EXECUTED, className)
                .build();
    }

    public static Trigger prepareTrigger(final String jobName, final String cronExpression) {
        return TriggerBuilder.newTrigger()
                .withIdentity(jobName + JobConstants.JOB_TRIGGER_SUFFIX, JobConstants.GROUP_NAME)
                .withSchedule(CronScheduleBuilder.cronSchedule(cronExpression))
                .build();
    }

    public static SchedulerJobHistory prepareSchedulerJobHistory(final String jobName, final Date firedTime) {
        final SchedulerJobHistory schedulerJobHistory = new SchedulerJobHistory();
        schedulerJobHistory.setJobName(jobName);
        schedulerJobHistory.setJobExecution(firedTime);
        schedulerJobHistory.setSchedulerExecutionStatus(SchedulerExecutionStatus.UNKNOWN);
        return schedulerJobHistory;
    }


}
