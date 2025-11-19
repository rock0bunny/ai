package com.epam.training.gen.ai.basics.service;

import com.openai.client.OpenAIClient;
import com.openai.models.ChatModel;
import com.openai.models.responses.ResponseCreateParams;
import com.openai.models.responses.ResponseOutputText;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@AllArgsConstructor
public class AnswerGeneratingService {
    private final OpenAIClient client;

    public List<String> answerQuery(String query) {
        ResponseCreateParams params = ResponseCreateParams.builder()
                .input(query)
                .model(ChatModel.GPT_5_NANO)
                .build();

        return client.responses().create(params).output().stream()
                .flatMap(item -> item.message().stream())
                .flatMap(message -> message.content().stream())
                .flatMap(content -> content.outputText().stream())
                .map(ResponseOutputText::text)
                .toList();

    }
}
