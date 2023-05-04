package com.czertainly.jobs;

import com.czertainly.api.model.scheduler.SchedulerJobExecutionMessage;
import com.czertainly.constants.JobConstants;
import com.czertainly.dao.entity.SchedulerJobHistory;
import com.czertainly.dao.repository.SchedulerJobHistoryRepository;
import com.czertainly.messaging.RabbitMQProducer;
import com.czertainly.utils.SchedulerUtils;
import org.quartz.Job;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class SchedulerJob implements Job {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerJob.class);

    private SchedulerJobHistoryRepository schedulerJobHistoryRepository;

    private RabbitMQProducer rabbitMQProducer;

    @Override
    public void execute(JobExecutionContext jobExecutionContext) {
        logger.info("SchedulerJob was fired.");
        final String jobName = jobExecutionContext.getJobDetail().getKey().getName();
        SchedulerJobHistory schedulerJobHistory
                = SchedulerUtils.prepareSchedulerJobHistory(jobName, jobExecutionContext.getFireTime());
        schedulerJobHistory = schedulerJobHistoryRepository.save(schedulerJobHistory);

        final String jobClassName = jobExecutionContext.getJobDetail().getJobDataMap().getString(JobConstants.CLASS_TOBE_EXECUTED);
        final SchedulerJobExecutionMessage schedulerExecutionMessage
                = new SchedulerJobExecutionMessage(schedulerJobHistory.getJobHistoryID(), jobName, jobClassName);
        rabbitMQProducer.sendMessage(schedulerExecutionMessage);
        logger.info("Fired job was queued.", schedulerExecutionMessage);
    }

    // SETTERs

    @Autowired
    public void setSchedulerJobHistoryRepository(SchedulerJobHistoryRepository schedulerJobHistoryRepository) {
        this.schedulerJobHistoryRepository = schedulerJobHistoryRepository;
    }

    @Autowired
    public void setRabbitMQProducer(RabbitMQProducer rabbitMQProducer) {
        this.rabbitMQProducer = rabbitMQProducer;
    }
}
