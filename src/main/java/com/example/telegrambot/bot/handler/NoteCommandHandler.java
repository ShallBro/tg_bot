package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.service.ExtractIdService;
import com.example.telegrambot.service.NoteService;
import com.example.telegrambot.utils.CommandPayloadExtractor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Order(3)
public class NoteCommandHandler implements UpdateHandler {

    private final NoteService noteService;
    private final TelegramBotSender sender;
    private final ExtractIdService extractService;

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

        Long id = extractService.extract(msg, "/note");

        noteService.findNote(chatId, id)
                .ifPresentOrElse(
                        note -> sender.sendText(chatId,
                                "🧠 Заметка #" + note.getId() + "\n\n" + note.getText()
                        ),
                        () -> sender.sendText(chatId,
                                "❌ Заметка с ID " + id + " не найдена"
                        )
                );
    }
}
