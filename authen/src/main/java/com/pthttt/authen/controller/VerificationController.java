package com.pthttt.authen.controller;

import com.pthttt.authen.service.VerificationService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

@Controller
@RequestMapping("/verification")
public class VerificationController {

    private final VerificationService verificationService;

    public VerificationController(VerificationService verificationService) {
        this.verificationService = verificationService;
    }

    @GetMapping
    public String showForm() {
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
            verificationService.getEmbedding(audioFile, modelName, ckptPath);
        } catch (Exception e) {
            model.addAttribute("error", "Lỗi: " + e.getMessage());
            return "verification";
        }

        return "redirect:/";
    }
}