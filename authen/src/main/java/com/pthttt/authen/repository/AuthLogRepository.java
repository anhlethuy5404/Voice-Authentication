package com.pthttt.authen.repository;

import com.pthttt.authen.model.AuthLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AuthLogRepository extends JpaRepository<AuthLog, Integer> {
    List<AuthLog> findByVectorId(int vectorId);
}
