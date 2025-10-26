package com.pthttt.authen.service;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.pthttt.authen.model.TrainRun;
import com.pthttt.authen.model.User;
import com.pthttt.authen.model.Vector;
import com.pthttt.authen.repository.TrainRunRepository;
import com.pthttt.authen.repository.UserRepository;
import com.pthttt.authen.repository.VectorRepository;

@Service
public class VerificationService {

    @Autowired
    private MachineLearningService machineLearningService;

    @Autowired
    private VectorRepository vectorRepository;

    @Autowired
    private TrainRunRepository trainRunRepository;

    @Autowired
    private UserRepository userRepository; // Although not directly used, it's good practice to have it if you need to fetch more user details.

    public User verify(MultipartFile audioFile, Integer trainRunId) throws Exception {
        TrainRun trainRun = trainRunRepository.findById(trainRunId)
                .orElseThrow(() -> new Exception("TrainRun not found with id: " + trainRunId));

        // Get the embedding for the new audio file
        float[] newEmbedding = machineLearningService.getEmbeddingForVerification(audioFile, trainRun.getModel().getName(), trainRun.getFilePath());

        // Fetch all vectors associated with this training run
        List<Vector> vectors = vectorRepository.findByTrainRunId(trainRunId);

        if (vectors.isEmpty()) {
            throw new Exception("No vectors found for this training run. Cannot perform verification.");
        }

        User bestMatchUser = null;
        double highestSimilarity = -1.0; // Cosine similarity is between -1 and 1

        // Iterate through all existing vectors to find the best match
        for (Vector vector : vectors) {
            float[] existingEmbedding = toFloatArray(vector.getEmbeddingVector());
            double similarity = cosineSimilarity(newEmbedding, existingEmbedding);

            if (similarity > highestSimilarity) {
                highestSimilarity = similarity;
                bestMatchUser = vector.getVoice().getUser(); // Assumes Vector -> Voice -> User relationship
            }
        }

        // Optional: Add a threshold to ensure the match is strong enough
        // For example: if (highestSimilarity < 0.8) { return null; }

        return bestMatchUser;
    }

    private double cosineSimilarity(float[] vectorA, float[] vectorB) {
        double dotProduct = 0.0;
        double normA = 0.0;
        double normB = 0.0;
        for (int i = 0; i < vectorA.length; i++) {
            dotProduct += vectorA[i] * vectorB[i];
            normA += Math.pow(vectorA[i], 2);
            normB += Math.pow(vectorB[i], 2);
        }
        return dotProduct / (Math.sqrt(normA) * Math.sqrt(normB)+ 1e-10);
    }

    private float[] toFloatArray(byte[] bytes) {
        ByteBuffer buffer = ByteBuffer.wrap(bytes);
        buffer.order(ByteOrder.nativeOrder());
        float[] floats = new float[bytes.length / 4];
        buffer.asFloatBuffer().get(floats);
        return floats;
    }
}