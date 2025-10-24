package com.pthttt.authen.service;

import java.io.IOException;

import org.springframework.web.multipart.MultipartFile;

public interface VoiceService {
    void saveVoice(MultipartFile file, String username) throws IOException;
}
