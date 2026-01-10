package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.bot.facade.MenuFacade;
import com.example.telegrambot.service.NoteService;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Order(1)
public class MenuCommandHandler extends SlashCommandHandler {

    private final MenuFacade menuFacade;

    public MenuCommandHandler(MenuFacade menuFacade, NoteService noteService, TelegramBotSender sender) {
        super("menu", noteService, sender);
        this.menuFacade = menuFacade;
    }

    @Override
    protected void handleCommand(Update update) {
        menuFacade.sendMenu(update.getMessage().getChatId());
    }
}
