package com.pthttt.authen.repository;

import com.pthttt.authen.model.Voice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface VoiceRepository extends JpaRepository<Voice, Integer> {
    List<Voice> findByUserId(Integer userId);
}
