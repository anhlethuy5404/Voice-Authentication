package com.pthttt.authen.service;

import java.io.File;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

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
        String url = aiServerUrl + "/voice/register_voice/"; // Thay đổi endpoint

        Map<String, String> request = new HashMap<>();
        request.put("model_name", modelName);
        request.put("ckpt_path", ckptPath);
        request.put("file_path", tempFile.getAbsolutePath());

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, String>> entity = new HttpEntity<>(request, headers);

        // 3️⃣ Gọi FastAPI
        ResponseEntity<Map> response = restTemplate.exchange(
                url, HttpMethod.POST, entity, Map.class
        );

        // 4️⃣ Xử lý phản hồi
        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            // Lấy danh sách các embedding
            List<List<Double>> embeddings = (List<List<Double>>) response.getBody().get("embeddings");
            System.out.println("Received embeddings: " + embeddings);

            if (embeddings != null && !embeddings.isEmpty()) {
                // Lấy embedding đầu tiên từ danh sách
                List<Double> embedding = embeddings.get(0);

                // Chuyển đổi List<Double> thành List<Float>
                List<Float> embeddingVector = embedding.stream()
                        .map(Double::floatValue)
                        .collect(Collectors.toList());

                ModelVoice modelVoice = new ModelVoice(new Date(), embeddingVector);
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
}
