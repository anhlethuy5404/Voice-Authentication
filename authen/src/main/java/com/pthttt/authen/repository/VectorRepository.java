package com.pthttt.authen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pthttt.authen.model.Vector;

@Repository
public interface VectorRepository extends JpaRepository<Vector, Integer> {
}
