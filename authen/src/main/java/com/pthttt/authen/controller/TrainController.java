package com.pthttt.authen.controller;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.pthttt.authen.service.TrainService;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/admin")
@RequiredArgsConstructor
public class TrainController {

    private static final Logger log = LoggerFactory.getLogger(TrainController.class);
    private final TrainService trainService;
    private final ObjectMapper objectMapper = new ObjectMapper();

    // ========== HIỂN THỊ FORM ==========
    @GetMapping("/train")
    public String showTrainPage(Model model) {
        return "train";
    }

    // ========== XỬ LÝ FORM SUBMIT ==========
    @PostMapping("/train")
    public String trainModel(
            @RequestParam("model_id") int modelId,
            @RequestParam("model_name") String modelName,
            @RequestParam("model_type") String modelType,
            @RequestParam("split_train") int splitTrain,
            @RequestParam("split_val") int splitVal,
            @RequestParam("split_test") int splitTest,
            @RequestParam("num_workers") int numWorkers,
            @RequestParam("num_epochs") int numEpochs,
            @RequestParam("batch_size") int batchSize,
            @RequestParam("learning_rate") double learningRate,
            @RequestParam("patience") int patience,
            @RequestParam(value = "selectedVoices", required = false) List<String> selectedVoices,
            Model model
    ) {
        log.info("🚀 Starting training for model: {}", modelName);

        // ✅ Parse selected voices
        List<Map<String, String>> parsedVoices = new java.util.ArrayList<>();
        if (selectedVoices != null) {
            for (String s : selectedVoices) {
                String[] parts = s.split("::", 2);
                if (parts.length == 2) {
                    Map<String, String> entry = new HashMap<>();
                    entry.put("user_id", parts[0]);
                    entry.put("file_path", parts[1]);
                    parsedVoices.add(entry);
                }
            }
        }

        // ✅ Chuẩn bị dataset (train / val split)
        Map<String, Object> dataset = trainService.prepareDataset(parsedVoices, splitTrain);
        log.info("✅ train voices: {}", dataset.get("train_data"));
        log.info("val voices: {}", dataset.get("val_data"));
        log.info("num voices: {}", dataset.get("num_classes"));

        try {
            // 1️⃣ Tạo config JSON (không lưu file)
            Map<String, Object> config = new HashMap<>();
            config.put("model_id", modelId);
            config.put("model_name", modelName);
            config.put("model_type", modelType);
            config.put("num_classes", dataset.get("num_classes"));
            config.put("train_data", dataset.get("train_data"));
            config.put("val_data", dataset.get("val_data"));
            config.put("num_workers", numWorkers);
            config.put("num_epochs", numEpochs);
            config.put("batch_size", batchSize);
            config.put("learning_rate", learningRate);
            config.put("patience", patience);
            config.put("selected_voices", selectedVoices != null ? selectedVoices : List.of());

            // ✨ Chuyển sang chuỗi JSON
            String jsonConfig = objectMapper.writerWithDefaultPrettyPrinter().writeValueAsString(config);
            log.info("📦 Config JSON ready to send:\n{}", jsonConfig);

            // 2️⃣ Gọi service training qua WebSocket (asynchronous)
            String pythonWsUrl = "ws://localhost:8000/model/train/ws";
            trainService.startTrainingAsync(pythonWsUrl, jsonConfig, new TrainService.TrainingListener() {
                @Override
                public void onStatus(String message) {
                    log.info("[STATUS] {}", message);
                }

                @Override
                public void onInfo(String message) {
                    log.info("[INFO] {}", message);
                }

                @Override
                public void onEpoch(int epoch, int total, double trainLoss, double valLoss) {
                    log.info("[EPOCH] {}/{} train_loss={} val_loss={}", epoch, total, trainLoss, valLoss);
                }

                @Override
                public void onCompleted(String message) {
                    log.info("[COMPLETED] {}", message);
                }

                @Override
                public void onError(String message) {
                    log.error("[ERROR] {}", message);
                }
            });

            model.addAttribute("message", "Training started asynchronously for model: " + modelName);
        } catch (Exception e) {
            log.error("❌ Failed to start training: {}", e.getMessage(), e);
            model.addAttribute("message", "Failed to start training: " + e.getMessage());
        }

        return "train";
    }
}
