package com.pthttt.authen.repository;

import com.pthttt.authen.model.TrainRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TrainRunRepository extends JpaRepository<TrainRun, Integer> {
    TrainRun findByModelVoiceId(Integer modelVoiceId);
}
