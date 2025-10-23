package com.pthttt.authen.model;

import java.time.LocalDateTime;
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
@Table(name = "train_runs")
public class TrainRun {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private int id;
    private int version;
    private String status; 
    private String filePath;
    private LocalDateTime startedAt;
    private LocalDateTime finishedAt;

    @ManyToOne
    @JoinColumn(name = "model_id")
    private Model model;

    @OneToMany(mappedBy = "trainRun")
    private List<ModelVoice> modelVoice;

    @OneToMany(mappedBy = "trainRun")
    private List<TrainLog> trainLogs;

    @OneToMany(mappedBy = "trainRun")
    private List<Hyperparameter> hyperparameters;

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

    public List<ModelVoice> getModelVoice() {
        return modelVoice;
    }

    public void setModelVoice(List<ModelVoice> modelVoice) {
        this.modelVoice = modelVoice;
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
    
}
