package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.bot.callbacks.CallbackCodec;
import com.example.telegrambot.bot.callbacks.CallbackCodecRegistry;
import com.example.telegrambot.bot.callbacks.CallbackType;
import com.example.telegrambot.bot.callbacks.MenuAction;
import com.example.telegrambot.bot.facade.MenuFacade;
import com.example.telegrambot.bot.facade.TagListFacade;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Order(7)
public class MenuCallbackHandler implements UpdateHandler {

    private final TelegramBotSender sender;
    private final HelpCommandHandler helpCommandHandler;
    private final LastCommandHandler lastCommandHandler;
    private final MenuFacade menuFacade;
    private final TagListFacade tagListFacade;
    private final CallbackCodec<MenuAction> menuCallbackCodec;

    public MenuCallbackHandler(TelegramBotSender sender,
                               HelpCommandHandler helpCommandHandler,
                               LastCommandHandler lastCommandHandler,
                               MenuFacade menuFacade,
                               TagListFacade tagListFacade,
                               CallbackCodecRegistry registry) {
        this.sender = sender;
        this.helpCommandHandler = helpCommandHandler;
        this.lastCommandHandler = lastCommandHandler;
        this.menuFacade = menuFacade;
        this.tagListFacade = tagListFacade;
        this.menuCallbackCodec = registry.get(CallbackType.MENU);
    }

    @Override
    public boolean supports(Update update) {
        return update.hasCallbackQuery()
                && update.getCallbackQuery().getData() != null
                && update.getCallbackQuery().getData().startsWith("menu|");
    }

    @Override
    public void handle(Update update) {
        var callbackQuery = update.getCallbackQuery();
        sender.answerCallback(callbackQuery.getId());

        Message message = resolveMessage(callbackQuery.getMessage());
        if (message == null) {
            return;
        }

        menuCallbackCodec.decode(callbackQuery.getData())
                .ifPresent(action -> {
                    Long chatId = message.getChatId();
                    switch (action) {
                        case LAST -> lastCommandHandler.sendLast(chatId);
                        case HELP -> helpCommandHandler.sendHelp(chatId);
                        case TAGS -> tagListFacade.sendPage(chatId, 0);
                        case BACK -> {
                            sender.deleteMessage(chatId, message.getMessageId());
                            menuFacade.sendMenu(chatId);
                        }
                    }
                });
    }

    private Message resolveMessage(MaybeInaccessibleMessage message) {
        if (message instanceof Message accessible) {
            return accessible;
        }
        return null;
    }
}
