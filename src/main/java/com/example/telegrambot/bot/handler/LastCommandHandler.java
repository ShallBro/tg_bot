package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.entity.Note;
import com.example.telegrambot.service.NoteService;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Order(2)
public class LastCommandHandler extends SlashCommandHandler {

    private final NoteService noteService;
    private final TelegramBotSender sender;

    public LastCommandHandler(NoteService noteService, TelegramBotSender sender) {
        super("last");
        this.noteService = noteService;
        this.sender = sender;
    }

    @Override
    protected void handleCommand(Update update) {
        Long chatId = update.getMessage().getChatId();
        Note last = noteService.last(chatId);

        String text = last == null
                ? "Пока нет заметок"
                : last.getText();

        sender.sendText(chatId, text);
    }
}
