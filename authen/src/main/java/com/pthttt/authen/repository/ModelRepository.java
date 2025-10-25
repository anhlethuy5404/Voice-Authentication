package com.pthttt.authen.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.pthttt.authen.model.Model;

@Repository
public interface ModelRepository extends JpaRepository<Model, Integer> {
    
}
