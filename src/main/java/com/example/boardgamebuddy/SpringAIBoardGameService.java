package com.example.boardgamebuddy;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.metadata.Usage;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

@Service
public class SpringAIBoardGameService implements BoardGameService {

    private static final Logger log = LoggerFactory.getLogger(SpringAIBoardGameService.class);

    private final ChatClient chatClient;
    private final GameRulesService gameRulesService;

    public SpringAIBoardGameService(ChatClient.Builder chatClientBuilder, GameRulesService grs) {
        this.chatClient = chatClientBuilder.build();
        this.gameRulesService = grs;
    }

    @Value("classpath:/promptTemplates/systemPromptTemplate.st")
    Resource promptTemplate;

    @Override
    public Answer askQuestion(Question question) {
        var gameRules = gameRulesService.getRulesFor(question.gameTitle());

        var responseEntity = chatClient.prompt()
            .system(systemSpec -> systemSpec
                    .text(promptTemplate)
                    .param("gameTitle", question.gameTitle())
                    .param("rules", gameRules))
            .user(question.question())
            .call()
            .responseEntity(Answer.class);

        var response = responseEntity.response();

        var metadata = response.getMetadata();
        logUsage(metadata.getUsage());

        return responseEntity.entity();
    }

    private void logUsage(Usage usage) {
        log.info("Token usage: prompt={}, generation={}, total={}",
                usage.getPromptTokens(),
                usage.getCompletionTokens(),
                usage.getTotalTokens());  
    }
}