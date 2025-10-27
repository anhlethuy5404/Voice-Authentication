package com.pthttt.authen.model;

import java.time.LocalDateTime;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "auth_log")
public class AuthLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String isRealVoice;
    private float similarity;
    @Column(name = "`rank`")
    private int rank;
    private int userMatch;
    private String checkType; //deepfake / embedding
    private LocalDateTime createdAt;

    @ManyToOne
    @JoinColumn(name = "vector_id")
    private Vector vector;

    public AuthLog() {
    }
    public AuthLog(int id, String isRealVoice, float similarity, String result, int rank, int userMatch,
            String checkType, LocalDateTime createdAt) {
        this.id = id;
        this.isRealVoice = isRealVoice;
        this.similarity = similarity;
        this.rank = rank;
        this.userMatch = userMatch;
        this.checkType = checkType;
        this.createdAt = createdAt;
    }
    public AuthLog(float similarity, String result, int rank, int userMatch, String checkType, LocalDateTime createdAt, Vector vector) {
        this.similarity = similarity;
        this.rank = rank;
        this.userMatch = userMatch;
        this.checkType = checkType;
        this.createdAt = createdAt;
        this.vector = vector;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getIsRealVoice() {
        return isRealVoice;
    }

    public void setIsRealVoice(String isRealVoice) {
        this.isRealVoice = isRealVoice;
    }
    public float getSimilarity() {
        return similarity;
    }

    public void setSimilarity(float similarity) {
        this.similarity = similarity;
    }
    public int getRank() {
        return rank;
    }
    public void setRank(int rank) {
        this.rank = rank;
    }
    public Vector getVector() {
        return vector;
    }
    public void setVector(Vector vector) {
        this.vector = vector;
    }
    public int getUserMatch() {
        return userMatch;
    }
    public void setUserMatch(int userMatch) {
        this.userMatch = userMatch;
    }
    public String getCheckType() {
        return checkType;
    }
    public void setCheckType(String checkType) {
        this.checkType = checkType;
    }
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
}
