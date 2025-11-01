package com.pthttt.authen.controller;

import com.pthttt.authen.model.Model;
import com.pthttt.authen.model.Voice;
import com.pthttt.authen.service.TrainService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class TrainController {
    private final TrainService trainService;

    @Autowired
    public TrainController(TrainService trainService) {
        this.trainService = trainService;
    }

    @GetMapping("")
    public String adminHome() {
        return "adminHome";
    }

    @GetMapping("/setup")
    public String getModelAndData(org.springframework.ui.Model model) {
        List<Voice> voices = trainService.findAllVoices();
        List<Model> models = trainService.findAllModels();

        model.addAttribute("voices", voices);
        model.addAttribute("models", models);
        return "adminSetup";
    }
}
