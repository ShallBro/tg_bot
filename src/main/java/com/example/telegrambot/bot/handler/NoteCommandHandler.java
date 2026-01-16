package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.entity.NoteAttachment;
import com.example.telegrambot.service.ExtractIdService;
import com.example.telegrambot.service.NoteService;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.List;

@Component
@Order(3)
public class NoteCommandHandler extends SlashCommandHandler {

    private final NoteService noteService;
    private final TelegramBotSender sender;
    private final ExtractIdService extractService;

    public NoteCommandHandler(NoteService noteService,
                              TelegramBotSender sender,
                              ExtractIdService extractService) {
        super("note", noteService, sender);
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

        sendNote(chatId, id);
    }

    public void sendNote(Long chatId, Long id) {
        noteService.findNote(chatId, id)
                .ifPresentOrElse(
                        note -> {
                            if (note.getText() != null && !note.getText().isBlank()) {
                                sender.sendText(chatId, "Заметка #" + note.getId() + "\n\n" + note.getText());
                            } else {
                                sender.sendText(chatId, "Заметка #" + note.getId());
                            }
                            sendAttachments(chatId, note.getId());
                        },
                        () -> sender.sendText(chatId, "❌ Заметка с ID " + id + " не найдена")
                );
    }
}
