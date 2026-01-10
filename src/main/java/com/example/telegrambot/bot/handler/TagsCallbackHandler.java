package com.example.telegrambot.bot.handler;

import com.example.telegrambot.bot.TelegramBotSender;
import com.example.telegrambot.bot.callbacks.TagCallbacks;
import com.example.telegrambot.bot.facade.TagListFacade;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Order;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Update;

@Component
@Order(8)
@RequiredArgsConstructor
public class TagsCallbackHandler implements UpdateHandler {

    private final TagListFacade tagListFacade;
    private final TelegramBotSender sender;

    @Override
    public boolean supports(Update update) {
        return update.hasCallbackQuery()
                && update.getCallbackQuery().getData() != null
                && update.getCallbackQuery().getData().startsWith("tags|");
    }

    @Override
    public void handle(Update update) {
        var callbackQuery = update.getCallbackQuery();
        sender.answerCallback(callbackQuery.getId());

        int page = TagCallbacks.parseTagsPage(callbackQuery.getData())
                .orElse(0);

        tagListFacade.editPage(
                callbackQuery.getMessage().getChatId(),
                callbackQuery.getMessage().getMessageId(),
                page
        );
    }
}
