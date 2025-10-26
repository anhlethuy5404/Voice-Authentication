package com.pthttt.authen.model;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "hyperparameter")
public class Hyperparameter {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String name;
    private String value;
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "trainRun_id")
    private TrainRun trainRun;

    public Hyperparameter() {
    }

    public Hyperparameter(String name, String value) {
        this.name = name;
        this.value = value;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    public TrainRun getTrainRun() {
        return trainRun;
    }
    public void setTrainRun(TrainRun trainRun) {
        this.trainRun = trainRun;
    }
    
}
