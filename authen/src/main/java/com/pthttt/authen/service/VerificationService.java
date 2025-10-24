package com.pthttt.authen.service;

import org.springframework.web.multipart.MultipartFile;

public interface VerificationService {
    void getEmbedding(MultipartFile audioFile, String modelName, String ckptPath) throws Exception;
}