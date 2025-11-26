package com.epam.training.gen.ai.semantic.service;

import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.services.chatcompletion.ChatCompletionService;
import com.microsoft.semantickernel.services.chatcompletion.ChatHistory;
import com.microsoft.semantickernel.services.chatcompletion.ChatMessageContent;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.boot.test.mock.mockito.SpyBean;
import org.springframework.test.web.servlet.MockMvc;
import reactor.core.publisher.Mono;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.any;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@AutoConfigureMockMvc
class AnswerGeneratingServiceIntegrationTest {

    @Autowired
    private MockMvc mockMvc;

    @SpyBean
    private Kernel kernel;

    @MockBean
    private ChatCompletionService chatCompletionService;

    @Test
    void askAI_endpoint_triggersChatCompletion_andKernelPluginsPresent() throws Exception {
        // Mock the chatCompletionService to return a test answer
        ChatMessageContent mockContent = Mockito.mock(ChatMessageContent.class);
        Mockito.when(mockContent.getContent()).thenReturn("test answer");
        Mockito.when(chatCompletionService.getChatMessageContentsAsync(any(ChatHistory.class), any(), any()))
                .thenReturn(Mono.just(List.of(mockContent)));

        var result = mockMvc.perform(post("/ai/ask")
                        .param("query", "test query"))
                .andExpect(status().isOk())
                .andReturn();

        assertThat(result.getResponse().getContentAsString()).isEqualTo("test answer");

        assertThat(kernel.getPlugins())
                .isNotEmpty();
    }
}