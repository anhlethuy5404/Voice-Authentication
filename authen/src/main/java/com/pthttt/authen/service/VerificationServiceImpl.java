package com.pthttt.authen.service;

import java.io.File;
import java.nio.ByteBuffer;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.pthttt.authen.model.ModelVoice;
import com.pthttt.authen.repository.ModelVoiceRepository;

@Service
public class VerificationServiceImpl implements VerificationService {

    @Value("${ai.server.url}")
    private String aiServerUrl;

    private final ModelVoiceRepository modelVoiceRepository;

    public VerificationServiceImpl(ModelVoiceRepository modelVoiceRepository) {
        this.modelVoiceRepository = modelVoiceRepository;
    }

    @Override
    public void getEmbedding(MultipartFile audioFile, String modelName, String ckptPath) throws Exception {
        // 1️⃣ Lưu file tạm
        File tempFile = File.createTempFile("voice_", ".wav");
        audioFile.transferTo(tempFile);

        // 2️⃣ Chuẩn bị request JSON
        RestTemplate restTemplate = new RestTemplate();
        String url = aiServerUrl + "/voice/register_voice/";

        Map<String, Object> request = new HashMap<>();
        request.put("model_name", modelName);
        request.put("ckpt_path", ckptPath);
        request.put("file_path", tempFile.getAbsolutePath());
        request.put("num_classes", 100);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        // 3️⃣ Gọi FastAPI
        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        // 4️⃣ Xử lý phản hồi
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            // embeddings trả về là List<List<Double>>
            List<List<Double>> embeddings = (List<List<Double>>) response.getBody().get("embeddings");

            if (embeddings != null && !embeddings.isEmpty()) {
                List<Double> embedding = embeddings.get(0);

                byte[] embBytes = convertEmbeddingToBytes(embedding);
                ModelVoice modelVoice = new ModelVoice(new Date(), embBytes);
                modelVoiceRepository.save(modelVoice);
            } else {
                throw new Exception("Không nhận được embedding nào từ server AI.");
            }
        } else {
            throw new Exception("Lỗi từ server AI: " + response.getStatusCode() + " - " + response.getBody());
        }

        // 5️⃣ Xoá file tạm
        tempFile.delete();
    }

    private byte[] convertEmbeddingToBytes(List<Double> embedding) {
        ByteBuffer buffer = ByteBuffer.allocate(embedding.size() * 4);
        for (Double value : embedding) {
            buffer.putFloat(value.floatValue());
        }
        return buffer.array();
    }
}