package com.pthttt.authen.service;

import java.io.File;
import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;

import com.pthttt.authen.repository.VectorRepository;

@Service
public class MachineLearningService {
    @Value("${ai.server.url}")
    private String aiServerUrl;

    @Autowired
    private VectorRepository vectorRepository;

    private byte[] convertEmbeddingToBytes(List<Double> embedding) {
        ByteBuffer buffer = ByteBuffer.allocate(embedding.size() * 4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        for (Double value : embedding) {
            buffer.putFloat(value.floatValue());
        }
        return buffer.array();
    }

    public float[] getEmbeddingForVerification(MultipartFile audioFile, String modelName, String ckptPath) throws Exception {
        File tempFile = File.createTempFile("voice_verif_", ".wav");
        audioFile.transferTo(tempFile);

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

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        tempFile.delete();

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            List<List<Double>> embeddings = (List<List<Double>>) response.getBody().get("embeddings");

            if (embeddings != null && !embeddings.isEmpty()) {
                List<Double> embedding = embeddings.get(0);
                float[] floatArray = new float[embedding.size()];
                for (int i = 0; i < embedding.size(); i++) {
                    floatArray[i] = embedding.get(i).floatValue();
                }
                return floatArray;
            } else {
                throw new Exception("Không nhận được embedding nào từ server AI.");
            }
        } else {
            throw new Exception("Lỗi từ server AI: " + response.getStatusCode() + " - " + response.getBody());
        }
    }

    public byte[] getEmbeddingByFilePath(String filePath, String modelName, String ckptPath) throws Exception {
        RestTemplate restTemplate = new RestTemplate();
        String url = aiServerUrl + "/voice/register_voice/";

        String absolutePath = new File(filePath).getAbsolutePath();

        Map<String, Object> request = new HashMap<>();
        request.put("model_name", modelName);
        request.put("ckpt_path", ckptPath);
        request.put("file_path", absolutePath);
        request.put("num_classes", 100);

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        HttpEntity<Map<String, Object>> entity = new HttpEntity<>(request, headers);

        ResponseEntity<Map> response = restTemplate.exchange(url, HttpMethod.POST, entity, Map.class);

        if (response.getStatusCode().is2xxSuccessful() && response.getBody() != null) {
            List<List<Double>> embeddings = (List<List<Double>>) response.getBody().get("embeddings");

            if (embeddings != null && !embeddings.isEmpty()) {
                List<Double> embedding = embeddings.get(0);
                return convertEmbeddingToBytes(embedding);
            } else {
                throw new Exception("Không nhận được embedding nào từ server AI.");
            }
        } else {
            throw new Exception("Lỗi từ server AI: " + response.getStatusCode() + " - " + response.getBody());
        }
    }
}