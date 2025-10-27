package com.pthttt.authen.repository;

import com.pthttt.authen.model.Hyperparameter;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface HyperparameterRepository extends JpaRepository<Hyperparameter, Integer> {
    List<Hyperparameter> findByTrainRunId(int trainRunId);
}
