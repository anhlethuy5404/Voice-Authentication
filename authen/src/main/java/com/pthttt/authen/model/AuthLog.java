package com.pthttt.authen.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

@Entity
@Table(name = "auth_logs")
public class AuthLog {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private String isRealVoice;
    private float similarity;
    private String result;
    @Column(name = "`rank`")
    private int rank;

    @ManyToOne
    @JoinColumn(name = "modelVoice_id")
    private ModelVoice modelVoice;

    public AuthLog() {
    }
    public AuthLog(String isRealVoice, float similarity, String result, int rank) {
        this.isRealVoice = isRealVoice;
        this.similarity = similarity;
        this.result = result;
        this.rank = rank;
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
    public String getResult() {
        return result;
    }
    public void setResult(String result) {
        this.result = result;
    }
    public int getRank() {
        return rank;
    }
    public void setRank(int rank) {
        this.rank = rank;
    }
    public ModelVoice getModelVoice() {
        return modelVoice;
    }
    public void setModelVoice(ModelVoice modelVoice) {
        this.modelVoice = modelVoice;
    }
}
