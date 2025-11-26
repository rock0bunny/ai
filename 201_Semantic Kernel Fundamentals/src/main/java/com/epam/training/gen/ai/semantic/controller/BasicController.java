package com.epam.training.gen.ai.semantic.controller;

import com.epam.training.gen.ai.semantic.service.AnswerGeneratingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@RestController
@AllArgsConstructor
@RequestMapping("/ai")
public class BasicController {
    private final AnswerGeneratingService service;

    @PostMapping("/ask")
    public ResponseEntity<String> askAI(@RequestParam(name = "query") String query) {
        return ResponseEntity.ok(service.answerQuery(query));
    }
}
