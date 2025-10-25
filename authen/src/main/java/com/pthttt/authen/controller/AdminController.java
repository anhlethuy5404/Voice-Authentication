package com.pthttt.authen.controller;

import com.pthttt.authen.model.Model;
import com.pthttt.authen.model.ModelDetailDTO;
import com.pthttt.authen.model.TrainResultDTO;
import com.pthttt.authen.model.Voice;
import com.pthttt.authen.service.AdminService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
public class AdminController {
    private final AdminService adminService;

    @Autowired
    public AdminController(AdminService adminService) {
        this.adminService = adminService;
    }

    @GetMapping("/models")
    public List<Model> findAllModels() {
        return adminService.findAllModels();
    }

    @GetMapping("/trainruns/by-model/{modelId}")
    public List<TrainResultDTO> findVersionByModelId(@PathVariable int modelId) {
        return adminService.findTrainResultByModelId(modelId);
    }

    @GetMapping("/getallvoice")
    public List<Voice>  findAllVoices() {
        return adminService.findAllVoices();
    }

}
