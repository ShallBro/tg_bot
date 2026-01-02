package com.example.telegrambot.configuration;

import com.example.telegrambot.bot.BotProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.bots.DefaultBotOptions;

@SuppressWarnings("deprecation")
@Configuration
public class TelegramSenderConfig {

    @Bean
    public DefaultAbsSender telegramSender(BotProperties props) {
        DefaultBotOptions options = new DefaultBotOptions();

        return new DefaultAbsSender(options) {
            @Override
            public String getBotToken() {
                return props.token();
            }
        };
    }
}
