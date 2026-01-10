package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.service.NoteService;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Order(1)
public class HelpCommandHandler extends SlashCommandHandler {

    private final TelegramBotSender sender;

    public HelpCommandHandler(TelegramBotSender sender, NoteService noteService) {
        super("help", noteService, sender);
        this.sender = sender;
    }

    @Override
    protected void handleCommand(Update update) {
        sendHelp(update.getMessage().getChatId());
    }

    public void sendHelp(Long chatId) {
        String text = """
                🧠 *MegaBrain* — твой личный бот для Telegram

                *Как сохранять заметки*
                Просто отправь текст в чат — он сохранится как заметка.
                Добавляй хештеги: #java #работа #идеи

                *Команды*
                • /last — последняя заметка
                • /note <id> — открыть заметку по ID
                • /search <текст> — поиск по заметкам
                • /tag <тег> — заметки по тегу
                • /tags — список тегов
                • /delete <id> — удалить заметку
                • /help — эта справка
                • /menu — меню с основными командами

                *Примеры*
                • /search liquibase
                • /tag java
                • /note 12
                • /delete 12
                """;

        sender.sendMarkdown(chatId, text, null);
    }
}
