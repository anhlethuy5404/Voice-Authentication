package com.pthttt.authen.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;

@Entity
@Table(name = "model_voice")
public class ModelVoice {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private Date createdAt;
    private List<Float> embeddingVector;

    @ManyToOne
    @JoinColumn(name = "voice_id")
    private Voice voice;

    @ManyToOne
    @JoinColumn(name = "trainRun_id")
    private TrainRun trainRun;

    @OneToMany(mappedBy = "modelVoice")
    private List<AuthLog> authLogs;

    public ModelVoice() {
    }
    
    public ModelVoice(Date createdAt, List<Float> embeddingVector) {
        this.createdAt = createdAt;
        this.embeddingVector = embeddingVector;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }
    public List<Float> getEmbeddingVector() {
        return embeddingVector;
    }

    public void setEmbeddingVector(List<Float> embeddingVector) {
        this.embeddingVector = embeddingVector;
    }

    public Voice getVoice() {
        return voice;
    }

    public void setVoice(Voice voice) {
        this.voice = voice;
    }

    public TrainRun getTrainRun() {
        return trainRun;
    }

    public void setTrainRun(TrainRun trainRun) {
        this.trainRun = trainRun;
    }
    public List<AuthLog> getAuthLogs() {
        return authLogs;
    }
    public void setAuthLogs(List<AuthLog> authLogs) {
        this.authLogs = authLogs;
    }
}

