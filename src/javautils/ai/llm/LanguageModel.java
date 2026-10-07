package javautils.ai.llm;

import java.util.List;
import java.util.concurrent.CompletableFuture;

public interface LanguageModel {
    String complete(String prompt);
    String complete(List<Message> messages);

    default String complete(String systemPrompt, String userPrompt) {
        List<Message> messages = List.of(
            new Message(Message.Role.SYSTEM, systemPrompt),
            new Message(Message.Role.USER, userPrompt)
        );
        return complete(messages);
    }

    CompletableFuture<String> completeAsync(String prompt);

    void setTemperature(double temperature);
    void setMaxTokens(int maxTokens);

    void setModel(String modelName);
    String getModel();
}
