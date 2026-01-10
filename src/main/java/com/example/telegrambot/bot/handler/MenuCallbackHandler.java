package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.bot.callbacks.MenuCallbacks;
import com.example.telegrambot.bot.facade.TagListFacade;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.MaybeInaccessibleMessage;
import org.telegram.telegrambots.meta.api.objects.Message;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Order(7)
@RequiredArgsConstructor
public class MenuCallbackHandler implements UpdateHandler {

    private final TelegramBotSender sender;
    private final HelpCommandHandler helpCommandHandler;
    private final LastCommandHandler lastCommandHandler;
    private final TagListFacade tagListFacade;

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

        Long chatId = resolveChatId(callbackQuery.getMessage());
        if (chatId == null) {
            return;
        }

        MenuCallbacks.parse(callbackQuery.getData())
                .ifPresent(action -> {
                    switch (action) {
                        case LAST -> lastCommandHandler.sendLast(chatId);
                        case HELP -> helpCommandHandler.sendHelp(chatId);
                        case TAGS -> tagListFacade.sendPage(chatId, 0);
                    }
                });
    }

    private Long resolveChatId(MaybeInaccessibleMessage message) {
        if (message instanceof Message accessible) {
            return accessible.getChatId();
        }
        return null;
    }
}
