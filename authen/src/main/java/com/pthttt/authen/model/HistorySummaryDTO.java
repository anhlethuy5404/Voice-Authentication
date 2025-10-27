package com.pthttt.authen.model;

import java.time.LocalDateTime;

public class HistorySummaryDTO {
    private String filePath;
    private String isRealVoice;
    private int rank;
    private String checkType;
    private int userMatch;
    private float similarity;
    private LocalDateTime createdAt;
    private String modelName;
    private int version;

    public HistorySummaryDTO(){};

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getIsRealVoice() {
        return isRealVoice;
    }

    public void setIsRealVoice(String isRealVoice) {
        this.isRealVoice = isRealVoice;
    }

    public int getRank() {
        return rank;
    }

    public void setRank(int rank) {
        this.rank = rank;
    }

    public String getCheckType() {
        return checkType;
    }

    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }

    public int getUserMatch() {
        return userMatch;
    }

    public void setUserMatch(int userMatch) {
        this.userMatch = userMatch;
    }

    public float getSimilarity() {
        return similarity;
    }

    public void setSimilarity(float similarity) {
        this.similarity = similarity;
    }

    public LocalDateTime getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }

    public String getModelName() {
        return modelName;
    }

    public void setModelName(String modelName) {
        this.modelName = modelName;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }
}
