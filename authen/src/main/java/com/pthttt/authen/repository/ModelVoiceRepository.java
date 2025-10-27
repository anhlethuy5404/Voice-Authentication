package com.pthttt.authen.repository;

import com.pthttt.authen.model.Vector;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface ModelVoiceRepository extends JpaRepository<Vector, Integer> {
    List<Vector> getByVoiceId(int voiceId);
}
