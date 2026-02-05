package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.bot.message.BotMessageService;
import com.example.telegrambot.entity.Note;
import com.example.telegrambot.service.EditStateService;
import com.example.telegrambot.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Order(6)
public class TextNoteHandler implements UpdateHandler {

    private final NoteService noteService;
    private final TelegramBotSender sender;
    private final EditStateService editStateService;
    private final BotMessageService messages;

    @Override
    public boolean supports(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && !update.getMessage().getText().startsWith("/");
    }

    @Override
    public void handle(Update update) {
        var msg = update.getMessage();
        Long chatId = msg.getChatId();

        var editingNoteId = editStateService.get(chatId);
        if (editingNoteId.isPresent()) {
            Long noteId = editingNoteId.get();
            boolean updated = noteService.updateTextNote(chatId, noteId, msg.getText());
            editStateService.cancel(chatId);
            if (updated) {
                sender.sendText(chatId, messages.text("note.edit.updated", noteId));
            } else {
                sender.sendText(chatId, messages.text("note.edit.not_found", noteId));
            }
            return;
        }

        Note note = noteService.saveTextNote(
                chatId,
                msg.getMessageId().longValue(),
                msg.getText()
        );
        sender.sendText(chatId, "✅ Заметка сохранена id: %s".formatted(note.getId()));
    }
}

