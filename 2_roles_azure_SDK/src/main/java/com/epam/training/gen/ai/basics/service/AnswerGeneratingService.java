package com.epam.training.gen.ai.basics.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.models.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
public class AnswerGeneratingService {
    private final OpenAIAsyncClient client;
    private final String deploymentOrModelName;

    public AnswerGeneratingService(OpenAIAsyncClient client, @Value("${client-azureopenai-deployment-name}") String deploymentOrModelName) {
        this.client = client;
        this.deploymentOrModelName = deploymentOrModelName;
    }

    public List<String> answerQuery(String query) {
        try {

            var systemMessage = "You are a man that never saw any vegetable or fruit";

            ChatCompletionsOptions options = new ChatCompletionsOptions(List.of(
                    new ChatRequestUserMessage(query),
                    new ChatRequestSystemMessage(systemMessage)//Provide me with an information what is an apple?
            ));

            CompletableFuture<List<String>> futureAnswers = new CompletableFuture<>();

            client.getChatCompletions(deploymentOrModelName, options)
                    .subscribe(response -> {
                                List<String> answers = response.getChoices().stream()
                                        .map(choice -> choice.getMessage().getContent())
                                        .collect(Collectors.toList());
                                futureAnswers.complete(answers);
                            },
                            throwable -> {
                                log.error("Error while generating answer for query: {}", query, throwable);
                                futureAnswers.completeExceptionally(throwable);
                            });

            return futureAnswers.join();
        } catch (Exception e) {
            log.error("Error while generating answer for query: {}", query, e);
            return Collections.emptyList();
        }

    }
}
