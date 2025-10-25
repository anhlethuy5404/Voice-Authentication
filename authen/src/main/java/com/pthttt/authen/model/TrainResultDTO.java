package com.pthttt.authen.model;

import java.time.LocalDate;
import java.time.LocalDateTime;

public class TrainResultDTO {
    private int version;
    private String filePath;
    private LocalDateTime CreateAt;
    private float accuracy;
    private float precision;
    private float recall;
    private float f1;

    public TrainResultDTO() {
    }

    public TrainResultDTO(int version, String filePath, LocalDateTime CreateAt, float accuracy, float precision, float recall, float f1) {
        this.version = version;
        this.filePath = filePath;
        this.CreateAt = CreateAt;
        this.accuracy = accuracy;
        this.precision = precision;
        this.recall = recall;
        this.f1 = f1;
    }
    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getCreateAt() {
        return CreateAt;
    }

    public void setCreateAt(LocalDateTime createAt) {
        CreateAt = createAt;
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
}
