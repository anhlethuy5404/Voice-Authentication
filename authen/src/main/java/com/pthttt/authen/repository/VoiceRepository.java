package com.pthttt.authen.repository;

import com.pthttt.authen.model.Voice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import com.pthttt.authen.model.Voice;
@Repository
public interface VoiceRepository extends JpaRepository<Voice, Integer> {
    Voice findById(int id);
    List<Voice> findByUserId(int userId);
}
