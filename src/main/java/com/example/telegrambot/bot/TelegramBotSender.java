package com.example.telegrambot.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@RequiredArgsConstructor
public class TelegramBotSender {
    private final DefaultAbsSender telegramSender;

    public void send(Long chatId, String text) {
        try {
            telegramSender.execute(SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(text)
                    .build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

}
