package com.pthttt.authen.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.pthttt.authen.model.User;
import com.pthttt.authen.model.Voice;

public interface VoiceRepository extends JpaRepository<Voice, Integer> {
    long countByUser(User user);
}
