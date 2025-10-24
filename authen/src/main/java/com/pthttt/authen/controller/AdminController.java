package com.pthttt.authen.controller;

import java.io.IOException;
import java.security.Principal;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.pthttt.authen.service.VoiceService;

@Controller
@RequestMapping("/admin")
public class AdminController {

    @Autowired
    private VoiceService voiceService;

    @GetMapping("/add-voice-form")
    public String showAddVoiceForm() {
        return "addvoice";
    }

    @PostMapping("/upload-voice")
    public String uploadVoice(@RequestParam("voiceFile") MultipartFile file, Principal principal, RedirectAttributes redirectAttributes) {
        if (principal == null) {
            return "redirect:/login";
        }

        try {
            voiceService.saveVoice(file, principal.getName());
            redirectAttributes.addFlashAttribute("message", "You successfully uploaded '" + file.getOriginalFilename() + "'!");
        } catch (IOException e) {
            e.printStackTrace();
            redirectAttributes.addFlashAttribute("message", "Failed to upload '" + file.getOriginalFilename() + "'. " + e.getMessage());
        }

        return "redirect:/admin/add-voice-form";
    }
}
