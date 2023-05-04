package com.czertainly.dao.entity;

import com.czertainly.api.model.scheduler.SchedulerJobExecutionStatus;
import jakarta.persistence.*;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.Date;

@Getter
@Setter
@NoArgsConstructor
@Entity
public class SchedulerJobHistory {

    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "scheduler_history_seq")
    @SequenceGenerator(name = "scheduler_history_seq", sequenceName = "scheduler_history_id_seq", allocationSize = 1)
    private Long jobHistoryID;

    private String jobName;

    private Date jobExecution;

    @Enumerated(EnumType.STRING)
    private SchedulerJobExecutionStatus schedulerExecutionStatus;


}
