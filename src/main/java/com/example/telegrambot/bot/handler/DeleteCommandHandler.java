package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.service.ExtractIdService;
import com.example.telegrambot.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Order(6)
public class DeleteCommandHandler implements UpdateHandler {

    private final NoteService noteService;
    private final TelegramBotSender sender;
    private final ExtractIdService extractService;

    @Override
    public boolean supports(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && update.getMessage().getText().startsWith("/delete");
    }

    @Override
    public void handle(Update update) {
        var msg = update.getMessage();
        Long chatId = msg.getChatId();

        Long id = extractService.extract(msg, "/delete");

        if (noteService.delete(chatId, id)) {
            sender.sendText(chatId, "✅ Заметка удалена");
        }
    }
}
