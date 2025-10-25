package com.pthttt.authen.repository;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pthttt.authen.model.Voice;


public interface VoiceRepository extends JpaRepository<Voice, Integer> {
    Voice findById(int id);
    List<Voice> findByUserId(int userId);
}
