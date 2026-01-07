package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Order(12)
public class UnknownCommandHandler implements UpdateHandler {

    private final TelegramBotSender sender;

    @Override
    public boolean supports(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && update.getMessage().getText().startsWith("/");
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        sender.sendText(chatId, "❓ Не понял команду. Напиши /help");
    }
}
