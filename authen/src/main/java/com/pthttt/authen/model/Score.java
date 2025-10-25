package com.pthttt.authen.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "score")
public class Score {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private float accuracy;
    @Column(name = "`precision`")
    private float precision;
    private float recall;
    private float f1;

    @OneToOne
    @JoinColumn(name = "trainRun_id")
    private TrainRun trainRun;

    public Score() {
    }

    public Score(float accuracy, float precision, float recall, float f1) {
        this.accuracy = accuracy;
        this.precision = precision;
        this.recall = recall;
        this.f1 = f1;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public float getAccuracy() {
        return accuracy;
    }

    public void setAccuracy(float accuracy) {
        this.accuracy = accuracy;
    }

    public float getPrecision() {
        return precision;
    }

    public void setPrecision(float precision) {
        this.precision = precision;
    }

    public float getRecall() {
        return recall;
    }

    public void setRecall(float recall) {
        this.recall = recall;
    }

    public float getF1() {
        return f1;
    }

    public void setF1(float f1) {
        this.f1 = f1;
    }
    public TrainRun getTrainRun() {
        return trainRun;
    }

    public void setTrainRun(TrainRun trainRun) {
        this.trainRun = trainRun;
    }
}