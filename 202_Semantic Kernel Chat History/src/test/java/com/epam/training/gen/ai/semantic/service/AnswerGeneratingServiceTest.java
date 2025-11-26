package com.epam.training.gen.ai.semantic.service;

import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.FunctionInvocation;
import com.microsoft.semantickernel.orchestration.FunctionResult;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import com.microsoft.semantickernel.semanticfunctions.KernelFunction;
import com.microsoft.semantickernel.semanticfunctions.KernelFunctionArguments;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;

class AnswerGeneratingServiceTest {

    private Kernel kernel;
    private InvocationContext invocationContext;
    private AnswerGeneratingService service;

    @BeforeEach
    void setUp() {
        kernel = mock(Kernel.class);
        invocationContext = mock(InvocationContext.class);
        service = new AnswerGeneratingService(kernel, invocationContext);
    }

    @Test
    void processWithHistory_twoRequests_bothQueriesInChatHistory() {
        // Arrange
        FunctionInvocation<String> invocation = mock(FunctionInvocation.class);
        when(kernel.invokeAsync(any(KernelFunction.class))).thenReturn(invocation);

        ArgumentCaptor<KernelFunctionArguments> argsCaptor = ArgumentCaptor.forClass(KernelFunctionArguments.class);
        when(invocation.withArguments(argsCaptor.capture())).thenReturn(invocation);

        FunctionResult<String> result = mock(FunctionResult.class);
        when(invocation.block()).thenReturn(result);
        var testAnswer = "test answer";
        when(result.getResult()).thenReturn(testAnswer);

        var userId = "user-123";

        // Act
        var prompt1 = "first query";
        var prompt2 = "second query";
        var response1 = service.processWithHistory(userId, prompt1);
        var response2 = service.processWithHistory(userId, prompt2);

        // Assert
        assertThat(response1).isEqualTo(testAnswer);
        assertThat(response2).isEqualTo(testAnswer);

        // The last call's arguments
        var capturedArgs = argsCaptor.getValue();
        assertThat(capturedArgs).isNotNull();
        var chatHistoryObj = capturedArgs.get("chatHistory").getValue();
        assertThat(chatHistoryObj).isInstanceOf(ChatHistory.class);
        var chatHistory = (ChatHistory) chatHistoryObj;

        // Check both queries are present in the chat history messages
        var hasPrompt1 = chatHistory.getMessages().stream()
                .anyMatch(m -> prompt1.equals(m.getContent()));
        var hasPrompt2 = chatHistory.getMessages().stream()
                .anyMatch(m -> prompt2.equals(m.getContent()));
        var hasTestAnswer = chatHistory.getMessages().stream()
                .anyMatch(m -> testAnswer.equals(m.getContent()));

        assertThat(hasPrompt1)
                .as("Chat history should contain the first user query: %s", prompt1)
                .isTrue();

        assertThat(hasPrompt2)
                .as("Chat history should contain the second user query: %s", prompt2)
                .isTrue();
        assertThat(hasTestAnswer)
                .as("Chat history should contain the the mocked response: %s", prompt2)
                .isTrue();
    }


    @Test
    void processWithHistory_differentUsers_separateHistories() {
        // Arrange
        FunctionInvocation<String> invocation = mock(FunctionInvocation.class);
        when(kernel.invokeAsync(any(KernelFunction.class))).thenReturn(invocation);

        ArgumentCaptor<KernelFunctionArguments> argsCaptor = ArgumentCaptor.forClass(KernelFunctionArguments.class);
        when(invocation.withArguments(argsCaptor.capture())).thenReturn(invocation);

        FunctionResult<String> result = mock(FunctionResult.class);
        when(invocation.block()).thenReturn(result);
        var testAnswer = "test answer";
        when(result.getResult()).thenReturn(testAnswer);

        var userId1 = "user-1";
        var userId2 = "user-2";
        var prompt1 = "hello from user1";
        var prompt2 = "hello from user2";

        // Act
        service.processWithHistory(userId1, prompt1);
        service.processWithHistory(userId2, prompt2);

        // Assert
        // There should be two captured argument sets, one for each user
        var capturedArgsList = argsCaptor.getAllValues();
        assertThat(capturedArgsList).hasSize(2);

        var chatHistory1 = (ChatHistory) capturedArgsList.get(0).get("chatHistory").getValue();
        var chatHistory2 = (ChatHistory) capturedArgsList.get(1).get("chatHistory").getValue();

        assertThat(chatHistory1).isNotSameAs(chatHistory2);

        var user1HasPrompt1 = chatHistory1.getMessages().stream()
                .anyMatch(m -> prompt1.equals(m.getContent()));
        var user2HasPrompt2 = chatHistory2.getMessages().stream()
                .anyMatch(m -> prompt2.equals(m.getContent()));

        assertThat(user1HasPrompt1)
                .as("User 1's history should contain their prompt")
                .isTrue();
        assertThat(user2HasPrompt2)
                .as("User 2's history should contain their prompt")
                .isTrue();

        // Ensure user 1's history does not contain user 2's prompt and vice versa
        var user1HasPrompt2 = chatHistory1.getMessages().stream()
                .anyMatch(m -> prompt2.equals(m.getContent()));
        var user2HasPrompt1 = chatHistory2.getMessages().stream()
                .anyMatch(m -> prompt1.equals(m.getContent()));

        assertThat(user1HasPrompt2).isFalse();
        assertThat(user2HasPrompt1).isFalse();
    }
}