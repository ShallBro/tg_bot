package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.service.ExtractIdService;
import com.example.telegrambot.service.NoteService;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Order(6)
public class DeleteCommandHandler extends SlashCommandHandler {

    private final NoteService noteService;
    private final TelegramBotSender sender;
    private final ExtractIdService extractService;

    public DeleteCommandHandler(NoteService noteService,
                                TelegramBotSender sender,
                                ExtractIdService extractService) {
        super("delete", noteService, sender);
        this.noteService = noteService;
        this.sender = sender;
        this.extractService = extractService;
    }

    @Override
    protected void handleCommand(Update update) {
        var msg = update.getMessage();
        Long chatId = msg.getChatId();

        Long id = extractService.extract(msg, command());
        if (id == null) {
            return;
        }

        if (noteService.delete(chatId, id)) {
            sender.sendText(chatId, "✅ Заметка удалена");
        }
    }
}
