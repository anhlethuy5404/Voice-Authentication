package com.pthttt.authen.repository;

import com.pthttt.authen.model.TrainLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainLogRepository extends JpaRepository<TrainLog, Integer> {
    List<TrainLog> findByTrainRunId(int trainRunId);
}
