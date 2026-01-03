package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.service.NoteService;
import com.example.telegrambot.utils.CommandPayloadExtractor;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

import java.util.Optional;

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

        Optional<String> text = CommandPayloadExtractor.extract(msg.getText(), "/note");

        if (text.isEmpty()) {
            sender.send(chatId, """
                ❌ Укажи ID заметки
                
                Пример:
                /note 12
                """);
            return;
        }

        long id;
        try {
            id = Long.parseLong(text.get());
        } catch (NumberFormatException e) {
            sender.send(chatId, "❌ ID должен быть числом");
            return;
        }

        noteService.findNote(chatId, id)
                .ifPresentOrElse(
                        note -> sender.send(chatId,
                                "🧠 Заметка #" + note.getId() + "\n\n" + note.getText()
                        ),
                        () -> sender.send(chatId,
                                "❌ Заметка с ID " + id + " не найдена"
                        )
                );
    }
}
