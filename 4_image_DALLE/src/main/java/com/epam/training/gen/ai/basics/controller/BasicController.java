package com.epam.training.gen.ai.basics.controller;

import com.epam.training.gen.ai.basics.service.ImageGeneratingService;
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
    private final ImageGeneratingService imageGeneratingService;


    @PostMapping("/generate-image")
    public ResponseEntity<String> generateImage(@RequestParam(name = "query") String query) {
        return ResponseEntity.ok(imageGeneratingService.generateImage(query));
    }
}
