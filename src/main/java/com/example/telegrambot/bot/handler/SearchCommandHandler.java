package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.service.NoteService;
import com.example.telegrambot.utils.CommandPayloadExtractor;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
@RequiredArgsConstructor
@Order(4)
public class SearchCommandHandler implements UpdateHandler {

    private final NoteService noteService;
    private final TelegramBotSender sender;

    @Override
    public boolean supports(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && update.getMessage().getText().startsWith("/search");
    }

    @Override
    public void handle(Update update) {
        var msg = update.getMessage();
        Long chatId = msg.getChatId();

        Optional<String> text = CommandPayloadExtractor.extract(msg.getText(), "/search");

        if (text.isEmpty()) {
            sender.sendText(chatId, "❌ Укажи ключевое слово.\nПример: /note liquibase");
            return;
        }

        var notes = noteService.findNotes(chatId, text.get());

        if (notes.isEmpty()) {
            sender.sendText(chatId, "🔎 Ничего не найдено по: " + text);
            return;
        }

        String response = notes.stream()
                .map(note -> "• [" + note.getId() + "] " + note.getText())
                .reduce("🔎 Нашёл:\n", (acc, line) -> acc + line + "\n");

        sender.sendText(chatId, response);
    }
}
