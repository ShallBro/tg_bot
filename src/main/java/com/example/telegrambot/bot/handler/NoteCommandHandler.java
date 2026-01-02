package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.service.NoteService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
public class NoteCommandHandler implements UpdateHandler {

    private final NoteService noteService;
    private final TelegramBotSender sender;

    @Override
    public boolean supports(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && update.getMessage().getText().startsWith("/note");
    }

    @Override
    public void handle(Update update) {
        var msg = update.getMessage();
        Long chatId = msg.getChatId();
        Long messageId = msg.getMessageId().longValue();

        String payload = msg.getText()
                .replaceFirst("^/note(@\\w+)?\\s*", "")
                .trim();

        if (!payload.isBlank()) {
            noteService.saveTextNote(chatId, messageId, payload);
        }

        sender.send(chatId, payload);
    }
}
