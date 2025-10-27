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
    private ModelVoiceRepository modelVoiceRepository;

    public List<Voice> getVoicesByUserId(int userId) {
        return voiceRepository.findByUserId(userId);
    }

    public List<ModelVoice> getModelVoicesByVoiceId(int voiceId) {
        return modelVoiceRepository.getByVoiceId(voiceId);
    }

    public List<AuthLog> getAuthLogsByModelVoiceId(int modelVoiceId) {
        return authLogRepository.findByModelVoiceId(modelVoiceId);
    }

    public TrainRun getTrainRunByModelVoiceId(int modelVoiceId) {
        return trainRunRepository.findByModelVoiceId(modelVoiceId);
    }

    public Model getModelByTrainRunId(int trainRunId) {
        return modelRepository.findByTrainRunId(trainRunId);
    }

    public List<HistorySummaryDTO> getHistoriesByUserId(int userId) {
        List <HistorySummaryDTO> histories = new ArrayList<>();

        List<Voice> voices = getVoicesByUserId(userId);

        for (Voice voice : voices) {
            var modelVoices = getModelVoicesByVoiceId(voice.getId());

            for (ModelVoice modelVoice : modelVoices) {
                var authLogs = getAuthLogsByModelVoiceId(modelVoice.getId());
                var trainRun = getTrainRunByModelVoiceId(modelVoice.getId());
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
