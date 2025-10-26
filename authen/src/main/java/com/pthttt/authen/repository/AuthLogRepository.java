package com.pthttt.authen.repository;

import com.pthttt.authen.model.AuthLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AuthLogRepository extends JpaRepository<AuthLog, Integer> {
}
