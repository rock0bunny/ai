package com.epam.training.gen.ai.basics.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

@Slf4j
@Service
//@AllArgsConstructor
public class AnswerGeneratingService {
    private final OpenAIAsyncClient client;

    //@Value("${client-azureopenai-deployment-name}")
    private final String deploymentName;

    public AnswerGeneratingService(OpenAIAsyncClient client,
                                   @Value("${client-azureopenai-deployment-name}") String deploymentName) {
        this.client = client;
        this.deploymentName = deploymentName;
    }

    public List<String> answerQuery(String query) {
        //String deploymentName = "gpt-4.1-nano";

        try {

            ChatCompletionsOptions options = new ChatCompletionsOptions(List.of(
                    new ChatRequestUserMessage(query)
            ));

            CompletableFuture<List<String>> futureAnswers = new CompletableFuture<>();

            client.getChatCompletions(deploymentName, options)
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
