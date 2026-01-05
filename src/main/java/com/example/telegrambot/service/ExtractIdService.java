package com.example.telegrambot.service;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.utils.CommandPayloadExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

@Service
@RequiredArgsConstructor
public class ExtractIdService {

    private final TelegramBotSender sender;

    public Long extract(Message msg, String command) {
        Optional<String> text = CommandPayloadExtractor.extract(msg.getText(), command);

        if (text.isEmpty()) {
            sender.sendText(msg.getChatId(), """
                ❌ Укажи ID заметки
                
                Пример:
                /note 12
                """);
            return null;
        }

        long id;
        try {
            id = Long.parseLong(text.get());
        } catch (NumberFormatException e) {
            sender.sendText(msg.getChatId(), "❌ ID должен быть числом");
            return null;
        }
        return id;
    }
}
