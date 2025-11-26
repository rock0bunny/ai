package com.epam.training.gen.ai.semantic.service;

import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionArguments;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

@Slf4j
@Service
@AllArgsConstructor
public class AnswerGeneratingService {
    private final Kernel kernel;
    private final InvocationContext invocationContext;
    private final ConcurrentMap<String, ChatHistory> userHistories = new ConcurrentHashMap<>();

    public String processWithHistory(String userId, String prompt) {
        ChatHistory chatHistory = userHistories.computeIfAbsent(userId, k -> new ChatHistory());
        chatHistory.addUserMessage(prompt);
        KernelFunctionArguments args = getKernelFunctionArguments(prompt, chatHistory);
        var result = kernel.invokeAsync(getChat())
                .withArguments(args)
                .block();

        String response = result.getResult();

        chatHistory.addAssistantMessage(response);

        return response;
    }

    private KernelFunction<String> getChat() {
        return KernelFunction.<String>createFromPrompt("""
                        {{$chatHistory}}
                        <message role="user">{{$request}}</message>""")
                .build();
    }

    private KernelFunctionArguments getKernelFunctionArguments(String prompt, ChatHistory chatHistory) {
        return KernelFunctionArguments.builder()
                .withVariable("request", prompt)
                .withVariable("chatHistory", chatHistory)
                .build();
    }
}