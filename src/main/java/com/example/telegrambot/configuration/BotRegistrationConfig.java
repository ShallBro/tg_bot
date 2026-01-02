package com.example.telegrambot.configuration;

import com.example.telegrambot.bot.NotesBot;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Configuration
@RequiredArgsConstructor
@Slf4j
public class BotRegistrationConfig {
    private final NotesBot bot;

    @PostConstruct
    public void register() {
        try {
            TelegramBotsApi api = new TelegramBotsApi(DefaultBotSession.class);
            api.registerBot(bot);
            log.info("Bot registered in TelegramBotsApi");
        } catch (Exception e) {
            throw new RuntimeException("Failed to register bot", e);
        }
    }
}
