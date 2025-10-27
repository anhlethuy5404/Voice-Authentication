package com.pthttt.authen.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pthttt.authen.model.Vector;

@Repository
public interface VectorRepository extends JpaRepository<Vector, Integer> {
    List<Vector> findByTrainRunId(Integer trainRunId);
    List<Vector> findByVoiceId(Integer voiceId);
}
