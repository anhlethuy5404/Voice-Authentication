package com.pthttt.authen.repository;

import com.pthttt.authen.model.Score;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScoreRepository extends JpaRepository<Score, Integer> {
    Score findByTrainRunId(int trainRunId);
}
