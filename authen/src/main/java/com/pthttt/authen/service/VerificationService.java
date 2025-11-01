package com.pthttt.authen.service;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pthttt.authen.model.AuthLog;
import com.pthttt.authen.model.TrainRun;
import com.pthttt.authen.model.Vector;
import com.pthttt.authen.repository.AuthLogRepository;
import com.pthttt.authen.repository.ModelRepository;
import com.pthttt.authen.repository.TrainRunRepository;
import com.pthttt.authen.repository.UserRepository;
import com.pthttt.authen.repository.VectorRepository;

@Service
public class VerificationService {

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private MachineLearningService machineLearningService;

    @Autowired
    private VectorRepository vectorRepository;

    @Autowired
    private TrainRunRepository trainRunRepository;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthLogRepository authLogRepository;

    public List<com.pthttt.authen.model.Model> getAvailableModel() {
        return modelRepository.findAll();
    }

    public List<TrainRun> getAvailableTrainRun() {
        return trainRunRepository.findAll();
    }

    public List<AuthLog> verify(MultipartFile audioFile, Integer trainRunId) throws Exception {
        TrainRun trainRun = trainRunRepository.findById(trainRunId)
                .orElseThrow(() -> new Exception("TrainRun not found with id: " + trainRunId));

        float[] newEmbedding = machineLearningService.getEmbeddingForVerification(audioFile, trainRun.getModel().getName(), trainRun.getFilePath());
        normalize(newEmbedding);
        List<Vector> vectors = vectorRepository.findByTrainRunId(trainRunId);

        if (vectors.isEmpty()) {
            throw new Exception("No vectors found for this training run. Cannot perform verification.");
        }

        Map<Vector, Double> record = new HashMap<>();

        for (Vector vector : vectors) {            
            float[] existingEmbedding = toFloatArray(vector.getEmbeddingVector());
            normalize(existingEmbedding);
            double similarity = cosineSimilarity(newEmbedding, existingEmbedding);
            record.put(vector, similarity);
        }

        List<Map.Entry<Vector, Double>> sortedRecord = record.entrySet().stream()
                .sorted(Map.Entry.<Vector, Double>comparingByValue().reversed())
                .limit(5)
                .collect(Collectors.toList());

        List<AuthLog> savedLogs = new ArrayList<>();
        for (int i = 0; i < Math.min(5, record.size()); i++) {
            Map.Entry<Vector, Double> entry = sortedRecord.get(i);
            AuthLog authLog = new AuthLog();
            authLog.setCreatedAt(LocalDateTime.now());
            authLog.setRank(i + 1);
            authLog.setSimilarity(entry.getValue().floatValue());
            authLog.setUserMatch(entry.getKey().getVoice().getUser().getId());
            authLog.setCheckType("embedding");
            authLog.setVector(entry.getKey());
            savedLogs.add(authLogRepository.save(authLog));
        }

        return savedLogs;
    }

    private double cosineSimilarity(float[] vectorA, float[] vectorB) {
        double dotProduct = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
        }
        return dotProduct;
    }

    private void normalize(float[] vector) {
        double norm = 0.0;
        for (float v : vector) {
            norm += v * v;
        }
        if (norm > 1e-12) { 
            float factor = (float) (1.0 / Math.sqrt(norm));
            for (int i = 0; i < vector.length; i++) {
                vector[i] *= factor;
            }
        }
    }

    private float[] toFloatArray(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.LITTLE_ENDIAN);
        float[] floats = new float[bytes.length / 4];
        buffer.asFloatBuffer().get(floats);
        return floats;
    }
}
