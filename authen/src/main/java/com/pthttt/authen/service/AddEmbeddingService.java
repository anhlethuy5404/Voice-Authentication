package com.pthttt.authen.service;

import java.util.Date;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.pthttt.authen.model.Model;
import com.pthttt.authen.model.TrainRun;
import com.pthttt.authen.model.Vector;
import com.pthttt.authen.model.Voice;
import com.pthttt.authen.repository.TrainRunRepository;
import com.pthttt.authen.repository.VectorRepository;
import com.pthttt.authen.repository.VoiceRepository;

@Service
public class AddEmbeddingService {

    @Autowired
    private VoiceRepository voiceRepository;

    @Autowired
    private TrainRunRepository trainRunRepository;

    @Autowired
    private VectorRepository vectorRepository;

    @Autowired
    private MachineLearningService machineLearningService;

    @Transactional
    public void createEmbedding(Integer voiceId, Integer trainRunId) throws Exception {
        // Fetch entity
        Voice voice = voiceRepository.findById(voiceId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid Voice ID: " + voiceId));

        TrainRun trainRun = trainRunRepository.findById(trainRunId)
                .orElseThrow(() -> new IllegalArgumentException("Invalid TrainRun ID: " + trainRunId));

        Model model = trainRun.getModel();
        if (model == null) {
            throw new IllegalStateException("TrainRun with ID " + trainRunId + " is not associated with any model.");
        }

        // Lấy tt cần
        String voiceFilePath = voice.getFilePath();
        String modelName = model.getName();
        String ckptPath = trainRun.getFilePath();
        // Path basePath = Paths.get(System.getProperty("user.dir"));
        // Path parentDir = basePath.getParent();
        // ckptPath = parentDir.resolve(ckptPath.substring(3)).normalize().toString();
        // System.out.println(ckptPath);


        byte[] embedding = machineLearningService.getEmbeddingByFilePath(voiceFilePath, modelName, ckptPath);

        // Save Vector
        Vector vector = new Vector();
        vector.setVoice(voice);
        vector.setTrainRun(trainRun);
        vector.setEmbeddingVector(embedding);
        vector.setCreatedAt(new Date());

        vectorRepository.save(vector);
    }
}
