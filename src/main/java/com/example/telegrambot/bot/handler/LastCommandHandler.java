package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.entity.Note;
import com.example.telegrambot.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class LastCommandHandler implements UpdateHandler {

    private final NoteService noteService;
    private final TelegramBotSender sender;

    @Override
    public boolean supports(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && update.getMessage().getText().startsWith("/last");
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();
        Note last = noteService.last(chatId);

        String text = last == null
                ? "Пока нет заметок"
                : last.getText();

        sender.send(chatId, text);
    }
}
