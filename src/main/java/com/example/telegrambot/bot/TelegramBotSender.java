package com.example.telegrambot.bot;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.DefaultAbsSender;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Component
@RequiredArgsConstructor
public class TelegramBotSender {
    private final DefaultAbsSender telegramSender;

    public void sendMarkdown(Long chatId, String markdown) {
        send(chatId, markdown, ParseMode.MARKDOWN);
    }

    public void sendText(Long chatId, String text) {
        send(chatId, text, null);
    }

    private void send(Long chatId, String text, String parseMode) {
        try {
            SendMessage.SendMessageBuilder builder = SendMessage.builder()
                    .chatId(chatId.toString())
                    .text(text);

            if (parseMode != null) {
                builder.parseMode(parseMode);
            }

            telegramSender.execute(builder.build());
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
}
