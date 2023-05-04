package com.czertainly.dao.repository;

import com.czertainly.dao.entity.SchedulerJobHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SchedulerJobHistoryRepository extends JpaRepository<SchedulerJobHistory, Long> {


}
