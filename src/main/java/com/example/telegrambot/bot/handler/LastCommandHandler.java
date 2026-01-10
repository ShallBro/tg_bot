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
        super("last", noteService, sender);
        this.noteService = noteService;
        this.sender = sender;
    }

    @Override
    protected void handleCommand(Update update) {
        sendLast(update.getMessage().getChatId());
    }

    public void sendLast(Long chatId) {
        Note last = noteService.last(chatId);
        if (last == null) {
            sender.sendText(chatId, "Нет сохраненных заметок.");
            return;
        }

        if (last.getText() != null && !last.getText().isBlank()) {
            sender.sendText(chatId, last.getText());
        }

        sendAttachments(chatId, last.getId());
    }
}
