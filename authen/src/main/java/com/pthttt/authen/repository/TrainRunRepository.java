package com.pthttt.authen.repository;

import com.pthttt.authen.model.TrainRun;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface TrainRunRepository extends JpaRepository<TrainRun, Integer> {
    TrainRun findByModelVoiceId(int modelVoiceId);
    List<TrainRun> findByModelId(int modelId);

    @Query("SELECT MAX(Tr.version) FROM TrainRun Tr WHERE Tr.model.id = :modelId")
    int findMaxVersionByModelId(int modelId);
}
