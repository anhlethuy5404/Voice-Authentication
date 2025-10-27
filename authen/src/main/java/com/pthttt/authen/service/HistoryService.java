package com.pthttt.authen.service;

import com.pthttt.authen.model.*;
import com.pthttt.authen.repository.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class HistoryService {
    @Autowired
    private TrainRunRepository trainRunRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private VoiceRepository voiceRepository;

    @Autowired
    private AuthLogRepository authLogRepository;

    @Autowired
    private VectorRepository vectorRepository;

    public List<Voice> getVoicesByUserId(int userId) {
        return voiceRepository.findByUserId(userId);
    }

    public List<Vector> getVectorByVoiceId(int voiceId) {
        return vectorRepository.findByVoiceId(voiceId);
    }

    public List<AuthLog> getAuthLogsByVectorId(int vectorId) {
        return authLogRepository.findByVectorId(vectorId);
    }

    public TrainRun getTrainRunByVectorId(int vectorId) {
        return trainRunRepository.findByVectorId(vectorId);
    }

    public Model getModelByTrainRunId(int trainRunId) {
        return modelRepository.findByTrainRunId(trainRunId);
    }

    public List<HistorySummaryDTO> getHistoriesByUserId(int userId) {
        List <HistorySummaryDTO> histories = new ArrayList<>();

        List<Voice> voices = getVoicesByUserId(userId);

        for (Voice voice : voices) {
            var vectors = getVectorByVoiceId(voice.getId());

            for (Vector vector : vectors) {
                var authLogs = getAuthLogsByVectorId(vector.getId());
                var trainRun = getTrainRunByVectorId(vector.getId());
                var model = getModelByTrainRunId(trainRun.getId());

                for (AuthLog authLog : authLogs) {
                    HistorySummaryDTO history = new HistorySummaryDTO();
//                    history.setCheckType();
                    history.setFilePath(voice.getFilePath());
//                    history.setCreatedAt(authLog.get());
                    history.setRank(authLog.getRank());
                    history.setVersion(trainRun.getVersion());
                    history.setSimilarity(authLog.getSimilarity());
                    history.setModelName(model.getName());
//                    history.setUserMatch(authLog.getUserMatch());
                    histories.add(history);
                }
            }
        }

        return histories;
    }
}
