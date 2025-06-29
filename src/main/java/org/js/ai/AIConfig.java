package org.js.ai;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Configuration;


@Configuration
public class AIConfig {

    public ChatClient getChatClient() {
        return chatClient;
    }
    private final ChatClient chatClient;

    public AIConfig( ChatClient.Builder builder, OpenAiChatModel model) {

        OpenAiChatOptions openAiChatOptions = OpenAiChatOptions.builder()
                .model("gpt-4o")
                .temperature(0.2)
                .frequencyPenalty(0.5)      // OpenAI-specific parameter
                .presencePenalty(0.3)       // OpenAI-specific parameter
                //.responseFormat(new ResponseFormat("json_object"))  // OpenAI-specific JSON mode
                .seed(42)                   // OpenAI-specific deterministic generation
                .build();
        chatClient = builder.defaultOptions(openAiChatOptions).build();

}
}