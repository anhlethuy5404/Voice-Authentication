package com.pthttt.authen.model;

import java.util.Date;
import java.util.List;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Lob;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "vector")
public class Vector {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    @Lob
    @Column(columnDefinition = "LONGBLOB")
    private byte[] embeddingVector;

    @ManyToOne
    @JoinColumn(name = "voice_id")
    private Voice voice;

    @ManyToOne
    @JoinColumn(name = "trainRun_id")
    private TrainRun trainRun;

    @OneToMany(mappedBy = "modelVoice")
    private List<AuthLog> authLogs;

    public Vector() {}

    public Vector(Date createdAt, byte[] embeddingVector) {
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

    public byte[] getEmbeddingVector() {
        return embeddingVector;
    }

    public void setEmbeddingVector(byte[] embeddingVector) {
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