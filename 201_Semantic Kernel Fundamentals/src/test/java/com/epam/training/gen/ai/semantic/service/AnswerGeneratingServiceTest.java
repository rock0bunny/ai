package com.epam.training.gen.ai.semantic.service;

import com.microsoft.semantickernel.Kernel;
import com.microsoft.semantickernel.orchestration.FunctionInvocation;
import com.microsoft.semantickernel.orchestration.FunctionResult;
import com.microsoft.semantickernel.orchestration.InvocationContext;
import org.junit.jupiter.api.Test;

import static org.mockito.Mockito.*;

class AnswerGeneratingServiceTest {

    @Test
    void answerQuery_callsKernelWithCorrectQuery() {
        Kernel kernel = mock(Kernel.class);
        InvocationContext invocationContext = mock(InvocationContext.class);

        FunctionInvocation<String> invocation = mock(FunctionInvocation.class);
        when(kernel.<String>invokePromptAsync("my test query")).thenReturn(invocation);
        when(invocation.withInvocationContext(invocationContext)).thenReturn(invocation);
        when(invocation.withResultType(String.class)).thenReturn(invocation);
        FunctionResult<String> result = mock(FunctionResult.class);
        when(invocation.block()).thenReturn(result);
        when(result.getResult()).thenReturn("test answer");

        AnswerGeneratingService service = new AnswerGeneratingService(kernel, invocationContext);

        // Act
        service.answerQuery("my test query");

        // Assert
        verify(kernel).invokePromptAsync("my test query");
        verify(invocation).withInvocationContext(invocationContext);
    }
}