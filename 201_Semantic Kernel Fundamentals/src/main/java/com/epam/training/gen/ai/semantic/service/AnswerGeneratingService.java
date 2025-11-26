package com.epam.training.gen.ai.semantic.service;

import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.FunctionResult;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Slf4j
@Service
@AllArgsConstructor
public class AnswerGeneratingService {
    private final Kernel kernel;
    private final InvocationContext invocationContext;
    //private final ChatCompletionService chatCompletionService;

    public String answerQuery(String query) {

        FunctionResult<String> result = kernel.invokePromptAsync(query)
                .withInvocationContext(invocationContext)
                .withResultType(String.class)
                .block();

        return result.getResult();

//        List<ChatMessageContent<?>> results = chatCompletionService
//                .getChatMessageContentsAsync(query, kernel, invocationContext)
//                .block();
//
//        return results.stream()
//                .map(ChatMessageContent::getContent)
//                .collect(Collectors.joining());
    }
}
