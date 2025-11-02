package com.pthttt.authen.service;

import com.pthttt.authen.model.*;
import com.pthttt.authen.repository.ModelRepository;
import com.pthttt.authen.repository.ScoreRepository;
import com.pthttt.authen.repository.TrainRunRepository;
import com.pthttt.authen.repository.VoiceRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class SetupService {
    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private VoiceRepository voiceRepository;

    @Autowired
    private TrainRunRepository trainRunRepository;

    @Autowired
    private ScoreRepository scoreRepository;

    public List<Model> findAllModels() {
        return modelRepository.findAll();
    }

    public List<TrainResultDTO> findTrainResultByModelId(int modelId) {
        List<TrainResultDTO> results = new ArrayList<>();
        List<TrainRun> trainRuns = trainRunRepository.findByModelId(modelId);
        for (TrainRun trainRun : trainRuns) {
            Score score = scoreRepository.findByTrainRunId(trainRun.getId());
            TrainResultDTO trainResult = new TrainResultDTO(trainRun.getVersion(), trainRun.getFilePath(), trainRun.getFinishedAt(), score.getAccuracy(), score.getPrecision(), score.getRecall(), score.getF1());
            results.add(trainResult);
        }
        return results;
    }

    public List<Voice> findAllVoices() {
        return voiceRepository.findAll();
    }
}
