package com.pthttt.authen.controller;

import com.pthttt.authen.model.HistorySummaryDTO;
import com.pthttt.authen.service.HistoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;

@Controller
@RequestMapping("/history")
public class HistoryController {

    private final HistoryService historyService;

    public HistoryController(HistoryService historyService) {
        this.historyService = historyService;
    }

    @GetMapping
    public String getAuthLogs(@RequestParam("userId") int userId, Model model) {
        List<HistorySummaryDTO> histories = historyService.getHistoriesByUserId(userId);
        model.addAttribute("histories", histories);
        return "history";
    }
}
