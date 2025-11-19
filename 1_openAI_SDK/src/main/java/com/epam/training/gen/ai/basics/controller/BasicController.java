package com.epam.training.gen.ai.basics.controller;

import com.epam.training.gen.ai.basics.service.AnswerGeneratingService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/ai")
public class BasicController {
    private final AnswerGeneratingService service;

    @PostMapping("/ask")
    public ResponseEntity<List<String>> askAI(@RequestParam(name = "query") String query) {
        return ResponseEntity.ok(service.answerQuery(query));
    }
}
