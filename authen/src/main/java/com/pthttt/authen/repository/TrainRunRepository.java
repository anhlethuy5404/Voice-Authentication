package com.pthttt.authen.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pthttt.authen.model.TrainRun;

@Repository
public interface TrainRunRepository extends JpaRepository<TrainRun, Integer> {
    List<TrainRun> findByModelId(Integer modelId);
}
