package com.pthttt.authen.model;

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
@Table(name = "voices")
public class Voice {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private String filePath;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "voice")
    private List<ModelVoice> modelVoices;

    public Voice() {
    }

    public Voice(String filePath, User user) {
        this.filePath = filePath;
        this.user = user;
    }
    
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<ModelVoice> getModelVoices() {
        return modelVoices;
    }
    public void setModelVoices(List<ModelVoice> modelVoices) {
        this.modelVoices = modelVoices;
    }
}
