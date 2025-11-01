package com.pthttt.authen.controller;

import com.pthttt.authen.model.Model;
import com.pthttt.authen.model.Voice;
import com.pthttt.authen.service.SetupService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class SetupController {
    private final SetupService SetupService;

    @Autowired
    public SetupController(SetupService SetupService) {
        this.SetupService = SetupService;
    }

    @GetMapping("")
    public String adminHome() {
        return "adminHome";
    }

    @GetMapping("/setup")
    public String getModelAndData(org.springframework.ui.Model model) {
        List<Voice> voices = SetupService.findAllVoices();
        List<Model> models = SetupService.findAllModels();

        model.addAttribute("voices", voices);
        model.addAttribute("models", models);
        return "adminSetup";
    }
}
