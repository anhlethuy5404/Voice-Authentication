package com.pthttt.authen.repository;

import com.pthttt.authen.model.Model;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelRepository extends JpaRepository<Model, Integer> {

    @Query("SELECT m FROM Model m JOIN m.trainRuns tr WHERE tr.id = :trainRunId")
    Model findByTrainRunId(@Param("trainRunId") int trainRunId);

    Model getModelById(int modelId);
}
