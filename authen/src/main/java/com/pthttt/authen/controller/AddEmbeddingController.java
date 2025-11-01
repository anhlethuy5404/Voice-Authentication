package com.pthttt.authen.controller;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pthttt.authen.model.TrainRun;
import com.pthttt.authen.model.Voice;
import com.pthttt.authen.repository.ModelRepository;
import com.pthttt.authen.repository.TrainRunRepository;
import com.pthttt.authen.repository.UserRepository;
import com.pthttt.authen.repository.VoiceRepository;
import com.pthttt.authen.service.AddEmbeddingService;

@Controller
@RequestMapping("/admin")
public class AddEmbeddingController {

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VoiceRepository voiceRepository;

    @Autowired
    private ModelRepository modelRepository;

    @Autowired
    private TrainRunRepository trainRunRepository;

    @Autowired
    private AddEmbeddingService addEmbeddingService;

    @GetMapping("/add-embedding")
    public String showAddEmbeddingPage(ModelMap model) {
        List<Voice> voices = voiceRepository.findAll();
        List<TrainRun> trainRuns = trainRunRepository.findAll();
        List<Map<String, Object>> voiceData = voices.stream()
            .filter(voice -> voice.getIsReal() == 1)
            .map(voice -> {
                java.util.Map<String, Object> map = new HashMap<>();
                map.put("id", voice.getId());
                map.put("filePath", voice.getFilePath());
                map.put("userId", voice.getUser().getId()); 
                return map;
            })
            .toList();

        List<Map<String, Object>> trainRunData = trainRuns.stream()
            .map(run -> {
                Map<String, Object> map = new HashMap<>();
                map.put("id", run.getId());
                map.put("version", run.getVersion());
                map.put("modelId", run.getModel().getId()); 
                return map;
            })
            .toList();

        model.addAttribute("users", userRepository.findByRole("USER"));
        model.addAttribute("voices", voiceData);
        model.addAttribute("models", modelRepository.findAll());
        model.addAttribute("trainRuns", trainRunData);

        return "adminAddembedding";
    }

    @PostMapping("/add-embedding")
    public String handleAddEmbedding(
            @RequestParam("voiceId") Integer voiceId,
            @RequestParam("trainRunId") Integer trainRunId,
            RedirectAttributes redirectAttributes) {
        try {
            addEmbeddingService.createEmbedding(voiceId, trainRunId);
            redirectAttributes.addFlashAttribute("successMessage", "Embedding added successfully!");
        } catch (Exception e) {
            redirectAttributes.addFlashAttribute("errorMessage", "Error adding embedding: " + e.getMessage());
            e.printStackTrace();
        }
        return "redirect:/admin/add-embedding";
    }
}
