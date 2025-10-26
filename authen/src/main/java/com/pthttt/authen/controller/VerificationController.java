package com.pthttt.authen.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.pthttt.authen.service.MachineLearningService;

@Controller
@RequestMapping("/verification")
public class VerificationController {
    @Autowired
    private MachineLearningService machineLearningService;

    @GetMapping
    public String showVerificationForm() {
        return "verification";
    }

    @PostMapping("/embedding")
    public String getEmbedding(
            @RequestParam("audioFile") MultipartFile audioFile,
            @RequestParam("modelName") String modelName,
            @RequestParam("ckptPath") String ckptPath,
            Model model
    ) {
        try {
            machineLearningService.getEmbedding(audioFile, modelName, ckptPath);
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "verification";
        }

        return "redirect:/";
    }
}