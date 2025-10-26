package com.pthttt.authen.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.pthttt.authen.model.TrainRun;
import com.pthttt.authen.model.User;
import com.pthttt.authen.repository.ModelRepository;
import com.pthttt.authen.repository.TrainRunRepository;
import com.pthttt.authen.service.VerificationService;

@Controller
@RequestMapping("/verification")
public class VerificationController {
    @Autowired
    private VerificationService verificationService;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private TrainRunRepository trainRunRepository;

    @GetMapping
    public String showVerificationForm(Model model) {
        List<com.pthttt.authen.model.Model> models = modelRepository.findAll();
        List<TrainRun> trainRuns = trainRunRepository.findAll();
        model.addAttribute("models", models);
        List<Map<String, Object>> trainRunData = trainRuns.stream()
            .map(run -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", run.getId());
                map.put("version", run.getVersion());
                map.put("modelId", run.getModel().getId()); 
                return map;
            })
            .toList();
        model.addAttribute("trainRuns", trainRunData);
        return "verification";
    }

    @PostMapping
    public String verify(
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam("trainRunId") Integer trainRunId,
            Model model
    ) {
        try {
            User user = verificationService.verify(audioFile, trainRunId);
            model.addAttribute("resultUser", user);
            if (user == null) {
                model.addAttribute("resultMessage", "Không tìm thấy người dùng phù hợp.");
            }
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi xác thực: " + e.getMessage());
        }

        // Add models and trainRuns again to repopulate the form after submission
        List<com.pthttt.authen.model.Model> models = modelRepository.findAll();
        List<TrainRun> trainRuns = trainRunRepository.findAll();
        model.addAttribute("models", models);
        model.addAttribute("trainRuns", trainRuns);

        return "verification";
    }
}