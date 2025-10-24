package com.pthttt.authen.repository;

import com.pthttt.authen.model.ModelVoice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ModelVoiceRepository extends JpaRepository<ModelVoice, Integer> {
}
