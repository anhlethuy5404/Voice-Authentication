package com.pthttt.authen.controller;

import com.pthttt.authen.model.Model;
import com.pthttt.authen.model.Voice;
import com.pthttt.authen.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/admin")
public class AdminController {
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/setup")
    public String getModelAndData(org.springframework.ui.Model model) {
        List<Voice> voices = adminService.findAllVoices();
        List<Model> models = adminService.findAllModels();

        model.addAttribute("voices", voices);
        model.addAttribute("models", models);
        return "admin";
    }
}
