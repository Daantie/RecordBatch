package com.example.rabobankassignment.web;

import com.example.rabobankassignment.service.RecordService;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;

@Controller
@RequestMapping("/records")
public class RecordController {
    private final RecordService recordService;

    public RecordController(RecordService recordService) {
        this.recordService = recordService;
    }

    @GetMapping("/invalid")
    public String usingToList(Model model) {
        model.addAttribute("invalidRecords", recordService.getInvalidRecords());
        return "invalid_records";
    }
}
