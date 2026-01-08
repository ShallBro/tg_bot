package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.bot.message.BotMessageService;
import com.example.telegrambot.bot.view.TagPagingFacade;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Order(11)
@Component
public class TagCommandHandler extends SlashCommandHandler {

    private final TelegramBotSender sender;
    private final BotMessageService messages;
    private final TagPagingFacade tagPagingFacade;

    public TagCommandHandler(TelegramBotSender sender,
                             BotMessageService messages,
                             TagPagingFacade tagPagingFacade) {
        super("tag");
        this.sender = sender;
        this.messages = messages;
        this.tagPagingFacade = tagPagingFacade;
    }

    @Override
    protected void handleCommand(Update update) {
        var msg = update.getMessage();
        Long chatId = msg.getChatId();

        var payload = extractPayload(update);
        if (payload.isEmpty()) {
            sender.sendMarkdown(chatId, messages.text("tag.command.missing"), null);
            return;
        }

        String tag = normalizeTag(payload.get());
        tagPagingFacade.sendFirstPage(chatId, tag);
    }

    private String normalizeTag(String raw) {
        String t = raw.trim();
        if (t.startsWith("#")) {
            t = t.substring(1);
        }
        return t.toLowerCase();
    }
}
