package com.epam.training.gen.ai.semantic.service;

import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class AnswerGeneratingService {
    private final ChatCompletionService chatCompletionService;
    private final Kernel kernel;
    private final InvocationContext invocationContext;

    public String answerQuery(String query) {

        ChatHistory chatHistory = new ChatHistory();
        chatHistory.addUserMessage(query);

        var result = chatCompletionService
                .getChatMessageContentsAsync(chatHistory, kernel, invocationContext)
                .block();

        return result.get(0).getContent();
    }
}
