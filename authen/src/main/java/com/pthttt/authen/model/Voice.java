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
import jakarta.persistence.Temporal;
import jakarta.persistence.TemporalType;

@Entity
@Table(name = "voice")
public class Voice {
    @Id
    @GeneratedValue(strategy=GenerationType.IDENTITY)
    private int id;
    private String filePath;

    @Temporal(TemporalType.TIMESTAMP)
    private Date createdAt;

    private int isReal; // 1 - real, 0 - fake

    @ManyToOne
    @JoinColumn(name = "user_id")
    private User user;

    @OneToMany(mappedBy = "voice")
    private List<Vector> vectors;

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

    public Date getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Date createdAt) {
        this.createdAt = createdAt;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public List<Vector> getVectors() {
        return vectors;
    }

    public void setVectors(List<Vector> vectors) {
        this.vectors = vectors;
    }

    public int getIsReal() {
        return isReal;
    }
    public void setIsReal(int isReal) {
        this.isReal = isReal;
    }
}
