package com.epam.training.gen.ai.basics.service;

import com.azure.ai.openai.OpenAIAsyncClient;
import com.azure.ai.openai.models.ChatCompletions;
import com.azure.ai.openai.models.ChatCompletionsOptions;
import com.azure.ai.openai.models.ChatChoice;
import com.azure.ai.openai.models.ChatResponseMessage;
import com.azure.ai.openai.models.ChatRequestUserMessage;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AnswerGeneratingServiceTest {

    @Test
    void answerQuery_sendsCorrectMessageAndReturnsExpectedMessages() {
        // Arrange
        var client = mock(OpenAIAsyncClient.class);
        var responseMessage = mock(ChatResponseMessage.class, invocation -> {
            if ("getContent".equals(invocation.getMethod().getName())) {
                return "Test answer";
            }
            return RETURNS_DEFAULTS.answer(invocation);
        });
        var choice = mock(ChatChoice.class);
        when(choice.getMessage()).thenReturn(responseMessage);
        var completions = mock(ChatCompletions.class);
        when(completions.getChoices()).thenReturn(List.of(choice));

        var optionsCaptor = ArgumentCaptor.forClass(ChatCompletionsOptions.class);
        when(client.getChatCompletions(anyString(), optionsCaptor.capture()))
                .thenReturn(Mono.just(completions));

        var service = new AnswerGeneratingService(client, "test-deployment");
        var userQuery = "Test query";

        // Act
        var result = service.answerQuery(userQuery);

        // Assert
        assertEquals(List.of("Test answer"), result);

        // Verify the message sent matches the query
        var sentOptions = optionsCaptor.getValue();
        var messages = sentOptions.getMessages();
        assertEquals(1, messages.size());
        assertInstanceOf(ChatRequestUserMessage.class, messages.get(0));
        assertEquals(userQuery, ((ChatRequestUserMessage) messages.get(0)).getContent().toString());
    }

    @Test
    void answerQuery_returnsEmptyListWhenNoChoices() {
        // Arrange
        var client = mock(OpenAIAsyncClient.class);
        var completions = mock(ChatCompletions.class);
        when(completions.getChoices()).thenReturn(List.of());
        when(client.getChatCompletions(anyString(), any(ChatCompletionsOptions.class)))
                .thenReturn(Mono.just(completions));

        var service = new AnswerGeneratingService(client, "test-deployment");

        // Act
        var result = service.answerQuery("Test query");

        // Assert
        assertTrue(result.isEmpty());
    }
}

