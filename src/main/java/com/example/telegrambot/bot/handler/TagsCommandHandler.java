package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.bot.facade.TagListFacade;
import com.example.telegrambot.service.NoteService;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Order(9)
@Component
public class TagsCommandHandler extends SlashCommandHandler {

    private final TagListFacade tagListFacade;

    public TagsCommandHandler(TagListFacade tagListFacade, NoteService noteService, TelegramBotSender sender) {
        super("tags", noteService, sender);
        this.tagListFacade = tagListFacade;
    }

    @Override
    protected void handleCommand(Update update) {
        Long chatId = update.getMessage().getChatId();
        int page = resolvePage(extractPayload(update).orElse(null));
        tagListFacade.sendPage(chatId, page);
    }

    private int resolvePage(String payload) {
        if (payload == null || payload.isBlank()) {
            return 0;
        }

        try {
            int pageNumber = Integer.parseInt(payload.trim());
            return Math.max(pageNumber - 1, 0);
        } catch (NumberFormatException e) {
            return 0;
        }
    }
}
