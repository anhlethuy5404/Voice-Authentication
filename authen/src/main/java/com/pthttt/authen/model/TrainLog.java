package com.pthttt.authen.model;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "train_log")
public class TrainLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int epoch;
    private float trainAccuracy;
    private float valAccuracy;
    private float trainLoss;
    private float valLoss;

    @ManyToOne
    @JoinColumn(name = "trainRun_id")
    private TrainRun trainRun;

    public TrainLog() {
    }
    public TrainLog(int epoch, float trainAccuracy, float valAccuracy, float trainLoss, float valLoss) {
        this.epoch = epoch;
        this.trainAccuracy = trainAccuracy;
        this.valAccuracy = valAccuracy;
        this.trainLoss = trainLoss;
        this.valLoss = valLoss;
    }
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public int getEpoch() {
        return epoch;
    }
    public void setEpoch(int epoch) {
        this.epoch = epoch;
    }
    public float getTrainAccuracy() {
        return trainAccuracy;
    }
    public void setTrainAccuracy(float trainAccuracy) {
        this.trainAccuracy = trainAccuracy;
    }

    public float getValAccuracy() {
        return valAccuracy;
    }
    public void setValAccuracy(float valAccuracy) {
        this.valAccuracy = valAccuracy;
    }
    public float getTrainLoss() {
        return trainLoss;
    }
    public void setTrainLoss(float trainLoss) {
        this.trainLoss = trainLoss;
    }
    public float getValLoss() {
        return valLoss;
    }
    public void setValLoss(float valLoss) {
        this.valLoss = valLoss;
    }
    public TrainRun getTrainRun() {
        return trainRun;
    }
    public void setTrainRun(TrainRun trainRun) {
        this.trainRun = trainRun;
    }
    
}
