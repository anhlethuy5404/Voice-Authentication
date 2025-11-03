package com.pthttt.authen.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import com.pthttt.authen.model.TrainRun;

@Repository
public interface TrainRunRepository extends JpaRepository<TrainRun, Integer> {
    @Query("SELECT t FROM TrainRun t JOIN t.vectors v WHERE v.id = :vectorId")
    TrainRun findByVectorId(@Param("vectorId") int vectorId);

    List<TrainRun> findByModelId(Integer modelId);
    Optional<TrainRun> findById(Integer id);

    @Query("SELECT MAX(Tr.version) FROM TrainRun Tr WHERE Tr.model.id = :modelId")
    int findMaxVersionByModelId(int modelId);

    List<TrainRun> findByType(String type);
}
