package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.service.NoteService;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

@Component
@Order(4)
public class SearchCommandHandler extends SlashCommandHandler {

    private final NoteService noteService;
    private final TelegramBotSender sender;

    public SearchCommandHandler(NoteService noteService, TelegramBotSender sender) {
        super("search", noteService, sender);
        this.noteService = noteService;
        this.sender = sender;
    }

    @Override
    protected void handleCommand(Update update) {
        var msg = update.getMessage();
        Long chatId = msg.getChatId();

        Optional<String> text = extractPayload(update);

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
