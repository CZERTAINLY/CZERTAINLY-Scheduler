package com.czertainly.controllers;

import com.czertainly.api.model.scheduler.SchedulerJobDto;
import com.czertainly.api.model.scheduler.SchedulerRequestDto;
import com.czertainly.api.model.scheduler.SchedulerResponseDto;
import com.czertainly.api.model.scheduler.SchedulerStatus;
import com.czertainly.constants.JobConstants;
import com.czertainly.utils.SchedulerUtils;
import io.swagger.v3.oas.annotations.Parameter;
import org.quartz.*;
import org.quartz.impl.matchers.GroupMatcher;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.SchedulerFactoryBean;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;


@RestController
@RequestMapping("/v1/scheduler")
public class SchedulerController {

    private static final Logger logger = LoggerFactory.getLogger(SchedulerController.class);

    private Scheduler scheduler;

    @RequestMapping(path = "/create", method = RequestMethod.POST, consumes = {"application/json"}, produces = {"application/json"})
    public SchedulerResponseDto createNewJob(@RequestBody SchedulerRequestDto schedulerDto) {
        final SchedulerJobDto schedulerDetail = schedulerDto.getSchedulerJob();
        try {
            if (scheduler.checkExists(new JobKey(schedulerDetail.getJobName(), JobConstants.GROUP_NAME))) {
                logger.info("Job {} already exists.", schedulerDto.getSchedulerJob().getJobName());
                return new SchedulerResponseDto(SchedulerStatus.OK, schedulerDetail.getJobName());
            }

            logger.info("Scheduling new job withe name {}", schedulerDetail.getJobName());
            final JobDetail jobDetail
                    = SchedulerUtils.prepareJobDetail(schedulerDetail.getJobName(), schedulerDetail.getClassNameToBeExecuted());
            final Trigger jobTrigger
                    = SchedulerUtils.prepareTrigger(schedulerDetail.getJobName(), schedulerDetail.getCronExpression());
            scheduler.scheduleJob(jobDetail, jobTrigger);
            logger.info("Job {} scheduled with by {}", schedulerDetail.getJobName(), schedulerDetail.getCronExpression());
        } catch (SchedulerException e) {
            logger.error("Unable to schedule job {}", schedulerDetail.getJobName(), e.getMessage());
            return new SchedulerResponseDto(SchedulerStatus.ERROR, schedulerDetail.getJobName());
        }
        return new SchedulerResponseDto(SchedulerStatus.OK, schedulerDetail.getJobName());
    }

    @GetMapping(path = "/update")
    public SchedulerResponseDto updateJob(@RequestBody final SchedulerRequestDto schedulerDto) {
        final SchedulerJobDto schedulerDetail = schedulerDto.getSchedulerJob();
        logger.info("Updating job with name {}", schedulerDetail.getJobName());
        if (deleteJob(schedulerDetail.getJobName())) {
            return createNewJob(schedulerDto);
        } else {
            return new SchedulerResponseDto(SchedulerStatus.ERROR, schedulerDetail.getJobName());
        }
    }

    @GetMapping(path = "/delete")
    public boolean deleteJob(@Parameter(description = "Job name") @PathVariable final String jobName) {
        logger.info("Delete/Unregister job with name {}", jobName);
        try {
            scheduler.unscheduleJob(new TriggerKey(jobName + JobConstants.JOB_TRIGGER_SUFFIX));
            scheduler.deleteJob(new JobKey(jobName));
            logger.info("Job {} was unregistered.", jobName);
            return true;
        } catch (SchedulerException e) {
            logger.error("Unable to unregister job {}" , jobName);
        }
        return false;
    }

    @GetMapping(path = "/list")
    public SchedulerResponseDto listJobs() {
        logger.info("Retrieve list of registered jobs.");
        final List<SchedulerJobDto> schedulerDetailList = new ArrayList<>();
        try {
            for (final JobKey jobKey : scheduler.getJobKeys(GroupMatcher.jobGroupEquals(JobConstants.GROUP_NAME))) {
                final JobDetail jobDetail = scheduler.getJobDetail(jobKey);
                final CronTrigger trigger = (CronTrigger) scheduler.getTrigger(new TriggerKey(jobKey.getName() + JobConstants.JOB_TRIGGER_SUFFIX));
                schedulerDetailList
                        .add(new SchedulerJobDto(jobKey.getName(), trigger.getCronExpression(), jobDetail.getJobDataMap().getString(JobConstants.CLASS_TOBE_EXECUTED)));
            }
        } catch (SchedulerException e) {
            logger.error("Unable to retrieve list of registered jobs.", e.getMessage());
            return new SchedulerResponseDto(SchedulerStatus.ERROR);
        }

        final SchedulerResponseDto schedulerResponseDto = new SchedulerResponseDto(SchedulerStatus.OK);
        schedulerResponseDto.setSchedulerJobList(schedulerDetailList);
        return schedulerResponseDto;
    }

    @GetMapping(path = "/enable")
    public SchedulerResponseDto enableJob(@Parameter(description = "Job name") @PathVariable String jobName) {
        logger.info("Enabling job with name {}", jobName);
        try {
            scheduler.resumeJob(new JobKey(jobName));
            logger.info("Job {} was resumed.", jobName);
        } catch (SchedulerException e) {
            logger.error("Unable to resume job {}", jobName, e.getMessage());
            return new SchedulerResponseDto(SchedulerStatus.ERROR, jobName);
        }
        return new SchedulerResponseDto(SchedulerStatus.OK, jobName);
    }

    @GetMapping(path = "/disable")
    public SchedulerResponseDto disableJob(@Parameter(description = "Job name") @PathVariable String jobName) {
        logger.info("Disabling job with name {}", jobName);
        try {
            scheduler.pauseJob(new JobKey(jobName));
            logger.info("Job {} was paused.", jobName);
        } catch (SchedulerException e) {
            logger.error("Unable to pause job {}", jobName, e.getMessage());
            return new SchedulerResponseDto(SchedulerStatus.ERROR, jobName);
        }
        return new SchedulerResponseDto(SchedulerStatus.OK, jobName);
    }

    // Setters

    @Autowired
    public void setSchedulerFactoryBean(SchedulerFactoryBean schedulerFactoryBean) {
        this.scheduler = schedulerFactoryBean.getScheduler();
    }

}
