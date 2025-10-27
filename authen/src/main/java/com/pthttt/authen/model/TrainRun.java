package com.pthttt.authen.model;

import java.time.LocalDateTime;
import java.util.List;

import jakarta.persistence.*;

@Entity
@Table(name = "train_run")
public class TrainRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int version;
    private String status; 
    private String filePath;
    private String type;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model;

    @OneToOne(mappedBy = "trainRun")
    private Score score;

    @OneToMany(mappedBy = "trainRun")
    private List<Vector> vectors;

    @OneToMany(mappedBy = "trainRun")
    private List<TrainLog> trainLogs;

    @OneToMany(mappedBy = "trainRun")
    private List<Hyperparameter> hyperparameters;

    @OneToOne(mappedBy = "trainRun")
    private Score score;

    public TrainRun() {
    }

    public TrainRun(int version, String status, String filePath, LocalDateTime startedAt, LocalDateTime finishedAt) {   
        this.version = version;
        this.status = status;
        this.filePath = filePath;
        this.startedAt = startedAt;
        this.finishedAt = finishedAt;
    }
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public LocalDateTime getStartedAt() {
        return startedAt;
    }

    public void setStartedAt(LocalDateTime startedAt) {
        this.startedAt = startedAt;
    }

    public LocalDateTime getFinishedAt() {
        return finishedAt;
    }

    public void setFinishedAt(LocalDateTime finishedAt) {
        this.finishedAt = finishedAt;
    }
    public Model getModel() {
        return model;
    }
    public void setModel(Model model) {
        this.model = model;
    }

    public List<Vector> getVectors() {
        return vectors;
    }

    public void setVectors(List<Vector> vectors) {
        this.vectors = vectors;
    }

    public List<TrainLog> getTrainLogs() {
        return trainLogs;
    }

    public void setTrainLogs(List<TrainLog> trainLogs) {
        this.trainLogs = trainLogs;
    }

    public List<Hyperparameter> getHyperparameters() {
        return hyperparameters;
    }

    public void setHyperparameters(List<Hyperparameter> hyperparameters) {
        this.hyperparameters = hyperparameters;
    }
    
    public Score getScore() {
        return score;
    }
    public void setScore(Score score) {
        this.score = score;
    }
    public String getType() {
        return type;
    }
    public void setType(String type) {
        this.type = type;
    }
}
