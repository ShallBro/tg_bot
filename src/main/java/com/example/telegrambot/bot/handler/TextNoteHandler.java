package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.entity.Note;
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

    @Override
    public boolean supports(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && !update.getMessage().getText().startsWith("/");
    }

    @Override
    public void handle(Update update) {
        var msg = update.getMessage();
        Note note = noteService.saveTextNote(
                msg.getChatId(),
                msg.getMessageId().longValue(),
                msg.getText()
        );
        sender.sendText(msg.getChatId(), "✅ Заметка сохранена id: %s".formatted(note.getId()));
    }
}
