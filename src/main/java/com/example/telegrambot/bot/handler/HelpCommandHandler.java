package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@RequiredArgsConstructor
@Order(1)
public class HelpCommandHandler implements UpdateHandler {

    private final TelegramBotSender sender;

    @Override
    public boolean supports(Update update) {
        return update.hasMessage()
                && update.getMessage().hasText()
                && update.getMessage().getText().startsWith("/help");
    }

    @Override
    public void handle(Update update) {
        Long chatId = update.getMessage().getChatId();

        String text = """
                🧠 *MegaBrain* — твой второй мозг в Telegram
                
                *Как сохранять заметки*
                Просто отправь текст — я сохраню его как заметку.
                Можно добавлять теги: #java #работа #идеи
                
                *Команды*
                • /last — последняя заметка
                • /note <id> — открыть заметку по ID
                • /search <текст> — поиск по заметкам
                • /tag <тег> — заметки по тегу
                • /tags — список тегов
                • /delete <id> — удалить заметку
                • /help — эта справка
                
                *Примеры*
                • купить молоко #дом
                • /search liquibase
                • /tag java
                • /note 12
                • /delete 12
                """;

        sender.sendMarkdown(chatId, text);
    }
}
